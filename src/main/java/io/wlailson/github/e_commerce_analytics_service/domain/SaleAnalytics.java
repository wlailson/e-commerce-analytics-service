package io.wlailson.github.e_commerce_analytics_service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document("sales")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SaleAnalytics {

    @Id
    private String id;

    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime occurredAt;
}
