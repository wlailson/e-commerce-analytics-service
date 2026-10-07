package io.wlailson.github.e_commerce_analytics_service.service;

import io.wlailson.github.e_commerce_analytics_service.domain.SaleAnalytics;
import io.wlailson.github.e_commerce_analytics_service.dto.DashboardAnalyticsDTO;
import io.wlailson.github.e_commerce_analytics_service.dto.DashboardSalesByStatusDTO;
import io.wlailson.github.e_commerce_analytics_service.dto.OrderCreateMessage;
import io.wlailson.github.e_commerce_analytics_service.dto.OrderEvent;
import io.wlailson.github.e_commerce_analytics_service.dto.OrderUpdatedMessage;
import io.wlailson.github.e_commerce_analytics_service.dto.PaymentCreatedMessage;
import io.wlailson.github.e_commerce_analytics_service.dto.PaymentStatus;
import io.wlailson.github.e_commerce_analytics_service.dto.SaleAnalyticsResponseDTO;
import io.wlailson.github.e_commerce_analytics_service.repository.AnalyticsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnalyticsServiceTest {

    private final AnalyticsRepository repository = mock(AnalyticsRepository.class);
    private final AnalyticsService service = new AnalyticsService(repository);

    @Test
    void processOrderCreatedCreatesSaleWithUtcTimestamp() {
        Instant occurredAt = Instant.parse("2026-10-06T18:00:00Z");
        OrderCreateMessage event = new OrderCreateMessage(1L, OrderEvent.CREATE, occurredAt, null, null);
        when(repository.findByOrderId(1L)).thenReturn(Optional.empty());

        service.processOrderCreated(event);

        SaleAnalytics saved = captureSavedSale();
        assertEquals(1L, saved.getOrderId());
        assertEquals("CREATE", saved.getStatus());
        assertEquals(LocalDateTime.ofInstant(occurredAt, ZoneOffset.UTC), saved.getOccurredAt());
    }

    @Test
    void processOrderUpdatedUpdatesExistingSaleAndPreservesOtherFields() {
        LocalDateTime originalTimestamp = LocalDateTime.parse("2026-10-05T12:00:00");
        SaleAnalytics existing = new SaleAnalytics(
                "sale-1", 1L, 2L, new BigDecimal("19.99"), "CREATE", originalTimestamp
        );
        Instant occurredAt = Instant.parse("2026-10-06T18:00:00Z");
        when(repository.findByOrderId(1L)).thenReturn(Optional.of(existing));

        service.processOrderUpdated(new OrderUpdatedMessage(1L, OrderEvent.SHIP, occurredAt));

        SaleAnalytics saved = captureSavedSale();
        assertEquals(existing, saved);
        assertEquals("SHIP", saved.getStatus());
        assertEquals(LocalDateTime.ofInstant(occurredAt, ZoneOffset.UTC), saved.getOccurredAt());
        assertEquals(2L, saved.getUserId());
        assertEquals(new BigDecimal("19.99"), saved.getAmount());
    }

    @Test
    void processPaymentCreatedPopulatesNewSaleAndUsesCreatedTimestamp() {
        LocalDateTime created = LocalDateTime.parse("2026-10-06T15:00:00");
        PaymentCreatedMessage event = new PaymentCreatedMessage(
                10L, 1L, 2L, new BigDecimal("19.99"), PaymentStatus.APPROVED, created, null
        );
        when(repository.findByOrderId(1L)).thenReturn(Optional.empty());

        service.processPaymentCreated(event);

        SaleAnalytics saved = captureSavedSale();
        assertEquals(1L, saved.getOrderId());
        assertEquals(2L, saved.getUserId());
        assertEquals(new BigDecimal("19.99"), saved.getAmount());
        assertEquals("APPROVED", saved.getStatus());
        assertEquals(created, saved.getOccurredAt());
    }

    @Test
    void processPaymentCreatedPreservesExistingTimestamp() {
        LocalDateTime originalTimestamp = LocalDateTime.parse("2026-10-05T12:00:00");
        SaleAnalytics existing = new SaleAnalytics(
                "sale-1", 1L, 2L, new BigDecimal("19.99"), "CREATE", originalTimestamp
        );
        PaymentCreatedMessage event = new PaymentCreatedMessage(
                10L, 1L, 3L, new BigDecimal("25.00"), PaymentStatus.REFUNDED,
                LocalDateTime.parse("2026-10-06T15:00:00"), null
        );
        when(repository.findByOrderId(1L)).thenReturn(Optional.of(existing));

        service.processPaymentCreated(event);

        SaleAnalytics saved = captureSavedSale();
        assertEquals(3L, saved.getUserId());
        assertEquals(new BigDecimal("25.00"), saved.getAmount());
        assertEquals("REFUNDED", saved.getStatus());
        assertEquals(originalTimestamp, saved.getOccurredAt());
    }

    @Test
    void getSalesMapsRepositoryPageToResponseDtos() {
        LocalDateTime occurredAt = LocalDateTime.parse("2026-10-06T15:00:00");
        SaleAnalytics sale = new SaleAnalytics(
                "sale-1", 2L, 3L, new BigDecimal("19.99"), "APPROVED", occurredAt
        );
        PageRequest pageable = PageRequest.of(1, 1);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(sale), pageable, 2));

        Page<SaleAnalyticsResponseDTO> result = service.getSales(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getNumber());
        assertEquals(1, result.getSize());
        assertEquals(new SaleAnalyticsResponseDTO(sale), result.getContent().getFirst());
        verify(repository).findAll(pageable);
    }

    @Test
    void getDashboardAggregatesRevenueAverageAndStatuses() {
        when(repository.findAll()).thenReturn(List.of(
                new SaleAnalytics("sale-1", 1L, 1L, new BigDecimal("1.00"), "APPROVED", null),
                new SaleAnalytics("sale-2", 2L, 2L, new BigDecimal("2.01"), "APPROVED", null),
                new SaleAnalytics("sale-3", 3L, null, null, null, null)
        ));

        DashboardAnalyticsDTO result = service.getDashboard();

        assertEquals(3L, result.totalSales());
        assertEquals(2L, result.salesWithAmount());
        assertEquals(new BigDecimal("3.01"), result.totalRevenue());
        assertEquals(new BigDecimal("1.51"), result.averageTicket());
        assertEquals(List.of(
                new DashboardSalesByStatusDTO("APPROVED", 2L),
                new DashboardSalesByStatusDTO("UNKNOWN", 1L)
        ), result.salesByStatus());
    }

    @Test
    void getDashboardReturnsZeroMoneyWhenNoSaleHasAmount() {
        when(repository.findAll()).thenReturn(List.of(
                new SaleAnalytics("sale-1", 1L, null, null, "CREATE", null)
        ));

        DashboardAnalyticsDTO result = service.getDashboard();

        assertEquals(1L, result.totalSales());
        assertEquals(0L, result.salesWithAmount());
        assertEquals(new BigDecimal("0.00"), result.totalRevenue());
        assertEquals(new BigDecimal("0.00"), result.averageTicket());
    }

    @Test
    void processPaymentCreatedRejectsNullEventWithoutSaving() {
        assertThrows(NullPointerException.class, () -> service.processPaymentCreated(null));

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private SaleAnalytics captureSavedSale() {
        ArgumentCaptor<SaleAnalytics> saleCaptor = ArgumentCaptor.forClass(SaleAnalytics.class);
        verify(repository).save(saleCaptor.capture());
        return saleCaptor.getValue();
    }
}
