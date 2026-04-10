package com.example.coreservice.entity.learning;

import com.example.coreservice.entity.BaseEntity;
import com.example.coreservice.entity.auth.User;
import com.example.coreservice.entity.content.KnowledgeAtom;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "unified_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnifiedAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private UnifiedReviewSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atom_id")
    private KnowledgeAtom atom;

    // --- PHẦN NGỮ CẢNH CÂU HỎI (Thay thế cho bảng ReviewQuestion) ---

    @Column(columnDefinition = "TEXT")
    private String questionContent; // Lưu JSON: { "q": "Java là gì?", "options": ["A", "B"], "type": "MCQ" }

    @Column(columnDefinition = "TEXT")
    private String correctAnswer;   // Lưu đáp án đúng để đối chiếu khi xem lại lịch sử

    @Column(columnDefinition = "TEXT")
    private String aiFeedback;      // AI giải thích tại sao user đúng/sai sau khi chấm điểm

    // --- PHẦN KẾT QUẢ TRẢ LỜI ---

    @Column(columnDefinition = "TEXT")
    private String userAnswer;      // Lưu câu trả lời thực tế của User

    private boolean isCorrect;

    private Double score;           // Điểm số (0.0 - 1.0)

    private Integer qualityLevel;   // Đánh giá chất lượng (0-5) cho thuật toán Spaced Repetition

    private Long responseTimeMs;    // Thời gian suy nghĩ (miliseconds)

    private Integer attemptCount;   // Lần học thứ n của Atom này
}