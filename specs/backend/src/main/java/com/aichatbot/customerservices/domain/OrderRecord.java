package com.aichatbot.customerservices.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record OrderRecord(
        UUID orderId,
        UUID customerId,
        String orderNumber,
        OrderStatus status,
        BigDecimal totalAmount,
        String currencyCode,
        List<OrderItemRecord> items,
        Instant createdAt,
        Instant updatedAt) {

    public OrderRecord {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(orderNumber, "orderNumber must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(totalAmount, "totalAmount must not be null");
        Objects.requireNonNull(currencyCode, "currencyCode must not be null");
        items = List.copyOf(Objects.requireNonNull(items, "items must not be null"));
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }
}
