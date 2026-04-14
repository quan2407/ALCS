package com.example.coreservice.mapper.content;

import com.example.coreservice.dto.request.NoteRequest;
import com.example.coreservice.dto.response.NoteResponse;
import com.example.coreservice.entity.content.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoteMapper {
    // Map từ Request sang Entity
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "atoms",ignore = true)
    Note toNote(NoteRequest request);

    // Map từ Entity sang Response
    NoteResponse toNoteResponse(Note note);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateNoteFromRequest(NoteRequest request, @MappingTarget Note note);
}
