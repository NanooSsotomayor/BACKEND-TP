package com.proyectoviajes.viajems.controller;

import com.proyectoviajes.viajems.dto.CalculoEstimadoResponse;
import com.proyectoviajes.viajems.dto.RutaCalculoDTO;
import com.proyectoviajes.viajems.service.CalculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rutas")
public class RutaController {

    private final CalculoService calculoService;

    public RutaController(CalculoService calculoService) {
        this.calculoService = calculoService;
    }

    @PostMapping("/estimacion")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<CalculoEstimadoResponse> obtenerEstimacion(@RequestBody RutaCalculoDTO request) {

        CalculoEstimadoResponse response = calculoService.calcularEstimacionRuta(request);

        return ResponseEntity.ok(response);
    }
}