package com.example.coreservice.service.content;

import com.example.coreservice.dto.request.NoteRequest;
import com.example.coreservice.dto.response.NoteResponse;
import com.example.coreservice.entity.auth.User;
import com.example.coreservice.entity.content.Note;
import com.example.coreservice.enums.ErrorCode;
import com.example.coreservice.exception.AppException;
import com.example.coreservice.mapper.content.NoteMapper;
import com.example.coreservice.repository.auth.UserRepository;
import com.example.coreservice.repository.content.NoteRepository;
import com.example.coreservice.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final SecurityUtils securityUtils;
    private final NoteMapper noteMapper;

    @Transactional
    public NoteResponse createNote(NoteRequest request) {
        User user = securityUtils.getCurrentUser();
        Note note = noteMapper.toNote(request);

        note.setUser(user);
        note.setAtomCount(0L);
        note.setWordCount(countWords(request.getContent()));

        return noteMapper.toNoteResponse(noteRepository.save(note));
    }

    private Long countWords(String content) {
        if (content == null || content.isBlank()) {
            return 0L;
        }
        // Tách chuỗi bằng khoảng trắng để đếm số từ
        return (long) content.trim().split("\\s+").length;
    }

}
