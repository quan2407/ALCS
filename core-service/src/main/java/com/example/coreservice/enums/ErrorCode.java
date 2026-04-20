package com.example.coreservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi chưa xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND(1003, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1004, "Sai email hoặc mật khẩu", HttpStatus.UNAUTHORIZED),
    USER_EXISTED(1005, "Email này đã được sử dụng", HttpStatus.BAD_REQUEST),
    AI_SERVICE_TIMEOUT(1001, "AI Service phản hồi quá chậm, vui lòng thử lại sau", HttpStatus.GATEWAY_TIMEOUT),
    AI_RESPONSE_MALFORMED(1002, "Dữ liệu từ AI trả về không đúng định dạng", HttpStatus.INTERNAL_SERVER_ERROR),
    KNOWLEDGE_ATOM_NOT_FOUND(2001, "Không tìm thấy Knowledge Atom yêu cầu", HttpStatus.NOT_FOUND),
    INVALID_NOTE_CONTENT(2002, "Nội dung ghi chú không hợp lệ để phân tích", HttpStatus.BAD_REQUEST),
    INVALID_VERIFICATION_CODE(1006, "Mã xác thực không chính xác", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_EXPIRED(1007, "Mã xác thực đã hết hạn", HttpStatus.BAD_REQUEST),
    USER_NOT_VERIFIED(1008, "Tài khoản chưa được xác thực email", HttpStatus.FORBIDDEN),
    NOTE_NOT_FOUND(2003, "Không tìm thấy ghi chú yêu cầu hoặc bạn không có quyền truy cập", HttpStatus.NOT_FOUND),
    AI_SERVICE_UNAVAILABLE(1010, "AI Service hiện không khả dụng", HttpStatus.SERVICE_UNAVAILABLE),

    AI_SERVICE_ERROR(1011, "Lỗi khi xử lý từ AI Service", HttpStatus.BAD_GATEWAY),

    AI_EMPTY_RESULT(1012, "AI không trích xuất được dữ liệu hợp lệ", HttpStatus.NO_CONTENT),

    AI_RETRY_FAILED(1013, "AI Service thất bại sau nhiều lần thử", HttpStatus.BAD_GATEWAY),

    AI_INVALID_INPUT(1014, "Nội dung gửi đến AI không hợp lệ", HttpStatus.BAD_REQUEST),
    USER_ALREADY_VERIFIED(1009, "Tài khoản đã được xác thực trước đó", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
