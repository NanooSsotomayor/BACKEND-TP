package com.proyectoviajes.usuarioms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "transportistas")
@Data
public class Transportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keycloakId;

    private String nombre;
    private String apellido;
    private String telefono; // Dato del transportista [cite: 18]

    // Este campo podría ser un Foreign Key al Microservicio de Inventario/Camiones
    // Por ahora, solo guardamos el identificador del camión asociado
    private String dominioCamionAsignado;
}