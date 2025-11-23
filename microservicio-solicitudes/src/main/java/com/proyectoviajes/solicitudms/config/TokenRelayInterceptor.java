package com.proyectoviajes.solicitudms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.IOException;

// Este interceptor adjunta el token JWT del usuario actual a la petición saliente.
public class TokenRelayInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TokenRelayInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        // 1. Obtener el token del contexto de seguridad (si el usuario está autenticado)
        String token = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication();

        if (principal instanceof JwtAuthenticationToken) {
            token = ((JwtAuthenticationToken) principal).getToken().getTokenValue();
        }

        // 2. Si hay token, añadir el header Authorization
        if (token != null) {
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }

        logger.debug("Outgoing request to {} | Authorization: {}", request.getURI(), request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));

        return execution.execute(request, body);
    }
}