package com.example.coreservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi chưa xác định", HttpStatus.INTERNAL_SERVER_ERROR),

    AI_SERVICE_TIMEOUT(1001, "AI Service phản hồi quá chậm, vui lòng thử lại sau", HttpStatus.GATEWAY_TIMEOUT),
    AI_RESPONSE_MALFORMED(1002, "Dữ liệu từ AI trả về không đúng định dạng", HttpStatus.INTERNAL_SERVER_ERROR),
    KNOWLEDGE_ATOM_NOT_FOUND(2001, "Không tìm thấy Knowledge Atom yêu cầu", HttpStatus.NOT_FOUND),
    INVALID_NOTE_CONTENT(2002, "Nội dung ghi chú không hợp lệ để phân tích", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
