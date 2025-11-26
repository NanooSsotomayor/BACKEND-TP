package com.proyectoviajes.viajems.controller;

import com.proyectoviajes.viajems.dto.CalculoEstimadoResponse;
import com.proyectoviajes.viajems.dto.RutaCalculoDTO;
import com.proyectoviajes.viajems.dto.RutaSugeridaDTO;
import com.proyectoviajes.viajems.service.CalculoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rutas")
public class RutaController {

    private final CalculoService calculoService;

    @Value("${viajes.default.cost-per-km:10.0}")
    private double costPerKm;

    @Value("${viajes.default.avg-speed-kmh:50.0}")
    private double avgSpeedKmh;

    public RutaController(CalculoService calculoService) {
        this.calculoService = calculoService;
    }

    @PostMapping("/estimacion")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<CalculoEstimadoResponse> obtenerEstimacion(@RequestBody RutaCalculoDTO request) {

        CalculoEstimadoResponse response = calculoService.calcularEstimacionRuta(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sugeridas")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<RutaSugeridaDTO> obtenerRutasSugeridas(
            @RequestParam("origenLat") double origenLat,
            @RequestParam("origenLon") double origenLon,
            @RequestParam("destinoLat") double destinoLat,
            @RequestParam("destinoLon") double destinoLon
    ) {
        // construimos puntos (origen -> destino)
        List<double[]> puntos = new ArrayList<>();
        puntos.add(new double[]{origenLat, origenLon});
        puntos.add(new double[]{destinoLat, destinoLon});

        double distancia = haversineKm(origenLat, origenLon, destinoLat, destinoLon);
        double costoEstimado = distancia * costPerKm;
        double tiempoEstimadoHoras = distancia / avgSpeedKmh;

        RutaSugeridaDTO dto = new RutaSugeridaDTO();
        dto.setPuntosRuta(puntos);
        dto.setDistanciaKm(distancia);
        dto.setCostoEstimado(costoEstimado);
        dto.setTiempoEstimadoHoras((long)Math.round(tiempoEstimadoHoras));

        return ResponseEntity.ok(dto);
    }

    // Haversine formula
    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to km
        return Math.round(distance * 1000.0) / 1000.0; // round to meters precision
    }
}