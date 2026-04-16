package com.example.coreservice.controller;

import com.example.coreservice.dto.request.NoteRequest;
import com.example.coreservice.dto.response.ApiResponse;
import com.example.coreservice.dto.response.NoteResponse;
import com.example.coreservice.dto.response.PageResponse;
import com.example.coreservice.service.content.KnowledgeExtractionService;
import com.example.coreservice.service.content.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;
    private final KnowledgeExtractionService extractionService;
    @PostMapping
    public ApiResponse<NoteResponse> createNote(@RequestBody @Valid NoteRequest request){
        NoteResponse data = noteService.createNote(request);
        return ApiResponse.success(data,"Note created successfully");
    }
    @GetMapping("/{id}")
    public ApiResponse<NoteResponse> getNote(@PathVariable Long id) {
        return ApiResponse.success(noteService.getNote(id), "Get note detail successfully");
    }

    @GetMapping
    public ApiResponse<PageResponse<NoteResponse>> getMyNotes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(noteService.getMyNotes(page, size), "Get list of notes successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<NoteResponse> updateNote(@PathVariable Long id, @RequestBody @Valid NoteRequest request) {
        return ApiResponse.success(noteService.updateNote(id, request), "Note updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ApiResponse.success(null, "Note deleted successfully");
    }
    @PostMapping("/{id}/extract")
    public ResponseEntity<String> extractKnowledge(@PathVariable Long id) {
        extractionService.extractAtomsFromNote(id);
        // Vì quá trình gọi AI chạy ngầm (Asynchronous), mình trả về 202 Accepted
        return ResponseEntity.accepted().body("Yêu cầu trích xuất đang được xử lý...");
    }
}
