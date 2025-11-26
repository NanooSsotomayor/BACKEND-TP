package com.proyectoviajes.inventarioms.controller;

import com.proyectoviajes.inventarioms.model.Contenedor;
import com.proyectoviajes.inventarioms.service.ContenedorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map; // Necesario para recibir el body simple

@RestController
@RequestMapping("/contenedores")
public class ContenedorController {

    private final ContenedorService contenedorService;

    public ContenedorController(ContenedorService contenedorService) {
        this.contenedorService = contenedorService;
    }

    /**
     * Endpoint interno consumido por el Microservicio Solicitudes para crear un contenedor.
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()") // Requiere token JWT válido
    public ResponseEntity<Contenedor> crearContenedor(@RequestBody Contenedor contenedor) {
        Contenedor nuevoContenedor = contenedorService.crearContenedor(contenedor);
        return ResponseEntity.ok(nuevoContenedor);
    }

    /**
     * Endpoint para consultar el estado de un contenedor (Seguimiento).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR')")
    public ResponseEntity<Contenedor> obtenerContenedorPorId(@PathVariable Long id) {
        return contenedorService.obtenerContenedorPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * ✅ NUEVO ENDPOINT: Actualizar solo el estado de un contenedor.
     * Se usa cuando el Operador confirma una solicitud.
     * Se espera un body JSON como: { "estado": "PROGRAMADA" }
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("isAuthenticated()") // Permitir a usuarios autenticados (servicios u operador)
    public ResponseEntity<Void> actualizarEstadoContenedor(
            @PathVariable Long id,
            @RequestBody Map<String, String> cambios) {

        String nuevoEstado = cambios.get("estado");
        if (nuevoEstado == null) {
            return ResponseEntity.badRequest().build();
        }


        contenedorService.actualizarEstado(id, nuevoEstado);

        return ResponseEntity.ok().build();
    }
}