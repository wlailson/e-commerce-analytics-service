package io.wlailson.github.e_commerce_analytics_service.dto;

import java.time.Instant;
import java.util.Set;

public record OrderCreateMessage(
        Long orderId,
        OrderEvent event,
        Instant occurredAt,
        Instant expiresAt,
        Set<OrderItemCreateMessage> items
) {
}
