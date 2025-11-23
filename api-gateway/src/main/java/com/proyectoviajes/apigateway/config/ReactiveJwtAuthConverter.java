package com.proyectoviajes.apigateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono; // CLAVE: Importación de Reactor

/**
 * Adapter para usar el JwtAuthConverter (no reactivo) en un contexto WebFlux.
 * Convierte el resultado a un Mono (flujo reactivo).
 */
@Component
public class ReactiveJwtAuthConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private final JwtAuthConverter delegate;

    // Inyectamos el conversor original no reactivo
    public ReactiveJwtAuthConverter(JwtAuthConverter delegate) {
        this.delegate = delegate;
    }

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        // Envolvemos la conversión del delegado en un Mono.just()
        return Mono.just(delegate.convert(jwt));
    }
}