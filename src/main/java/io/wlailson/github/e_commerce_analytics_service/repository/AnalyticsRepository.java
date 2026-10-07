package io.wlailson.github.e_commerce_analytics_service.repository;

import io.wlailson.github.e_commerce_analytics_service.domain.SaleAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalyticsRepository extends MongoRepository<SaleAnalytics, String> {

    Optional<SaleAnalytics> findByOrderId(Long orderId);
}
