package com.proyectoviajes.inventarioms.repository;

import com.proyectoviajes.inventarioms.model.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CamionRepository extends JpaRepository<Camion, String> {

    /**
     * Busca camiones según su estado de disponibilidad.
     * Usado por el endpoint GET /camiones/libres
     * * @param disponible true para buscar libres, false para ocupados
     * @return Lista de camiones que coinciden
     */
    List<Camion> findByDisponible(Boolean disponible);
}