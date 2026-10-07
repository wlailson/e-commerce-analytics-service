package io.wlailson.github.e_commerce_analytics_service.dto;

public record DashboardSalesByStatusDTO(
        String status,
        Long total
) {
}
