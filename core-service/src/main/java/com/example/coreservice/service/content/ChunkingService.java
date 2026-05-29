package com.example.coreservice.service.content;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ChunkingService {

    public List<String> splitIntoChunks(String content) {

        return Arrays.stream(content.split("\n\n"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
