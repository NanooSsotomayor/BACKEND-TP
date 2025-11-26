package com.proyectoviajes.inventarioms.controller;

import com.proyectoviajes.inventarioms.model.Camion;
import com.proyectoviajes.inventarioms.repository.CamionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/camiones")
public class CamionController {

    private final CamionRepository camionRepository;

    public CamionController(CamionRepository camionRepository) {
        this.camionRepository = camionRepository;
    }

    /**
     * Registrar un nuevo camión en la flota.
     * Requisito TPI: Carga de camiones por el Operador.
     */
    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Camion> crearCamion(@RequestBody Camion camion) {
        // Aquí podrías validar si el transportistaKeycloakId existe llamando al MS Usuarios
        return ResponseEntity.ok(camionRepository.save(camion));
    }

    /**
     * Listar todos los camiones.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<Camion>> obtenerTodos() {
        return ResponseEntity.ok(camionRepository.findAll());
    }

    /**
     * Requisito TPI: Permite determinar los camiones libres.
     * Fundamental para la lógica de asignación de rutas.
     */
    @GetMapping("/libres")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<Camion>> obtenerCamionesLibres() {
        // Requiere que el método findByDisponible exista en el repositorio
        return ResponseEntity.ok(camionRepository.findByDisponible(true));
    }

    /**
     * Consultar datos de un camión específico por su patente/dominio.
     */
    @GetMapping("/{dominio}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR', 'TRANSPORTISTA')")
    public ResponseEntity<Camion> obtenerCamionPorDominio(@PathVariable String dominio) {
        return camionRepository.findById(dominio)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Actualizar datos de un camión (ej. cambiar costos o disponibilidad).
     * Requisito TPI: Actualizar camiones.
     */
    @PutMapping("/{dominio}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Camion> actualizarCamion(@PathVariable String dominio, @RequestBody Camion camionDetalles) {
        return camionRepository.findById(dominio)
                .map(camion -> {
                    // Actualizamos los campos permitidos
                    camion.setCapacidadPesoKg(camionDetalles.getCapacidadPesoKg());
                    camion.setCapacidadVolumenM3(camionDetalles.getCapacidadVolumenM3());
                    camion.setDisponible(camionDetalles.getDisponible());
                    camion.setCostoBaseKm(camionDetalles.getCostoBaseKm());
                    camion.setConsumoCombustiblePromedioLts(camionDetalles.getConsumoCombustiblePromedioLts());
                    camion.setTransportistaKeycloakId(camionDetalles.getTransportistaKeycloakId());

                    return ResponseEntity.ok(camionRepository.save(camion));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Eliminar un camión de la flota.
     */
    @DeleteMapping("/{dominio}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarCamion(@PathVariable String dominio) {
        if (!camionRepository.existsById(dominio)) {
            return ResponseEntity.notFound().build();
        }
        camionRepository.deleteById(dominio);
        return ResponseEntity.noContent().build();
    }
}