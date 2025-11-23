package com.proyectoviajes.inventarioms.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Convierte un JWT (Json Web Token) en un objeto JwtAuthenticationToken,
 * extrayendo roles específicos de Keycloak.
 */
@Component
public class JwtAuthConverter implements Converter<Jwt, JwtAuthenticationToken> {

    // Necesario para extraer los roles por defecto (SCOPEs)
    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {

        // 1. Obtiene las autoridades por defecto de Spring Security (SCOPEs)
        Collection<GrantedAuthority> authorities = Stream.concat(
                defaultGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractRealmRoles(jwt).stream() // 2. Combina con los roles del Realm
        ).collect(Collectors.toSet());

        // El claim 'preferred_username' es común para usar como nombre de usuario.
        String principalClaimName = "preferred_username";

        return new JwtAuthenticationToken(jwt, authorities, principalClaimName);
    }

    /**
     * Extrae los roles del Realm de Keycloak que generalmente están anidados en el claim 'realm_access'.
     * Keycloak envía los roles como 'realm_access': { 'roles': [...] }
     */
    private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt jwt) {

        // El claim 'realm_access' contiene un mapa.
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");

        // Los roles son una lista dentro de ese mapa.
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAccess.get("roles");

        if (roles == null) {
            return Set.of();
        }

        // Convierte cada rol en un SimpleGrantedAuthority, añadiendo el prefijo 'ROLE_'.
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toSet());
    }
}