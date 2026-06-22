package com.aichatbot.customerservices.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record KnowledgeDocumentRecord(
        UUID documentId,
        String sourceName,
        String originalFileName,
        String documentType,
        String contentType,
        int version,
        String uploadedBy,
        Instant uploadedAt,
        String extractedText,
        List<KnowledgeChunkRecord> chunks) {

    public KnowledgeDocumentRecord {
        chunks = List.copyOf(chunks);
    }
}
