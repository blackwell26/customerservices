package com.aichatbot.customerservices.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class KnowledgeIngestionStatusStore {

    private final List<KnowledgeIngestionStatusRecord> attempts = new ArrayList<>();

    public synchronized void recordSuccess(
            String sourceName,
            String originalFileName,
            String documentType,
            int version,
            String uploadedBy,
            int chunkCount,
            int characterCount) {
        attempts.add(new KnowledgeIngestionStatusRecord(
                UUID.randomUUID(),
                sourceName,
                originalFileName,
                documentType,
                version,
                uploadedBy,
                "SUCCESS",
                null,
                null,
                chunkCount,
                characterCount,
                Instant.now()));
    }

    public synchronized void recordFailure(
            String sourceName,
            String originalFileName,
            String documentType,
            int version,
            String uploadedBy,
            String errorCode,
            String errorMessage) {
        attempts.add(new KnowledgeIngestionStatusRecord(
                UUID.randomUUID(),
                sourceName,
                originalFileName,
                documentType,
                version,
                uploadedBy,
                "FAILED",
                errorCode,
                errorMessage,
                0,
                0,
                Instant.now()));
    }

    public synchronized List<KnowledgeIngestionStatusRecord> recent(int limit) {
        int safeLimit = Math.max(0, limit);
        return attempts.stream()
                .sorted(Comparator.comparing(KnowledgeIngestionStatusRecord::timestamp).reversed())
                .limit(safeLimit)
                .toList();
    }

    public synchronized List<KnowledgeIngestionStatusRecord> recentBySourceName(String sourceName, int limit) {
        int safeLimit = Math.max(0, limit);
        return attempts.stream()
                .filter(record -> sourceName == null || sourceName.isBlank() || sourceName.equals(record.sourceName()))
                .sorted(Comparator.comparing(KnowledgeIngestionStatusRecord::timestamp).reversed())
                .limit(safeLimit)
                .toList();
    }
}
