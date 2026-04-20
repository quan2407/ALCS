package com.example.coreservice.service.content;

import com.example.coreservice.dto.response.AIExtractionResponse;
import com.example.coreservice.entity.auth.User;
import com.example.coreservice.entity.content.KnowledgeAtom;
import com.example.coreservice.entity.content.Note;
import com.example.coreservice.enums.ErrorCode;
import com.example.coreservice.enums.ProcessingStatus;
import com.example.coreservice.exception.AppException;
import com.example.coreservice.mapper.content.KnowledgeAtomMapper;
import com.example.coreservice.repository.content.KnowledgeAtomRepository;
import com.example.coreservice.repository.content.NoteRepository;
import com.example.coreservice.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeExtractionService {

    private final WebClient aiWebClient;
    private final NoteRepository noteRepository;
    private final KnowledgeAtomRepository atomRepository;
    private final KnowledgeAtomMapper atomMapper;
    private final SecurityUtils securityUtils;

    @Value("${app.ai-service.internal-token}")
    private String internalToken;

    @Transactional
    public void extractAtomsFromNote(Long noteId) {
        Note note = getNoteWithSecurity(noteId);

        if (note.getContent() == null || note.getContent().isBlank()) {
            throw new AppException(ErrorCode.AI_INVALID_INPUT);
        }

        note.setProcessingStatus(ProcessingStatus.PROCESSING);
        noteRepository.save(note);

        log.info("Start extracting atoms for noteId={}", noteId);

        try {
            List<AIExtractionResponse> responses = callAIWithRetry(note);

            if (responses == null || responses.isEmpty()) {
                throw new AppException(ErrorCode.AI_EMPTY_RESULT);
            }

            List<KnowledgeAtom> atoms = mapToAtoms(responses, note);

            saveAtomsSafely(note, atoms);

            updateMetrics(note, atoms);

            note.setProcessingStatus(ProcessingStatus.DONE);

            log.info("Finished extracting {} atoms for noteId={}", atoms.size(), noteId);

        } catch (Exception e) {
            note.setProcessingStatus(ProcessingStatus.FAILED);
            throw e;

        } finally {
            // 🔥 4. LUÔN SAVE LẠI TRẠNG THÁI
            noteRepository.save(note);
        }
    }

    private List<KnowledgeAtom> mapToAtoms(List<AIExtractionResponse> responses, Note note) {
        return responses.stream()
                .map(res -> {
                    KnowledgeAtom atom = atomMapper.toEntity(res);
                    atom.setNote(note);
                    atom.setReviewLevel(1L);
                    return atom;
                })
                .toList();
    }
    private void saveAtomsSafely(Note note, List<KnowledgeAtom> atoms) {
        atomRepository.deleteAllByNote(note);
        atomRepository.saveAll(atoms);
    }
    private void updateMetrics(Note note, List<KnowledgeAtom> atoms) {
        long atomCount = atoms.size();
        long wordCount = note.getWordCount() != null ? note.getWordCount() : 0;

        note.setAtomCount(atomCount);

        if (wordCount > 0) {
            note.setKnowledgeDensity((double) atomCount / wordCount);
        }

        noteRepository.save(note);
    }
    private List<AIExtractionResponse> callAIWithRetry(Note note) {
        int maxRetry = 3;

        for (int i = 1; i <= maxRetry; i++) {
            try {
                return aiWebClient.post()
                        .uri("/analyze")
                        .header("X-ALCS-Internal-Token", internalToken)
                        .bodyValue(Map.of("content", note.getContent()))
                        .retrieve()
                        .bodyToFlux(AIExtractionResponse.class)
                        .collectList()
                        .block();

            } catch (WebClientResponseException e) {
                log.error("AI HTTP error ({}): {}", e.getStatusCode(), e.getResponseBodyAsString());

                throw new AppException(ErrorCode.AI_SERVICE_ERROR);

            } catch (Exception e) {
                log.error("AI call failed (attempt {}): {}", i, e.getMessage());

                if (i == maxRetry) {
                    throw new AppException(ErrorCode.AI_RETRY_FAILED);
                }

                try {
                    Thread.sleep(1000 * i);
                } catch (InterruptedException ignored) {}
            }
        }

        throw new AppException(ErrorCode.AI_SERVICE_ERROR);
    }

    private Note getNoteWithSecurity(Long noteId) {
        User user = securityUtils.getCurrentUser();
        return noteRepository.findByIdAndUser(noteId,user)
                .orElseThrow(() -> new AppException(ErrorCode.NOTE_NOT_FOUND));
    }
}
