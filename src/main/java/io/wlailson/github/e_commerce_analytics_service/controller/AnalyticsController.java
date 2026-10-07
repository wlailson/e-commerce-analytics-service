package io.wlailson.github.e_commerce_analytics_service.controller;

import io.wlailson.github.e_commerce_analytics_service.dto.DashboardAnalyticsDTO;
import io.wlailson.github.e_commerce_analytics_service.dto.SaleAnalyticsResponseDTO;
import io.wlailson.github.e_commerce_analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService service;

    @GetMapping("/sales")
    public ResponseEntity<Page<SaleAnalyticsResponseDTO>> getSales(Pageable pageable) {
        return ResponseEntity.ok(service.getSales(pageable));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardAnalyticsDTO> dashboard() {
        return ResponseEntity.ok(service.getDashboard());
    }
}
