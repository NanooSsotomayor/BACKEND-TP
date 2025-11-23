package com.proyectoviajes.viajems.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OsrmService {

    private final RestTemplate restTemplate;
    private final String osrmBaseUrl;

    public OsrmService(RestTemplate restTemplate, @Value("${osrm.base-url}") String osrmBaseUrl) {
        this.restTemplate = restTemplate;
        this.osrmBaseUrl = osrmBaseUrl;
    }

    /**
     * Calcula la ruta y distancia entre dos puntos (lat, lon) usando OSRM.
     * La llamada a la API externa es un requisito clave del TPI. [cite: 92]
     * @param latOrigin Latitud de origen
     * @param lonOrigin Longitud de origen
     * @param latDest Latitud de destino
     * @param lonDest Longitud de destino
     * @return El JSON de respuesta de OSRM (contiene distancia y tiempo)
     */
    public String getRouteDistance(double latOrigin, double lonOrigin, double latDest, double lonDest) {
        // Formato de OSRM: /route/v1/driving/lon1,lat1;lon2,lat2
        String url = String.format("%s/route/v1/driving/%f,%f;%f,%f?overview=false",
                osrmBaseUrl, lonOrigin, latOrigin, lonDest, latDest);

        return restTemplate.getForObject(url, String.class);
    }
}