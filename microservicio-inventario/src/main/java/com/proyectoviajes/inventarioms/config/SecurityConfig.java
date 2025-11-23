package com.proyectoviajes.inventarioms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // ✅ NUEVO: Habilita el uso de @PreAuthorize
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter; // ⚠️ NUEVO

    // Inyección de dependencias (por constructor)
    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 3. Configuración del Resource Server (OAuth2)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                    // ✅ CLAVE: Define el conversor para mapear roles
                    jwt.jwtAuthenticationConverter(jwtAuthConverter);
                }));

        return http.build();
    }
}