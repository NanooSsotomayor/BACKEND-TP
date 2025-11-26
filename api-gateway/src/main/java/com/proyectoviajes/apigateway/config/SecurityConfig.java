package com.proyectoviajes.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    // Leer la URI JWKS desde propiedades (soporta perfiles local/docker)
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

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
                            // Usar el decodificador definido abajo, que lee jwkSetUri de propiedades
                            jwt.jwtDecoder(jwtDecoder());

                            // Usar el conversor de roles (Para leer ROLE_CLIENTE, etc.)
                            jwt.jwtAuthenticationConverter(reactiveJwtAuthConverter);
                        })
                );

        return http.build();
    }

    /**
     * Define manualmente el Bean ReactiveJwtDecoder.
     * Lee la propiedad jwkSetUri desde `application*.yml`.
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        log.info("Creating NimbusReactiveJwtDecoder with jwkSetUri={}", jwkSetUri);
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
}