package com.example.coreservice.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AIChatRequest {

    private String message;

    private List<ChatMessageDto> history;
}