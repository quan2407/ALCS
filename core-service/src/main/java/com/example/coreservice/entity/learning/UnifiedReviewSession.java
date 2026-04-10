package com.example.coreservice.entity.learning;

import com.example.coreservice.entity.BaseEntity;
import com.example.coreservice.entity.auth.User; // Import User của bạn
import com.example.coreservice.entity.content.Note;
import com.example.coreservice.enums.SessionStatus;
import com.example.coreservice.enums.SessionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "unified_review_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnifiedReviewSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    private Note note;

    @Enumerated(EnumType.STRING)
    private SessionType sessionType;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;
    // hệ thống lưu lại atom gần nhâ mình hocj
    private Integer currentAtomIndex;
    // cần mất bao nhiêu thời gian để học
    private LocalDateTime startTime;
    private LocalDateTime lastActivityTime;

    private Long ttlSeconds;
    @Column(columnDefinition = "TEXT")
    private String metrics;
    // luu kết quả tạm thời
    @Column(columnDefinition = "TEXT")
    private String atomProgresses;
}