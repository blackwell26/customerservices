package com.aichatbot.customerservices.service;

import java.util.UUID;

public record KnowledgeChunkRecord(
        UUID chunkId,
        UUID documentId,
        int sequence,
        String chunkText,
        String embeddingReferenceId) {
}
