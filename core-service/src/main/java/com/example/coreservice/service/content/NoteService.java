package com.example.coreservice.service.content;

import com.example.coreservice.dto.request.NoteRequest;
import com.example.coreservice.dto.response.NoteResponse;
import com.example.coreservice.dto.response.PageResponse;
import com.example.coreservice.entity.auth.User;
import com.example.coreservice.entity.content.Note;
import com.example.coreservice.enums.ErrorCode;
import com.example.coreservice.exception.AppException;
import com.example.coreservice.mapper.content.NoteMapper;
import com.example.coreservice.repository.content.NoteRepository;
import com.example.coreservice.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
    public NoteResponse getNote(Long id) {
        User user = securityUtils.getCurrentUser();

        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorCode.NOTE_NOT_FOUND));

        return noteMapper.toNoteResponse(note);
    }

    public PageResponse<NoteResponse> getMyNotes(int page, int size){
        User user = securityUtils.getCurrentUser();

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Note> notePage = noteRepository.findAllByUserAndIsArchivedFalse(user, pageable);

        List<NoteResponse> data = notePage.getContent().stream()
                .map(noteMapper::toNoteResponse)
                .toList();

        return PageResponse.of(notePage, data);
    }

}
