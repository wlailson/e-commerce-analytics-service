package io.wlailson.github.e_commerce_analytics_service.controller;

import io.wlailson.github.e_commerce_analytics_service.dto.DashboardAnalyticsDTO;
import io.wlailson.github.e_commerce_analytics_service.dto.DashboardSalesByStatusDTO;
import io.wlailson.github.e_commerce_analytics_service.dto.SaleAnalyticsResponseDTO;
import io.wlailson.github.e_commerce_analytics_service.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsService service;

    @Test
    void getSalesReturnsPagedSalesAndPassesRequestedPageable() throws Exception {
        PageRequest pageable = PageRequest.of(1, 2);
        SaleAnalyticsResponseDTO sale = new SaleAnalyticsResponseDTO(
                "sale-1", 42L, 7L, new BigDecimal("129.90"), "APPROVED",
                LocalDateTime.parse("2026-10-06T15:00:00")
        );
        when(service.getSales(pageable)).thenReturn(new PageImpl<>(List.of(sale), pageable, 3));

        mockMvc.perform(get("/analytics/sales").param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("sale-1"))
                .andExpect(jsonPath("$.content[0].orderId").value(42))
                .andExpect(jsonPath("$.content[0].amount").value(129.90))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(2));

        verify(service).getSales(pageable);
    }

    @Test
    void dashboardReturnsAnalyticsSummary() throws Exception {
        when(service.getDashboard()).thenReturn(new DashboardAnalyticsDTO(
                3L,
                2L,
                new BigDecimal("259.80"),
                new BigDecimal("129.90"),
                List.of(new DashboardSalesByStatusDTO("APPROVED", 2L))
        ));

        mockMvc.perform(get("/analytics/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSales").value(3))
                .andExpect(jsonPath("$.salesWithAmount").value(2))
                .andExpect(jsonPath("$.totalRevenue").value(259.80))
                .andExpect(jsonPath("$.averageTicket").value(129.90))
                .andExpect(jsonPath("$.salesByStatus[0].status").value("APPROVED"))
                .andExpect(jsonPath("$.salesByStatus[0].total").value(2));

        verify(service).getDashboard();
    }
}