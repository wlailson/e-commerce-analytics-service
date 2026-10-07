package io.wlailson.github.e_commerce_analytics_service.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardAnalyticsDTO(
        Long totalSales,
        Long salesWithAmount,
        BigDecimal totalRevenue,
        BigDecimal averageTicket,
        List<DashboardSalesByStatusDTO> salesByStatus
) {
}
