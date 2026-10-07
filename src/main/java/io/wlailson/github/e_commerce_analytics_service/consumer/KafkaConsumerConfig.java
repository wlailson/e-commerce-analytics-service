package io.wlailson.github.e_commerce_analytics_service.consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JacksonJsonMessageConverter;

@Configuration(proxyBeanMethods = false)
public class KafkaConsumerConfig {

    @Bean
    public JacksonJsonMessageConverter kafkaJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
