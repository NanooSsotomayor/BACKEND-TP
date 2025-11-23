package com.proyectoviajes.solicitudms.repository;

import com.proyectoviajes.solicitudms.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {

    // Spring Data JPA provee los métodos CRUD básicos (save, findById, findAll, etc.).

    /**
     * Requisito Funcional: Consultar el estado del transporte de un contenedor (Cliente).
     * Este metodo es crucial para que un cliente pueda ver sus propias solicitudes.
     * @param clienteKeycloakId El ID del cliente (extraído del token JWT)
     * @return Lista de solicitudes asociadas a ese cliente.
     */
}