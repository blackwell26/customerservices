package com.aichatbot.customerservices.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import com.aichatbot.customerservices.api.dto.OrderTrackingResponse;

import org.springframework.stereotype.Service;

@Service
public class OrderTrackingService {

    private static final Map<String, OrderTrackingResponse> ORDERS = Map.of(
            "100045", new OrderTrackingResponse(
                    "100045",
                    "SHIPPED",
                    new BigDecimal("149.99"),
                    "1Z999AA10123456784",
                    "UPS",
                    Instant.parse("2026-06-18T14:15:00Z"),
                    Instant.parse("2026-06-21T09:45:00Z"),
                    "Your order has shipped and is in transit."),
            "100046", new OrderTrackingResponse(
                    "100046",
                    "PROCESSING",
                    new BigDecimal("89.50"),
                    null,
                    null,
                    Instant.parse("2026-06-21T18:00:00Z"),
                    Instant.parse("2026-06-22T13:10:00Z"),
                    "Your order is being prepared for shipment."));

    public OrderTrackingResponse track(String orderNumber) {
        String normalizedOrderNumber = normalize(orderNumber);
        OrderTrackingResponse response = ORDERS.get(normalizedOrderNumber);
        if (response == null) {
            throw new OrderNotFoundException(normalizedOrderNumber);
        }

        return response;
    }

    private String normalize(String orderNumber) {
        if (orderNumber == null) {
            throw new OrderNotFoundException(null);
        }

        String normalized = orderNumber.trim();
        if (normalized.isEmpty()) {
            throw new OrderNotFoundException(orderNumber);
        }

        return normalized;
    }
}
