package io.wlailson.github.e_commerce_analytics_service;

import org.springframework.boot.SpringApplication;

public class TestECommerceAnalyticsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(ECommerceAnalyticsServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
