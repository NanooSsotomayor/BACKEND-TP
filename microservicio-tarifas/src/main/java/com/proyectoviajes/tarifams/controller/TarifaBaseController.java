package com.proyectoviajes.tarifams.controller;

import com.proyectoviajes.tarifams.model.TarifaBase;
import com.proyectoviajes.tarifams.repository.TarifaBaseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tarifas-base")
public class TarifaBaseController {

    private final TarifaBaseRepository tarifaBaseRepository;

    public TarifaBaseController(TarifaBaseRepository tarifaBaseRepository) {
        this.tarifaBaseRepository = tarifaBaseRepository;
    }

    /** Requisito: Carga y modifica parámetros de tarifación (Operador/Administrador). */
    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<TarifaBase> crearTarifa(@RequestBody TarifaBase tarifa) {
        return ResponseEntity.ok(tarifaBaseRepository.save(tarifa));
    }

    @GetMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<List<TarifaBase>> obtenerTodas() {
        return ResponseEntity.ok(tarifaBaseRepository.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<TarifaBase> actualizarTarifa(@PathVariable Long id, @RequestBody TarifaBase tarifa) {
        if (!tarifaBaseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tarifa.setId(id);
        return ResponseEntity.ok(tarifaBaseRepository.save(tarifa));
    }
}