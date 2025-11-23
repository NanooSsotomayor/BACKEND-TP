package com.proyectoviajes.usuarioms.repository;

import com.proyectoviajes.usuarioms.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Metodo para buscar un cliente por su ID de Keycloak (útil al iniciar sesión)
    Cliente findByKeycloakId(String keycloakId);
}