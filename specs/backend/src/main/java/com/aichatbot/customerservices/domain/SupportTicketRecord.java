package com.aichatbot.customerservices.domain;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record SupportTicketRecord(
        UUID ticketId,
        UUID customerId,
        String category,
        TicketStatus status,
        TicketPriority priority,
        String subject,
        String description,
        List<SupportTicketCommentRecord> comments,
        Instant createdAt,
        Instant updatedAt) {

    public SupportTicketRecord {
        Objects.requireNonNull(ticketId, "ticketId must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(category, "category must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(priority, "priority must not be null");
        Objects.requireNonNull(subject, "subject must not be null");
        Objects.requireNonNull(description, "description must not be null");
        comments = List.copyOf(Objects.requireNonNull(comments, "comments must not be null"));
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }
}
