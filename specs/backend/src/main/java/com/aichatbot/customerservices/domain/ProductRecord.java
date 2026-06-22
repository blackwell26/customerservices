package com.aichatbot.customerservices.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ProductRecord(
        UUID productId,
        String sku,
        String name,
        String description,
        BigDecimal price,
        String currencyCode,
        int stockQuantity,
        Instant createdAt,
        Instant updatedAt) {

    public ProductRecord {
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(sku, "sku must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(currencyCode, "currencyCode must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }
}
