package com.aichatbot.customerservices.domain;

import java.time.Instant;
import java.util.Objects;

public record ChatMessageRecord(String role, String text, Instant timestamp) {

    public ChatMessageRecord {
        Objects.requireNonNull(role, "role must not be null");
        Objects.requireNonNull(text, "text must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
    }
}
