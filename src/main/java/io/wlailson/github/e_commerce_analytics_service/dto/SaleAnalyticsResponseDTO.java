package io.wlailson.github.e_commerce_analytics_service.dto;

import io.wlailson.github.e_commerce_analytics_service.domain.SaleAnalytics;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados analíticos registrados para uma venda.")
public record SaleAnalyticsResponseDTO(
        @Schema(description = "Identificador do registro analítico.", example = "66f1a2b3c4d5e6f789012345")
        String id,
        @Schema(description = "Identificador do pedido.", example = "42")
        Long orderId,
        @Schema(description = "Identificador do usuário que realizou o pedido.", example = "7")
        Long userId,
        @Schema(description = "Valor monetário da venda.", example = "129.90")
        BigDecimal amount,
        @Schema(description = "Status mais recente conhecido para o pedido.", example = "APPROVED")
        String status,
        @Schema(description = "Data e hora do evento mais recente.", example = "2026-10-06T15:00:00")
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
