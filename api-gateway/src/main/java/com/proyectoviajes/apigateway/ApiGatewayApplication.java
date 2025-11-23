package com.proyectoviajes.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// Se eliminan las exclusiones para permitir que Spring Boot registre los beans
// relacionados con OAuth2 Resource Server (ReactiveJwtDecoder, etc.).

// ✅ EXCLUIR el autoconfigure de seguridad de Spring MVC (servlet)
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}