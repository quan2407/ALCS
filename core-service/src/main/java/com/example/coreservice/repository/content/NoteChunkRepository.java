package com.example.coreservice.repository.content;

import com.example.coreservice.entity.content.Note;
import com.example.coreservice.entity.content.NoteChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteChunkRepository
        extends JpaRepository<NoteChunk, Long> {

    List<NoteChunk> findByNoteOrderByChunkOrder(Note note);

    void deleteByNote(Note note);
}