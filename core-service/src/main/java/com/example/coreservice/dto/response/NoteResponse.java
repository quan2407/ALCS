package com.example.coreservice.dto.response;

import com.example.coreservice.enums.ContentFormat;
import com.example.coreservice.enums.ProcessingStatus;
import com.example.coreservice.enums.SourceType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponse {
    private Long id;
    private String title;
    private String content;
    private SourceType sourceType;
    private ContentFormat format;
    private Set<String> tags;
    private Long atomCount;
    private Long wordCount;
    private Double knowledgeDensity;
    private boolean isArchived;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProcessingStatus processingStatus;
}