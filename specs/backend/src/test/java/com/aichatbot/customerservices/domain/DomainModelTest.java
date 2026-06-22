package com.aichatbot.customerservices.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class DomainModelTest {

    @Test
    void orderItemsAreDefensivelyCopied() {
        List<OrderItemRecord> items = new ArrayList<>();
        items.add(new OrderItemRecord(UUID.randomUUID(), UUID.randomUUID(), "SKU-1", "Item 1", 2, new BigDecimal("19.99")));

        OrderRecord order = new OrderRecord(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "100045",
                OrderStatus.SHIPPED,
                new BigDecimal("39.98"),
                "USD",
                items,
                Instant.parse("2026-06-21T09:45:00Z"),
                Instant.parse("2026-06-22T09:45:00Z"));

        items.add(new OrderItemRecord(UUID.randomUUID(), UUID.randomUUID(), "SKU-2", "Item 2", 1, new BigDecimal("9.99")));

        assertEquals(1, order.items().size());
    }

    @Test
    void chatSessionRequiresNonNullCollections() {
        assertThrows(NullPointerException.class, () -> new ChatSessionRecord(
                "session-1",
                "customer-1",
                ChatSessionStatus.ACTIVE,
                "order_tracking",
                null,
                Instant.parse("2026-06-21T09:45:00Z"),
                Instant.parse("2026-06-22T09:45:00Z")));
    }
}
