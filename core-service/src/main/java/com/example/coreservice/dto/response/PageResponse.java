package com.example.coreservice.dto.response;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private List<T> data;
    public static <T> PageResponse<T> of(Page<?> page, List<T> data) {
        return PageResponse.<T>builder()
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageSize(page.getSize())
                .data(data)
                .build();
    }
}