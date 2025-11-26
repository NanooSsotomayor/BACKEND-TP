package com.proyectoviajes.viajems.service;

import com.proyectoviajes.viajems.dto.CostoEstimadoRequest;
import com.proyectoviajes.viajems.dto.CostoEstimadoResponse;
import com.proyectoviajes.viajems.dto.CalculoEstimadoResponse;
import com.proyectoviajes.viajems.dto.RutaCalculoDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Service
public class CalculoService {

    private final OsrmService osrmService;
    private final TarifaService tarifaService;
    private final ObjectMapper objectMapper;

    public CalculoService(OsrmService osrmService, TarifaService tarifaService, ObjectMapper objectMapper) {
        this.osrmService = osrmService;
        this.tarifaService = tarifaService;
        this.objectMapper = objectMapper;
    }

    public CalculoEstimadoResponse calcularEstimacionRuta(RutaCalculoDTO request) {

        // ✅ CORRECCIÓN CRÍTICA: Usamos los getters que coinciden con el DTO actualizado (volumenM3 y pesoKg)
        // Si antes usabas getVolumenContenedorM3(), ahora es getVolumenM3()
        Double volumenContenedorM3 = request.getVolumenM3();
        Double pesoContenedorKg = request.getPesoKg();

        Double distanciaKm = 0.0;
        Double tiempoEstimadoHoras = 0.0;
        Double costoEstimado = 0.0;

        // 1. OBTENER COORDENADAS DESDE LA LISTA
        List<double[]> puntosRuta = request.getPuntosRuta();

        if (puntosRuta == null || puntosRuta.size() < 2) {
            throw new IllegalArgumentException("La ruta debe tener al menos origen y destino.");
        }

        // Asumimos: [Latitud, Longitud]
        // 🛑 Origen es el primer punto (índice 0)
        double[] origenCoords = puntosRuta.get(0);
        double origenLat = origenCoords[0];
        double origenLon = origenCoords[1];

        // 🛑 Destino es el último punto (índice N-1)
        double[] destinoCoords = puntosRuta.get(puntosRuta.size() - 1);
        double destinoLat = destinoCoords[0];
        double destinoLon = destinoCoords[1];

        try {
            // 2. LLAMADA A OSRM (que espera Longitud, Latitud)
            // Nota: OsrmService recibe (latOrigin, lonOrigin, latDest, lonDest) -> Verifica el orden en tu OsrmService
            // Si tu OsrmService.getRouteDistance espera (lon, lat, lon, lat), déjalo así.
            // Si espera (lat, lon, lat, lon), inviértelo. Asumiré que tu código original estaba bien para tu OsrmService.
            String osrmResultJson = osrmService.getRouteDistance(origenLon, origenLat, destinoLon, destinoLat);

            // 2.1. Parsear la respuesta de OSRM (JSON)
            JsonNode root = objectMapper.readTree(osrmResultJson);

            if (root.path("routes").isEmpty()) {
                throw new RuntimeException("OSRM no encontró rutas entre los puntos indicados.");
            }

            // La distancia viene en metros, la convertimos a kilómetros
            distanciaKm = root.path("routes").path(0).path("distance").asDouble() / 1000.0;
            // El tiempo viene en segundos, lo convertimos a horas
            tiempoEstimadoHoras = root.path("routes").path(0).path("duration").asDouble() / 3600.0;

            // 3. CALCULAR EL COSTO (usando MS Tarifas)
            // Creamos el request para Tarifas pasando el volumen que recuperamos arriba
            CostoEstimadoRequest costoRequest = new CostoEstimadoRequest(
                    distanciaKm,
                    volumenContenedorM3, // ✅ Aquí pasamos el valor correcto (no null)
                    pesoContenedorKg
            );

            CostoEstimadoResponse tarifaResponse = tarifaService.getCostoEstimado(costoRequest);
            if (tarifaResponse != null) {
                costoEstimado = tarifaResponse.getCostoTotalEstimado();
            }

        } catch (ResourceAccessException e) {
            // 🛑 Error de Conexión
            System.err.println("❌ ERROR FATAL - CONEXIÓN: Fallo al contactar OSRM o MS Tarifas.");
            e.printStackTrace();
            throw new RuntimeException("Fallo al contactar un servicio externo (Verifique Docker Compose).", e);

        } catch (HttpClientErrorException e) {
            // 🛑 Error 4xx o 5xx del servicio externo
            System.err.println("❌ ERROR LÓGICO - HTTP: Recibido " + e.getStatusCode() + " de servicio externo.");
            System.err.println("Cuerpo de error: " + e.getResponseBodyAsString());
            e.printStackTrace();
            throw new RuntimeException("Fallo de negocio en servicio externo (Ver logs para detalles).", e);

        } catch (Exception e) {
            // 🛑 Otros Errores
            System.err.println("❌ ERROR INTERNO: Fallo durante el cálculo de ruta/costo.");
            e.printStackTrace();
            throw new RuntimeException("Fallo interno en cálculo de ruta: " + e.getMessage(), e);
        }

        // 4. CONSOLIDAR RESPUESTA
        CalculoEstimadoResponse response = new CalculoEstimadoResponse();
        response.setDistanciaTotalKm(distanciaKm);
        response.setTiempoEstimadoHoras(tiempoEstimadoHoras);
        response.setCostoEstimado(costoEstimado);

        return response;
    }
}