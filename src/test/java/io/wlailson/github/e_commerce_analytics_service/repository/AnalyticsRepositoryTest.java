package io.wlailson.github.e_commerce_analytics_service.repository;

import io.wlailson.github.e_commerce_analytics_service.domain.SaleAnalytics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@Testcontainers
class AnalyticsRepositoryTest {

    @Container
    @ServiceConnection
    static final MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:7.0"));

    @Autowired
    private AnalyticsRepository repository;

    @BeforeEach
    void cleanRepository() {
        repository.deleteAll();
    }

    @Test
    void savesAndReadsSaleById() {
        SaleAnalytics sale = new SaleAnalytics(
                null,
                42L,
                7L,
                new BigDecimal("129.90"),
                "APPROVED",
                LocalDateTime.parse("2026-10-06T15:00:00")
        );

        SaleAnalytics saved = repository.save(sale);

        SaleAnalytics loaded = repository.findById(saved.getId()).orElseThrow();
        assertEquals(42L, loaded.getOrderId());
        assertEquals(7L, loaded.getUserId());
        assertEquals(new BigDecimal("129.90"), loaded.getAmount());
        assertEquals("APPROVED", loaded.getStatus());
        assertEquals(sale.getOccurredAt(), loaded.getOccurredAt());
    }

    @Test
    void findsSaleByOrderIdAndReturnsEmptyForUnknownOrder() {
        repository.save(new SaleAnalytics(null, 42L, 7L, new BigDecimal("129.90"), "APPROVED", null));

        assertTrue(repository.findByOrderId(42L).isPresent());
        assertTrue(repository.findByOrderId(99L).isEmpty());
    }
}