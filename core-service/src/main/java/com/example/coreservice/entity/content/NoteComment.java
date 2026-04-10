package com.example.coreservice.entity.content;

import com.example.coreservice.entity.BaseEntity;
import com.example.coreservice.entity.auth.User;
import com.example.coreservice.enums.CommentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "note_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private CommentStatus status = CommentStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // TÍNH NĂNG REPLY: Cấu trúc cây (Self-Reference)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private NoteComment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<NoteComment> replies;
}