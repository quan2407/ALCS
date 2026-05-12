package com.example.coreservice.dto.request;

import lombok.Data;

@Data
public class ChatMessageDto {

    private String role;

    private String content;
}