package com.aichatbot.customerservices.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record OrderItemRecord(
        UUID orderItemId,
        UUID productId,
        String sku,
        String productName,
        int quantity,
        BigDecimal unitPrice) {

    public OrderItemRecord {
        Objects.requireNonNull(orderItemId, "orderItemId must not be null");
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(sku, "sku must not be null");
        Objects.requireNonNull(productName, "productName must not be null");
        Objects.requireNonNull(unitPrice, "unitPrice must not be null");
    }
}
