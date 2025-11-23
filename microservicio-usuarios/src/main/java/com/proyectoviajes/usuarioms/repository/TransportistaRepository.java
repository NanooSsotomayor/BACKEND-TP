package com.proyectoviajes.usuarioms.repository;

import com.proyectoviajes.usuarioms.model.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportistaRepository extends JpaRepository<Transportista, Long> {
    // Metodo para buscar un Transportista por su ID de Keycloak (útil al iniciar sesión)
    Transportista findByKeycloakId(String keycloakId);
}