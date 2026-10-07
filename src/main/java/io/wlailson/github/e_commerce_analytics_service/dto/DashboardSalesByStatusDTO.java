package io.wlailson.github.e_commerce_analytics_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Contagem de vendas para um status.")
public record DashboardSalesByStatusDTO(
        @Schema(description = "Status das vendas.", example = "APPROVED")
        String status,
        @Schema(description = "Quantidade de vendas com este status.", example = "82")
        Long total
) {
}
