package com.example.coreservice.service.ai;

import com.example.coreservice.dto.request.AIChatRequest;
import com.example.coreservice.dto.request.ChatMessageDto;
import com.example.coreservice.dto.response.ChatResponse;
import com.example.coreservice.entity.auth.User;
import com.example.coreservice.entity.content.Note;
import com.example.coreservice.enums.ErrorCode;
import com.example.coreservice.exception.AppException;
import com.example.coreservice.repository.content.NoteRepository;
import com.example.coreservice.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIChatService {

    private final WebClient aiWebClient;
    private final NoteRepository noteRepository;
    private final SecurityUtils securityUtils;

    @Value("${app.ai-service.internal-token}")
    private String internalToken;

    public ChatResponse chat(Long noteId, AIChatRequest request) {

        Note note = getNoteWithSecurity(noteId);

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new AppException(ErrorCode.AI_INVALID_INPUT);
        }

        try {

            Map<String, Object> response = aiWebClient.post()
                    .uri("/chat")
                    .header("X-ALCS-Internal-Token", internalToken)
                    .bodyValue(Map.of(
                            "note", note.getContent(),
                            "message", request.getMessage(),
                            "history", buildHistory(request.getHistory())
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || response.get("answer") == null) {
                throw new AppException(ErrorCode.AI_EMPTY_RESULT);
            }

            return ChatResponse.builder()
                    .answer(response.get("answer").toString())
                    .build();

        } catch (WebClientResponseException e) {

            log.error(
                    "AI HTTP error ({}): {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );

            throw new AppException(ErrorCode.AI_SERVICE_ERROR);

        } catch (Exception e) {

            log.error("AI chat failed: {}", e.getMessage());

            throw new AppException(ErrorCode.AI_SERVICE_ERROR);
        }
    }

    private String buildHistory(List<ChatMessageDto> history) {

        if (history == null || history.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (ChatMessageDto msg : history) {

            sb.append(msg.getRole())
                    .append(": ")
                    .append(msg.getContent())
                    .append("\n");
        }

        return sb.toString();
    }

    private Note getNoteWithSecurity(Long noteId) {

        User user = securityUtils.getCurrentUser();

        return noteRepository.findByIdAndUser(noteId, user)
                .orElseThrow(() -> new AppException(ErrorCode.NOTE_NOT_FOUND));
    }
}