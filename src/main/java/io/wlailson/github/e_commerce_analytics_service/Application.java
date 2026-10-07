package io.wlailson.github.e_commerce_analytics_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "E-commerce Analytics Service API",
        version = "1.0.0",
        description = "API para consulta de vendas e indicadores analíticos."
))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token JWT necessário para acessar os endpoints de analytics."
)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}