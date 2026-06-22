package com.aichatbot.customerservices.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record SupportTicketCommentRecord(
        UUID commentId,
        String authorType,
        String authorName,
        String comment,
        Instant createdAt) {

    public SupportTicketCommentRecord {
        Objects.requireNonNull(commentId, "commentId must not be null");
        Objects.requireNonNull(authorType, "authorType must not be null");
        Objects.requireNonNull(authorName, "authorName must not be null");
        Objects.requireNonNull(comment, "comment must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
    }
}
