package com.example.coreservice.service.content;

import com.example.coreservice.dto.request.KnowledgeAtomUpdateRequest;
import com.example.coreservice.dto.request.NoteRequest;
import com.example.coreservice.dto.response.KnowledgeAtomResponse;
import com.example.coreservice.dto.response.NoteResponse;
import com.example.coreservice.dto.response.PageResponse;
import com.example.coreservice.entity.auth.User;
import com.example.coreservice.entity.content.KnowledgeAtom;
import com.example.coreservice.entity.content.Note;
import com.example.coreservice.enums.ErrorCode;
import com.example.coreservice.exception.AppException;
import com.example.coreservice.mapper.content.KnowledgeAtomMapper;
import com.example.coreservice.mapper.content.NoteMapper;
import com.example.coreservice.repository.content.KnowledgeAtomRepository;
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
    private final KnowledgeAtomRepository atomRepository;
    private final KnowledgeAtomMapper atomMapper;

    @Transactional
    public NoteResponse createNote(NoteRequest request) {
        User user = securityUtils.getCurrentUser();
        Note note = noteMapper.toNote(request);
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            note.setTitle("Untitled");
        }
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
        Page<Note> notePage = noteRepository.findAllByUserAndIsArchivedFalseAndIsDeletedFalse(user, pageable);

        List<NoteResponse> data = notePage.getContent().stream()
                .map(noteMapper::toNoteResponse)
                .toList();

        return PageResponse.of(notePage, data);
    }
    @Transactional
    public NoteResponse updateNote(Long id, NoteRequest request) {
        User user = securityUtils.getCurrentUser();
        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorCode.NOTE_NOT_FOUND));

        noteMapper.updateNoteFromRequest(request, note);

        note.setWordCount(countWords(note.getContent()));

        return noteMapper.toNoteResponse(noteRepository.save(note));
    }

    @Transactional
    public void deleteNote(Long id) {
        User user = securityUtils.getCurrentUser();

        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorCode.NOTE_NOT_FOUND));

        note.setDeleted(true);
        noteRepository.save(note);
    }

    public List<KnowledgeAtomResponse> getAtomsByNoteId(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTE_NOT_FOUND));

        return atomRepository.findAllByNoteOrderByCreatedAtDesc(note).stream()
                .map(atomMapper::toResponse)
                .toList();
    }
    @Transactional
    public KnowledgeAtomResponse updateAtom(Long atomId, KnowledgeAtomUpdateRequest request) {
        KnowledgeAtom atom = atomRepository.findById(atomId)
                .orElseThrow(() -> new AppException(ErrorCode.KNOWLEDGE_ATOM_NOT_FOUND));

        atom.setTitle(request.getTitle());
        atom.setContent(request.getContent());
        atom.setType(request.getType());

        if (request.getDifficultyScore() != null) {
            atom.setDifficultyScore(request.getDifficultyScore());
        }
        if (request.getImportanceScore() != null) {
            atom.setImportanceScore(request.getImportanceScore());
        }

        return atomMapper.toResponse(atomRepository.save(atom));
    }

    @Transactional
    public void deleteAtom(Long atomId) {
        if (!atomRepository.existsById(atomId)) {
            throw new AppException(ErrorCode.KNOWLEDGE_ATOM_NOT_FOUND);
        }

        // 2. Thực hiện xóa
        atomRepository.deleteById(atomId);
    }

    @Transactional
    public void archiveNote(Long id) {
        User user = securityUtils.getCurrentUser();

        Note note = noteRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorCode.NOTE_NOT_FOUND));

        note.setArchived(true);
        noteRepository.save(note);
    }
}
