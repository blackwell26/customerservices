package com.aichatbot.customerservices.api.dto;

import java.util.UUID;

public record KnowledgeUploadResponse(
        UUID documentId,
        String sourceName,
        String originalFileName,
        String documentType,
        int version,
        int chunkCount,
        int characterCount,
        String status,
        String message) {
}
