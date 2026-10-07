package io.wlailson.github.e_commerce_analytics_service.service;

import io.wlailson.github.e_commerce_analytics_service.domain.SaleAnalytics;
import io.wlailson.github.e_commerce_analytics_service.dto.DashboardAnalyticsDTO;
import io.wlailson.github.e_commerce_analytics_service.dto.DashboardSalesByStatusDTO;
import io.wlailson.github.e_commerce_analytics_service.dto.OrderCreateMessage;
import io.wlailson.github.e_commerce_analytics_service.dto.OrderEvent;
import io.wlailson.github.e_commerce_analytics_service.dto.OrderUpdatedMessage;
import io.wlailson.github.e_commerce_analytics_service.dto.PaymentCreatedMessage;
import io.wlailson.github.e_commerce_analytics_service.dto.SaleAnalyticsResponseDTO;
import io.wlailson.github.e_commerce_analytics_service.repository.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Service
public class AnalyticsService {

    private static final BigDecimal ZERO_MONEY = BigDecimal.ZERO.setScale(2);

    private final AnalyticsRepository repository;

    public void processOrderCreated(OrderCreateMessage event) {
        Objects.requireNonNull(event, "event must not be null");
        processOrderEvent(event.orderId(), event.event(), event.occurredAt());
    }

    public void processOrderUpdated(OrderUpdatedMessage event) {
        Objects.requireNonNull(event, "event must not be null");
        processOrderEvent(event.orderId(), event.event(), event.occurredAt());
    }

    public void processPaymentCreated(PaymentCreatedMessage event) {
        Objects.requireNonNull(event, "event must not be null");
        Objects.requireNonNull(event.status(), "event.status must not be null");

        updateSale(event.orderId(), sale -> {
            sale.setUserId(event.userId());
            sale.setAmount(event.price());
            sale.setStatus(event.status().name());
            if (sale.getOccurredAt() == null && event.created() != null) {
                sale.setOccurredAt(event.created());
            }
        });
    }

    public Page<SaleAnalyticsResponseDTO> getSales(Pageable pageable) {
        return repository.findAll(pageable).map(SaleAnalyticsResponseDTO::new);
    }

    public DashboardAnalyticsDTO getDashboard() {
        return DashboardSummary.from(repository.findAll()).toDto();
    }

    private void updateSale(Long orderId, Consumer<SaleAnalytics> update) {
        Objects.requireNonNull(orderId, "event.orderId must not be null");
        SaleAnalytics sale = repository.findByOrderId(orderId).orElseGet(SaleAnalytics::new);
        sale.setOrderId(orderId);
        update.accept(sale);
        repository.save(sale);
    }

    private void processOrderEvent(Long orderId, OrderEvent event, Instant occurredAt) {
        Objects.requireNonNull(event, "event.event must not be null");
        updateSale(orderId, sale -> {
            sale.setStatus(event.name());
            if (occurredAt != null) {
                sale.setOccurredAt(LocalDateTime.ofInstant(occurredAt, ZoneOffset.UTC));
            }
        });
    }

    private static class DashboardSummary {
        private long totalSales;
        private long salesWithAmount;
        private BigDecimal totalRevenue = BigDecimal.ZERO;
        private final Map<String, Long> salesByStatus = new TreeMap<>();

        private static DashboardSummary from(List<SaleAnalytics> sales) {
            DashboardSummary summary = new DashboardSummary();
            sales.forEach(summary::include);
            return summary;
        }

        private void include(SaleAnalytics sale) {
            totalSales++;
            salesByStatus.merge(statusOf(sale), 1L, Long::sum);
            if (sale.getAmount() != null) {
                totalRevenue = totalRevenue.add(sale.getAmount());
                salesWithAmount++;
            }
        }

        private DashboardAnalyticsDTO toDto() {
            BigDecimal revenue = totalRevenue.setScale(2, RoundingMode.HALF_UP);
            BigDecimal averageTicket = salesWithAmount == 0
                    ? ZERO_MONEY
                    : revenue.divide(BigDecimal.valueOf(salesWithAmount), 2, RoundingMode.HALF_UP);
            List<DashboardSalesByStatusDTO> statusTotals = salesByStatus.entrySet().stream()
                    .map(entry -> new DashboardSalesByStatusDTO(entry.getKey(), entry.getValue()))
                    .toList();
            return new DashboardAnalyticsDTO(
                    totalSales,
                    salesWithAmount,
                    revenue,
                    averageTicket,
                    statusTotals
            );
        }

        private static String statusOf(SaleAnalytics sale) {
            return sale.getStatus() == null ? "UNKNOWN" : sale.getStatus();
        }
    }
}
