package com.proyectoviajes.solicitudms.controller;

import com.proyectoviajes.solicitudms.dto.SolicitudRequest;
import com.proyectoviajes.solicitudms.model.Solicitud;
import com.proyectoviajes.solicitudms.service.SolicitudService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    /**
     * Endpoint: Registrar una nueva solicitud de transporte de contenedor (Cliente).
     * Protegido por autenticación (y roles).
     */
    @PostMapping
    public ResponseEntity<Solicitud> registrarSolicitud(
            @RequestBody SolicitudRequest request,
            @AuthenticationPrincipal Jwt jwt) { // Inyectamos el token decodificado

        String clienteKeycloakId = jwt.getSubject();

        // ✅ NUEVO: Obtenemos el String del token para reenviarlo
        String token = jwt.getTokenValue();

        // Pasamos el token al servicio
        Solicitud nuevaSolicitud = solicitudService.registrarNuevaSolicitud(request, clienteKeycloakId, token);

        return ResponseEntity.ok(nuevaSolicitud);
    }
}