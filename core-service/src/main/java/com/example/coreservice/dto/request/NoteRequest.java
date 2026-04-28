package com.example.coreservice.dto.request;

import com.example.coreservice.enums.ContentFormat;
import com.example.coreservice.enums.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequest {

    @Size(max = 255, message = "TITLE_TOO_LONG")
    private String title;

    private String content;

    private SourceType sourceType;

    private ContentFormat format;

    private Set<String> tags;

    private Boolean isPublic;
}