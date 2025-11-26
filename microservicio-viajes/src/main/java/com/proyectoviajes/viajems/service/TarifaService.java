package com.proyectoviajes.viajems.service;

import com.proyectoviajes.viajems.dto.CostoEstimadoRequest;
import com.proyectoviajes.viajems.dto.CostoEstimadoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder; // IMPORTANTE
import org.springframework.security.oauth2.jwt.Jwt; // IMPORTANTE
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TarifaService {

    private final RestTemplate restTemplate;
    private final String tarifasServiceUrl;

    public TarifaService(RestTemplate restTemplate, @Value("${microservices.tarifas.url}") String tarifasServiceUrl) {
        this.restTemplate = restTemplate;
        this.tarifasServiceUrl = tarifasServiceUrl;
    }

    /**
     * Llama al microservicio de Tarifas para obtener el costo estimado.
     * @param request Datos de distancia y volumen.
     * @return CostoEstimadoResponse con el precio.
     */
    public CostoEstimadoResponse getCostoEstimado(CostoEstimadoRequest request) {

        // 1. Obtener el token JWT de la sesión actual (el que vino de ms-solicitudes)
        String token = "";
        try {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            token = jwt.getTokenValue();
        } catch (Exception e) {
            // Si por alguna razón no hay token (ej: pruebas unitarias), logueamos pero seguimos
            // Aunque en producción esto causará un 401 en el siguiente paso.
            System.err.println("No se encontró token en el contexto de seguridad: " + e.getMessage());
        }

        // 2. Preparar Headers con el Token (Propagación)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!token.isEmpty()) {
            headers.setBearerAuth(token); // ✅ Aquí agregamos la "llave" para entrar a Tarifas
        }

        HttpEntity<CostoEstimadoRequest> entity = new HttpEntity<>(request, headers);

        // 3. Ejecutar POST al endpoint de Tarifas
        String url = tarifasServiceUrl + "/costo-estimado";

        // Usamos try-catch para que si Tarifas falla, no explote todo el servicio
        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    CostoEstimadoResponse.class
            ).getBody();
        } catch (Exception e) {
            // Si falla tarifación, devolvemos un valor por defecto o relanzamos una excepción controlada
            System.err.println("Error llamando a MS Tarifas: " + e.getMessage());
            throw new RuntimeException("No se pudo calcular la tarifa externa", e);
        }
    }
}