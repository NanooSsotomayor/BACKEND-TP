package com.proyectoviajes.solicitudms.controller;

import com.proyectoviajes.solicitudms.dto.SolicitudRequest;
import com.proyectoviajes.solicitudms.model.Solicitud;
import com.proyectoviajes.solicitudms.service.SolicitudService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // 1. Registrar Solicitud (Cliente) -> Crea en estado BORRADOR
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Solicitud> registrarSolicitud(
            @RequestBody SolicitudRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String clienteKeycloakId = jwt.getSubject();
        String token = jwt.getTokenValue();
        Solicitud nuevaSolicitud = solicitudService.registrarNuevaSolicitud(request, clienteKeycloakId, token);
        return ResponseEntity.ok(nuevaSolicitud);
    }

    // 2. Consultar Seguimiento (Cliente)
    @GetMapping("/seguimiento")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<Solicitud>> obtenerSolicitudesCliente(
            @AuthenticationPrincipal Jwt jwt) {
        String clienteKeycloakId = jwt.getSubject();
        return ResponseEntity.ok(solicitudService.obtenerSolicitudesCliente(clienteKeycloakId));
    }

    // 3. ✅ NUEVO: Confirmar/Programar Solicitud (Operador)
    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('OPERADOR')") // Solo el Operador puede confirmar
    public ResponseEntity<Solicitud> confirmarSolicitud(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        String token = jwt.getTokenValue();
        Solicitud confirmada = solicitudService.confirmarSolicitud(id, token);
        return ResponseEntity.ok(confirmada);
    }
}