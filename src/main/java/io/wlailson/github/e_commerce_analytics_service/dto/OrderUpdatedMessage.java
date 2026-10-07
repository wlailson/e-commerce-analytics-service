package io.wlailson.github.e_commerce_analytics_service.dto;


import java.time.Instant;

public record OrderUpdatedMessage(
        Long orderId,
        OrderEvent event,
        Instant occurredAt
) {
}
