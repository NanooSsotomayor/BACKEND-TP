package com.proyectoviajes.viajems.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    // Nombre del cliente en Keycloak al que pertenece este microservicio (ajustar si es diferente)
    private final String resourceId = "viajes-ms";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractResourceRoles(jwt);
        // Usa 'preferred_username' como el nombre principal del usuario autenticado
        return new JwtAuthenticationToken(jwt, authorities, jwt.getClaimAsString("preferred_username"));
    }

    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
        // 1. Obtener la sección 'resource_access' del token
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        // 2. Verificar que el token tenga acceso a nuestro recurso (viajes-ms)
        if (resourceAccess == null || !resourceAccess.containsKey(resourceId)) {
            return List.of();
        }

        Map<String, Object> resource = (Map<String, Object>) resourceAccess.get(resourceId);

        // 3. Extraer la lista de roles
        Collection<String> roles = (Collection<String>) resource.get("roles");

        if (roles == null) {
            return List.of();
        }

        // 4. Convertir los roles a Spring Security Authorities
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Spring necesita el prefijo ROLE_
                .collect(Collectors.toSet());
    }
}