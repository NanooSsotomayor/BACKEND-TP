package com.proyectoviajes.solicitudms.repository;

import com.proyectoviajes.solicitudms.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    /**
     * Metodo de búsqueda para el seguimiento.
     * Busca todas las Solicitudes cuyo campo 'clienteKeycloakId' coincida con el parámetro.
     */
    List<Solicitud> findByClienteKeycloakId(String clienteKeycloakId);
}