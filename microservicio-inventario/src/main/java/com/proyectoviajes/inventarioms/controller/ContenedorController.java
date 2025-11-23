package com.proyectoviajes.inventarioms.controller;

import com.proyectoviajes.inventarioms.model.Contenedor;
import com.proyectoviajes.inventarioms.service.ContenedorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/contenedores")
public class ContenedorController {

    private final ContenedorService contenedorService;

    public ContenedorController(ContenedorService contenedorService) {
        this.contenedorService = contenedorService;
    }

    /**
     * Endpoint interno consumido por el Microservicio Solicitudes para crear un contenedor.
     * Requisito: Debe estar protegido por Keycloak. Podríamos restringirlo solo a 'OPERADOR'
     * o, como es una llamada de servicio a servicio, a un rol interno.
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()") // Requiere token JWT válido (Mínimo de seguridad)
    public ResponseEntity<Contenedor> crearContenedor(@RequestBody Contenedor contenedor) {

        Contenedor nuevoContenedor = contenedorService.crearContenedor(contenedor);
        return ResponseEntity.ok(nuevoContenedor);
    }

    /**
     * Endpoint para consultar el estado de un contenedor (Seguimiento).
     * Requisito: Accesible por Cliente (para su seguimiento) u Operador.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR')")
    public ResponseEntity<Contenedor> obtenerContenedorPorId(@PathVariable Long id) {

        return contenedorService.obtenerContenedorPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}