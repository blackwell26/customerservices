package com.aichatbot.customerservices.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record CustomerRecord(
        UUID customerId,
        String username,
        String email,
        String fullName,
        Instant createdAt,
        Instant updatedAt) {

    public CustomerRecord {
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }
}
