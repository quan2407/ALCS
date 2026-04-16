package com.example.coreservice.mapper.content;

import com.example.coreservice.dto.response.AIExtractionResponse;
import com.example.coreservice.dto.response.KnowledgeAtomResponse;
import com.example.coreservice.entity.content.KnowledgeAtom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KnowledgeAtomMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "note", ignore = true)
    @Mapping(target = "reviewLevel", ignore = true)
    @Mapping(target = "isArchived", constant = "false")
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "atomVector", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    KnowledgeAtom toEntity(AIExtractionResponse response);

    KnowledgeAtomResponse toResponse(KnowledgeAtom entity);
}
