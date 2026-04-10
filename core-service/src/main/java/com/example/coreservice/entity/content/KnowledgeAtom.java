package com.example.coreservice.entity.content;

import com.example.coreservice.entity.BaseEntity;
import com.example.coreservice.enums.AtomType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "knowledge_atoms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeAtom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private AtomType type;

    private Double difficultyScore;
    private Double importanceScore;
    private Long reviewLevel;

    // dùng để tìm kiếm các kiến thức tương tự
    @Column(columnDefinition = "TEXT")
    private String atomVector;

    @ElementCollection
    @CollectionTable(name = "atom_tags", joinColumns = @JoinColumn(name = "atom_id"))
    @Column(name = "tag_name")
    private Set<String> tags = new HashSet<>(); // tag này để phân loại về mặt kỹ thuật

    private boolean isArchived = false;
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;
}