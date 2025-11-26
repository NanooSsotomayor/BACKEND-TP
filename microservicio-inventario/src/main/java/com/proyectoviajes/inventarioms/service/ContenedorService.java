package com.proyectoviajes.inventarioms.service;

import com.proyectoviajes.inventarioms.model.Contenedor;
import com.proyectoviajes.inventarioms.repository.ContenedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante agregar esto

import java.util.Optional;

@Service
public class ContenedorService {

    private final ContenedorRepository contenedorRepository;

    public ContenedorService(ContenedorRepository contenedorRepository) {
        this.contenedorRepository = contenedorRepository;
    }

    /**
     * Persiste un nuevo contenedor en la base de datos compartida.
     * @param contenedor Objeto Contenedor a guardar.
     * @return El contenedor persistido con el ID generado.
     */
    @Transactional
    public Contenedor crearContenedor(Contenedor contenedor) {
        // Aquí podríamos añadir lógica de validación, por ejemplo:
        // if (contenedor.getPesoKg() <= 0) throw new IllegalArgumentException("Peso inválido.");
        return contenedorRepository.save(contenedor);
    }

    /**
     * Obtiene un Contenedor por su ID usando el Repositorio.
     */
    public Optional<Contenedor> obtenerContenedorPorId(Long id) {
        return contenedorRepository.findById(id);
    }

    // ----------------------------------------------------------------
    // ✅ MÉTODO NUEVO: Necesario para el flujo "Confirmar Solicitud"
    // ----------------------------------------------------------------
    @Transactional
    public void actualizarEstado(Long id, String nuevoEstado) {
        // 1. Buscamos el contenedor, si no existe lanzamos error
        Contenedor contenedor = contenedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenedor no encontrado con id: " + id));

        // 2. Actualizamos el valor
        contenedor.setEstado(nuevoEstado);

        // 3. Guardamos los cambios (JPA lo hace automático al final de la transacción, pero el save es explícito)
        contenedorRepository.save(contenedor);
    }
}