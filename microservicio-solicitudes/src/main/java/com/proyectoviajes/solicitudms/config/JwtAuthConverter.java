// java
package com.proyectoviajes.solicitudms.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<SimpleGrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<SimpleGrantedAuthority> extractAuthorities(Jwt jwt) {
        Object rolesClaim = jwt.getClaim("roles");
        if (rolesClaim instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) rolesClaim;
            return roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(Collectors.toList());
        }

        Object realm = jwt.getClaim("realm_access");
        if (realm instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> realmMap = (Map<String, Object>) realm;
            Object r = realmMap.get("roles");
            if (r instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) r;
                return roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }
}