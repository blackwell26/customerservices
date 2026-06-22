package com.aichatbot.customerservices.domain;

import java.util.Objects;
import java.util.UUID;

public record KnowledgeChunkRecord(
        UUID chunkId,
        UUID documentId,
        int chunkIndex,
        String content,
        String embeddingReference) {

    public KnowledgeChunkRecord {
        Objects.requireNonNull(chunkId, "chunkId must not be null");
        Objects.requireNonNull(documentId, "documentId must not be null");
        Objects.requireNonNull(content, "content must not be null");
    }
}
