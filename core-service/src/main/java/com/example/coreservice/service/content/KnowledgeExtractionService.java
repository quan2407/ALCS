package com.example.coreservice.service.content;

import com.example.coreservice.dto.response.AIExtractionResponse;
import com.example.coreservice.entity.content.KnowledgeAtom;
import com.example.coreservice.entity.content.Note;
import com.example.coreservice.enums.ErrorCode;
import com.example.coreservice.exception.AppException;
import com.example.coreservice.mapper.content.KnowledgeAtomMapper;
import com.example.coreservice.repository.content.KnowledgeAtomRepository;
import com.example.coreservice.repository.content.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

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

    @Value("${app.ai-service.internal-token}")
    private String internalToken;

    @Transactional
    public void extractAtomsFromNote(Long noteId){
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTE_NOT_FOUND));
        log.info("Bắt đầu trích xuất Atoms cho Note: {}", note.getTitle());
        atomRepository.deleteAllByNote(note);
        aiWebClient.post()
                .uri("/analyze")
                .header("X-ALCS-Internal-Token", internalToken)
                .bodyValue(Map.of("content", note.getContent()))
                .retrieve()
                .bodyToFlux(AIExtractionResponse.class)
                .collectList()
                .subscribe(responses -> {
                    List<KnowledgeAtom> atoms = responses.stream()
                            .map(res -> {
                                KnowledgeAtom atom = atomMapper.toEntity(res);
                                atom.setNote(note); // Gán Note cha
                                atom.setReviewLevel(1L); // Gán level khởi tạo
                                return atom;
                            })
                            .collect(Collectors.toList());

                    atomRepository.saveAll(atoms);

                    note.setAtomCount((long) atoms.size());
                    noteRepository.save(note);

                    log.info("Hoàn thành trích xuất: {} atoms đã được tạo.", atoms.size());
                }, error -> {
                    log.error("Lỗi khi gọi AI Service: {}", error.getMessage());
                });
    }
}
