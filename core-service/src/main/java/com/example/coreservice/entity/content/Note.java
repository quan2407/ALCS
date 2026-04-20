package com.example.coreservice.entity.content;

import com.example.coreservice.entity.BaseEntity;
import com.example.coreservice.entity.auth.User;
import com.example.coreservice.enums.ContentFormat;
import com.example.coreservice.enums.ProcessingStatus;
import com.example.coreservice.enums.SourceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KnowledgeAtom> atoms;

    @Column(columnDefinition = "TEXT")
    private String mindMapConfig;

    private Long atomCount;
    private Long wordCount;
    // atomCount/ wordCount, nếu tỉ lệ cao thì ba viết nhiều kiến thức, còn thấp
    // thì bài viết đó nhiều từ nối, ví dụ chứ kiến thức không có mấy
    // => user đánh giá những tài liệu nào cần học
    private Double knowledgeDensity;
    // AI chấm xem đọ chính xác, sự logic, độ đầydudur nguồn tham chiếu
    private Long qualityScore;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;
    private String sourceUrl;
    @Enumerated(EnumType.STRING)
    private ContentFormat format;

    @ElementCollection
    @CollectionTable(
            name = "note_tags",
            joinColumns = @JoinColumn(name = "note_id")
    )
    @Column(name = "tag_name")
    private Set<String> tags = new HashSet<>();
    // nếu archive là true thì sẽ ẩn đi nhưng không xóa để sau này tiện tra
    private boolean isArchived = false;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted = false;
    private boolean isPublic = false;
    @Enumerated(EnumType.STRING)
    private ProcessingStatus processingStatus = ProcessingStatus.IDLE;
    ///
    /// Tạo ra tọa độ ghi chú trong không gian đa chiều
    /// AI chuyển văn bản thành mảng vector
    /// ý nghĩa càng giống nhau -> khoảng cách giữa các vector càng ngắn
    @Column(columnDefinition = "TEXT")
    private String embeddingVector;
    private String embeddingVersion;
    private LocalDateTime lastEmbeddedAt;

}