package com.aichatbot.customerservices.api.dto;

import java.time.Instant;
import java.util.UUID;

import com.aichatbot.customerservices.service.KnowledgeIngestionStatusRecord;

public record KnowledgeIngestionStatusResponse(
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

    public static KnowledgeIngestionStatusResponse from(KnowledgeIngestionStatusRecord record) {
        return new KnowledgeIngestionStatusResponse(
                record.attemptId(),
                record.sourceName(),
                record.originalFileName(),
                record.documentType(),
                record.version(),
                record.uploadedBy(),
                record.status(),
                record.errorCode(),
                record.errorMessage(),
                record.chunkCount(),
                record.characterCount(),
                record.timestamp());
    }
}
