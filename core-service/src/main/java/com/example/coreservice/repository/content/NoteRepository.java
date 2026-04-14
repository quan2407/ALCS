package com.example.coreservice.repository.content;

import com.example.coreservice.entity.content.Note;
import com.example.coreservice.entity.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    Page<Note> findAllByUserAndIsArchivedFalseAndIsDeletedFalse(User user, Pageable pageable);
    Optional<Note> findByIdAndUser(Long id, User user);

    long countByUser(User user);
}