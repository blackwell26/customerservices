package com.aichatbot.customerservices.service;

import java.time.Instant;
import java.util.List;

public record ChatSessionRecord(
        String sessionId,
        String username,
        List<ChatMessageRecord> messages,
        Instant createdAt,
        Instant updatedAt) {

    public ChatSessionRecord {
        messages = List.copyOf(messages);
    }

    public ChatSessionRecord withMessage(String role, String text) {
        return new ChatSessionRecord(
                sessionId,
                username,
                append(new ChatMessageRecord(role, text, Instant.now())),
                createdAt,
                Instant.now());
    }

    private List<ChatMessageRecord> append(ChatMessageRecord message) {
        var updated = new java.util.ArrayList<>(messages);
        updated.add(message);
        return updated;
    }
}

