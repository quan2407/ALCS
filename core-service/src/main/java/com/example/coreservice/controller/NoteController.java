package com.example.coreservice.controller;

import com.example.coreservice.dto.request.NoteRequest;
import com.example.coreservice.dto.response.ApiResponse;
import com.example.coreservice.dto.response.NoteResponse;
import com.example.coreservice.service.content.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;
    @PostMapping
    public ApiResponse<NoteResponse> createNote(@RequestBody @Valid NoteRequest request){
        NoteResponse data = noteService.createNote(request);
        return ApiResponse.success(data,"Note created successfully");
    }
}
