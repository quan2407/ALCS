package com.example.coreservice.dto.response;

import com.example.coreservice.enums.AtomType;
import lombok.Data;

import java.util.List;

@Data
public class AIExtractionResponse {
    private String title;
    private String content;
    private AtomType type;
    private Double difficultyScore;
    private Double importanceScore;
    private List<String> tags;
}
