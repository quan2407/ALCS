package com.example.coreservice.service.ai;

import com.example.coreservice.dto.request.ChatRequest;
import com.example.coreservice.dto.response.ChatResponse;
import com.example.coreservice.entity.content.Note;
import com.example.coreservice.enums.ErrorCode;
import com.example.coreservice.exception.AppException;
import com.example.coreservice.repository.content.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteChatService {

    private final NoteRepository noteRepository;

    public ChatResponse chat(Long noteId, ChatRequest request) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTE_NOT_FOUND));

        String answer = """
                This is a fake AI response.

                Your question:
                %s

                Related note:
                %s
                """.formatted(
                request.getMessage(),
                note.getTitle()
        );

        return ChatResponse.builder()
                .answer(answer)
                .build();
    }
}