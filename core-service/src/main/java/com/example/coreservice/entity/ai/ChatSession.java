package com.example.coreservice.entity.ai;

import com.example.coreservice.entity.BaseEntity;
import com.example.coreservice.entity.auth.User;
import com.example.coreservice.entity.content.Note;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    private Note note;

    @Column(columnDefinition = "TEXT")
    private String summary;
}