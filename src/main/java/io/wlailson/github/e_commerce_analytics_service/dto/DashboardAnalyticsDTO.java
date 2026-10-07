package io.wlailson.github.e_commerce_analytics_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Indicadores agregados das vendas registradas.")
public record DashboardAnalyticsDTO(
        @Schema(description = "Quantidade total de vendas.", example = "120")
        Long totalSales,
        @Schema(description = "Quantidade de vendas que possuem valor informado.", example = "115")
        Long salesWithAmount,
        @Schema(description = "Receita total das vendas com valor informado.", example = "15490.50")
        BigDecimal totalRevenue,
        @Schema(description = "Receita total dividida pela quantidade de vendas com valor.", example = "134.70")
        BigDecimal averageTicket,
        @Schema(description = "Quantidade de vendas agrupada por status.")
        List<DashboardSalesByStatusDTO> salesByStatus
) {
}
