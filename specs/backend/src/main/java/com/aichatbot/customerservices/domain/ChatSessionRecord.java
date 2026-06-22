package com.aichatbot.customerservices.domain;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record ChatSessionRecord(
        String sessionId,
        String customerId,
        ChatSessionStatus status,
        String currentIntent,
        List<ChatMessageRecord> messages,
        Instant createdAt,
        Instant updatedAt) {

    public ChatSessionRecord {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(status, "status must not be null");
        messages = List.copyOf(Objects.requireNonNull(messages, "messages must not be null"));
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }
}
