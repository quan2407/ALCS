package com.example.coreservice.enums;

public enum SessionStatus {
    ACTIVE,      // Phiên học đang diễn ra
    PAUSED,      // Đang tạm dừng (người dùng thoát ra nhưng chưa kết thúc)
    COMPLETED,   // Đã hoàn thành (đã học hết các Atoms được giao)
    ABANDONED,   // Bị bỏ dở (hết TTL hoặc người dùng chủ động hủy)
    ARCHIVED     // Đã được lưu trữ sau khi tổng hợp kết quả vào dữ liệu lớn
}
