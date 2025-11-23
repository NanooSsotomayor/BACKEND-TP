package com.proyectoviajes.inventarioms.service;

import com.proyectoviajes.inventarioms.model.Contenedor;
import com.proyectoviajes.inventarioms.repository.ContenedorRepository;
import org.springframework.stereotype.Service;
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
    public Contenedor crearContenedor(Contenedor contenedor) {
        // Aquí podríamos añadir lógica de validación, por ejemplo:
        // if (contenedor.getPesoKg() <= 0) throw new IllegalArgumentException("Peso inválido.");

        return contenedorRepository.save(contenedor);
    }

    /**
     * Obtiene un Contenedor por su ID usando el Repositorio.
     */
    public Optional<Contenedor> obtenerContenedorPorId(Long id) {
        return contenedorRepository.findById(id); // Acceso legal dentro del Service
    }
}