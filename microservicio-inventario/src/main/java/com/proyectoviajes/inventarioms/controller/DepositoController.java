package com.proyectoviajes.inventarioms.controller;

import com.proyectoviajes.inventarioms.model.Deposito;
import com.proyectoviajes.inventarioms.repository.DepositoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/depositos")
public class DepositoController {

    private final DepositoRepository depositoRepository;

    public DepositoController(DepositoRepository depositoRepository) {
        this.depositoRepository = depositoRepository;
    }

    /**
     * Crea un nuevo depósito.
     * Requisito: Carga de depósitos por el Operador.
     */
    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Deposito> crearDeposito(@RequestBody Deposito deposito) {
        return ResponseEntity.ok(depositoRepository.save(deposito));
    }

    /**
     * Lista todos los depósitos disponibles.
     * Útil para que el operador vea la red de distribución.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<Deposito>> obtenerTodos() {
        return ResponseEntity.ok(depositoRepository.findAll());
    }

    /**
     * Busca un depósito específico por su ID.
     * Útil para consultar detalles de ubicación o costos.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<Deposito> obtenerDepositoPorId(@PathVariable Long id) {
        return depositoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Actualiza la información de un depósito existente.
     * Requisito: Actualización de datos por el Operador.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Deposito> actualizarDeposito(@PathVariable Long id, @RequestBody Deposito depositoDetalles) {
        return depositoRepository.findById(id)
                .map(deposito -> {
                    deposito.setNombre(depositoDetalles.getNombre());
                    deposito.setDireccion(depositoDetalles.getDireccion());
                    deposito.setLatitud(depositoDetalles.getLatitud());
                    deposito.setLongitud(depositoDetalles.getLongitud());
                    deposito.setCostoEstadiaDiario(depositoDetalles.getCostoEstadiaDiario());
                    return ResponseEntity.ok(depositoRepository.save(deposito));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Elimina un depósito (Opcional, pero buena práctica CRUD).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarDeposito(@PathVariable Long id) {
        if (!depositoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        depositoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}