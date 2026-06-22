package com.aichatbot.customerservices.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderTrackingResponse(
        String orderNumber,
        String status,
        BigDecimal totalAmount,
        String trackingNumber,
        String shippingCarrier,
        Instant createdAt,
        Instant updatedAt,
        String customerMessage) {
}
