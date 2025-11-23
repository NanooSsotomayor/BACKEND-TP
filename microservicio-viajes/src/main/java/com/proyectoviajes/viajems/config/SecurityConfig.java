package com.proyectoviajes.viajems.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Importa tu conversor de JWT. Asume que está en el mismo paquete o en un módulo compartido
import com.proyectoviajes.viajems.config.JwtAuthConverter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    // Inyecta el conversor de roles para Keycloak
    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Deshabilitar CSRF, necesario para APIs REST
                .csrf(csrf -> csrf.disable())

                // 2. Configuración de autorización
                .authorizeHttpRequests(authorize -> authorize
                        // Permitir acceso sin autenticación a Swagger/OpenAPI y endpoints de salud
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**", "/error").permitAll()

                        // Asegurar todos los demás endpoints REQUIEREN AUTENTICACIÓN
                        .anyRequest().authenticated()
                )

                // 3. Configuración del Resource Server (JWT)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                // Aplica el conversor de JWT para extraer roles y authorities
                                .jwtAuthenticationConverter(jwtAuthConverter)
                        )
                );

        return http.build();
    }
}