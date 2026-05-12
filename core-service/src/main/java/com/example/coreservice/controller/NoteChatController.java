package com.example.coreservice.controller;

import com.example.coreservice.dto.request.ChatRequest;
import com.example.coreservice.dto.response.ApiResponse;
import com.example.coreservice.dto.response.ChatResponse;
import com.example.coreservice.service.ai.NoteChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteChatController {

    private final NoteChatService chatService;

    @PostMapping("/{id}/chat")
    public ApiResponse<ChatResponse> chat(
            @PathVariable Long id,
            @Valid @RequestBody ChatRequest request
    ) {

        ChatResponse data = chatService.chat(id, request);

        return ApiResponse.success(
                data,
                "AI response generated successfully"
        );
    }
}