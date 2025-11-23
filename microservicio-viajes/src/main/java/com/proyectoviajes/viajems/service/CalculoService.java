package com.proyectoviajes.viajems.service;

import com.proyectoviajes.viajems.dto.RutaCalculoDTO;
import com.proyectoviajes.viajems.dto.CalculoEstimadoResponse;
import com.proyectoviajes.viajems.dto.CostoEstimadoRequest;
import com.proyectoviajes.viajems.dto.CostoEstimadoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class CalculoService {

    private final OsrmService osrmService;
    private final RestTemplate internalRestTemplate;

    @Value("${microservices.tarifas.url}")
    private String tarifasServiceUrl;

    public CalculoService(OsrmService osrmService, RestTemplate osrmRestTemplate) {
        this.osrmService = osrmService;
        this.internalRestTemplate = osrmRestTemplate;
    }

    public CalculoEstimadoResponse calcularEstimacionRuta(RutaCalculoDTO request) {

        // 1. OBTENER DISTANCIA/TIEMPO (usando OSRM)
        List<double[]> puntosRuta = request.getPuntosRuta();
        double[] origen = puntosRuta.get(0);
        double[] destino = puntosRuta.get(puntosRuta.size() - 1);

        String osrmResult = osrmService.getRouteDistance(origen[0], origen[1], destino[0], destino[1]);

        // --- SIMULACIÓN DE RESULTADOS ---
        Double distanciaKm = 150.0;
        Long tiempoEstimadoSegundos = 10800L;

        // 2. LLAMADA A MS TARIFAS
        CostoEstimadoRequest costoRequest = new CostoEstimadoRequest();
        costoRequest.setDistanciaKm(distanciaKm);
        costoRequest.setVolumenContenedorM3(request.getVolumenContenedorM3());

        CostoEstimadoResponse tarifaResponse = internalRestTemplate.postForObject(
                tarifasServiceUrl + "/costo-estimado",
                costoRequest,
                CostoEstimadoResponse.class
        );

        // 3. CONSOLIDAR RESPUESTA
        CalculoEstimadoResponse response = new CalculoEstimadoResponse();
        response.setDistanciaTotalKm(distanciaKm);
        response.setTiempoEstimadoHoras(tiempoEstimadoSegundos / 3600);
        response.setCostoEstimado(tarifaResponse.getCostoTotalEstimado());

        return response;
    }
}