package io.wlailson.github.e_commerce_analytics_service.consumer;

import io.wlailson.github.e_commerce_analytics_service.dto.OrderCreateMessage;
import io.wlailson.github.e_commerce_analytics_service.dto.OrderUpdatedMessage;
import io.wlailson.github.e_commerce_analytics_service.dto.PaymentCreatedMessage;
import io.wlailson.github.e_commerce_analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = "${spring.kafka.consumer.topics.orderCreated}")
    public void orderCreated(OrderCreateMessage message) {
        analyticsService.processOrderCreated(message);
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topics.orderUpdated}")
    public void orderUpdated(OrderUpdatedMessage message) {
        analyticsService.processOrderUpdated(message);
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topics.paymentCreated}")
    public void paymentCreated(PaymentCreatedMessage message) {
        analyticsService.processPaymentCreated(message);
    }

}
