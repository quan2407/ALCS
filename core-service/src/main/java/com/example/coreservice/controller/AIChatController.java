package com.example.coreservice.controller;

import com.example.coreservice.dto.request.AIChatRequest;
import com.example.coreservice.dto.response.ApiResponse;
import com.example.coreservice.dto.response.ChatResponse;
import com.example.coreservice.service.ai.AIChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class AIChatController {

    private final AIChatService aiChatService;

    @PostMapping("/{noteId}/chat")
    public ApiResponse<ChatResponse> chat(
            @PathVariable Long noteId,
            @RequestBody AIChatRequest request
    ) {

        ChatResponse data = aiChatService.chat(noteId, request);

        return ApiResponse.success(
                data,
                "AI response generated successfully"
        );
    }
}