package com.example.coreservice.entity.ai;

import com.example.coreservice.entity.BaseEntity;
import com.example.coreservice.enums.MessageRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "conversation_memories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationMemory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Enumerated(EnumType.STRING)
    private MessageRole role;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private Integer tokensUsed;
}