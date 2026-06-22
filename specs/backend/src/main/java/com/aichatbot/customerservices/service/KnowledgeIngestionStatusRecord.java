package com.aichatbot.customerservices.service;

import java.time.Instant;
import java.util.UUID;

public record KnowledgeIngestionStatusRecord(
        UUID attemptId,
        String sourceName,
        String originalFileName,
        String documentType,
        int version,
        String uploadedBy,
        String status,
        String errorCode,
        String errorMessage,
        int chunkCount,
        int characterCount,
        Instant timestamp) {
}
