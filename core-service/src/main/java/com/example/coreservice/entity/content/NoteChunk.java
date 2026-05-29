package com.example.coreservice.entity.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class NoteChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer chunkOrder;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String contentHash;

    @ManyToOne(fetch = FetchType.LAZY)
    private Note note;
}
