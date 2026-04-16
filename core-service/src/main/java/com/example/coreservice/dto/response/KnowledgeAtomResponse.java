package com.example.coreservice.dto.response;

import com.example.coreservice.enums.AtomType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeAtomResponse {
    private Long id;
    private String title;
    private String content;
    private AtomType type;
    private Double difficultyScore;
    private Double importanceScore;
    private Long reviewLevel;
    private Set<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}