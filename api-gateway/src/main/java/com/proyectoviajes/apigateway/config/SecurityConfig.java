package com.proyectoviajes.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // URL directa a las claves públicas de Keycloak (JWKS)
    // Esto evita que Spring tenga que "descubrir" la configuración y falle con el issuer
    private final String jwkSetUri = "http://localhost:8080/auth/realms/viajes-ms-realm/protocol/openid-connect/certs";

    // Inyectamos el conversor de roles (ReactiveJwtAuthConverter) que creamos antes
    private final ReactiveJwtAuthConverter reactiveJwtAuthConverter;

    // Constructor para inyección de dependencias
    public SecurityConfig(ReactiveJwtAuthConverter reactiveJwtAuthConverter) {
        this.reactiveJwtAuthConverter = reactiveJwtAuthConverter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                // 1. Deshabilitar CSRF (no necesario para APIs stateless)
                .csrf(csrf -> csrf.disable())

                // 2. Configuración de rutas protegidas
                .authorizeExchange(exchange -> exchange
                        // .pathMatchers("/publico/**").permitAll() // Ejemplo para rutas públicas
                        .anyExchange().authenticated() // Todo lo demás requiere autenticación
                )

                // 3. Configuración del Resource Server (OAuth2 JWT)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                            // ✅ A: Usar el decodificador manual definido abajo (Soluciona error de inicio)
                            jwt.jwtDecoder(jwtDecoder());

                            // ✅ B: Usar el conversor de roles (Para leer ROLE_CLIENTE, etc.)
                            jwt.jwtAuthenticationConverter(reactiveJwtAuthConverter);
                        })
                );

        return http.build();
    }

    /**
     * Define manualmente el Bean ReactiveJwtDecoder.
     * Esto fuerza a Spring Security a usar la URL de certificados específica
     * en lugar de intentar adivinarla desde el issuer-uri.
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
}