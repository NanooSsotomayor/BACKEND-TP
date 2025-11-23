package com.proyectoviajes.tarifams.controller;

import com.proyectoviajes.tarifams.dto.CostoEstimadoRequest;
import com.proyectoviajes.tarifams.dto.CostoEstimadoResponse;
import com.proyectoviajes.tarifams.service.TarifaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/tarifas")
public class TarifaController {

    private final TarifaService tarifaService;

    public TarifaController(TarifaService tarifaService) {
        this.tarifaService = tarifaService;
    }

    /**
     * Endpoint interno consumido por el Microservicio Viajes para obtener la estimación de costos.
     */
    @PostMapping("/costo-estimado")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CostoEstimadoResponse> calcularCostoEstimado(@RequestBody CostoEstimadoRequest request) {

        CostoEstimadoResponse response = tarifaService.calcularCostoEstimado(request);
        return ResponseEntity.ok(response);
    }
}