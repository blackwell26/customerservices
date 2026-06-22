package com.aichatbot.customerservices.domain;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
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
        Objects.requireNonNull(documentId, "documentId must not be null");
        Objects.requireNonNull(sourceName, "sourceName must not be null");
        Objects.requireNonNull(originalFileName, "originalFileName must not be null");
        Objects.requireNonNull(documentType, "documentType must not be null");
        Objects.requireNonNull(contentType, "contentType must not be null");
        Objects.requireNonNull(uploadedBy, "uploadedBy must not be null");
        Objects.requireNonNull(uploadedAt, "uploadedAt must not be null");
        Objects.requireNonNull(extractedText, "extractedText must not be null");
        chunks = List.copyOf(Objects.requireNonNull(chunks, "chunks must not be null"));
    }
}
