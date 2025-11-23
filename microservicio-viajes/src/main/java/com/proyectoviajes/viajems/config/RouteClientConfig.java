package com.proyectoviajes.viajems.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RouteClientConfig {

    @Bean
    // Exponemos RestTemplate para inyectarlo en los servicios
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}