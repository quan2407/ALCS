package com.example.coreservice.repository.content;

import com.example.coreservice.entity.content.KnowledgeAtom;
import com.example.coreservice.entity.content.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeAtomRepository extends JpaRepository<KnowledgeAtom, Long> {

    List<KnowledgeAtom> findAllByNoteOrderByCreatedAtDesc(Note note);

    List<KnowledgeAtom> findAllByNoteAndType(Note note, com.example.coreservice.enums.AtomType type);

    void deleteAllByNote(Note note);
}