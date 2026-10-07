package io.wlailson.github.e_commerce_analytics_service.dto;

import io.wlailson.github.e_commerce_analytics_service.domain.SaleAnalytics;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleAnalyticsResponseDTO(
        String id,
        Long orderId,
        Long userId,
        BigDecimal amount,
        String status,
        LocalDateTime occurredAt
) {

    public SaleAnalyticsResponseDTO(SaleAnalytics sale) {
        this(
                sale.getId(),
                sale.getOrderId(),
                sale.getUserId(),
                sale.getAmount(),
                sale.getStatus(),
                sale.getOccurredAt()
        );
    }
}
