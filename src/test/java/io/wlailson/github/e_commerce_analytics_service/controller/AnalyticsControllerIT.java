package io.wlailson.github.e_commerce_analytics_service.controller;

import io.wlailson.github.e_commerce_analytics_service.domain.SaleAnalytics;
import io.wlailson.github.e_commerce_analytics_service.repository.AnalyticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AnalyticsControllerIT {

    private static final String TOKEN = "analytics-controller-it-token";

    @Container
    @ServiceConnection
    static final KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("apache/kafka-native:4.2.1"));

    @Container
    @ServiceConnection
    static final MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:8.2"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnalyticsRepository repository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        when(jwtDecoder.decode(TOKEN)).thenReturn(Jwt.withTokenValue(TOKEN)
                .header("alg", "none")
                .claim("sub", "integration-test")
                .build());
        repository.save(new SaleAnalytics(
                null,
                42L,
                7L,
                new BigDecimal("129.90"),
                "APPROVED",
                LocalDateTime.parse("2026-10-06T15:00:00")
        ));
    }

    @Test
    void salesEndpointReadsPersistedSalesThroughHttp() throws Exception {
        mockMvc.perform(get("/analytics/sales")
                        .header("Authorization", "Bearer " + TOKEN)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderId").value(42))
                .andExpect(jsonPath("$.content[0].userId").value(7))
                .andExpect(jsonPath("$.content[0].amount").value(129.90))
                .andExpect(jsonPath("$.content[0].status").value("APPROVED"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void dashboardEndpointAggregatesPersistedSalesThroughHttp() throws Exception {
        mockMvc.perform(get("/analytics/dashboard")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSales").value(1))
                .andExpect(jsonPath("$.salesWithAmount").value(1))
                .andExpect(jsonPath("$.totalRevenue").value(129.90))
                .andExpect(jsonPath("$.averageTicket").value(129.90))
                .andExpect(jsonPath("$.salesByStatus[0].status").value("APPROVED"))
                .andExpect(jsonPath("$.salesByStatus[0].total").value(1));
    }

    @Test
    void analyticsEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/analytics/dashboard"))
                .andExpect(status().isUnauthorized());
    }
}
