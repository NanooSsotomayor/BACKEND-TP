package com.proyectoviajes.solicitudms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class InternalClientConfig {

    @Bean
    public RestTemplate internalRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.setInterceptors(Collections.singletonList(new TokenRelayInterceptor()));

        return restTemplate;
    }
}