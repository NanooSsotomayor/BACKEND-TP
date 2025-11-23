package com.proyectoviajes.inventarioms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "contenedores")
@Data
public class Contenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dimensiones y peso, restricciones para el camión [cite: 13, 72]
    private Double pesoKg;
    private Double volumenM3;

    // Estado y ubicación actual para seguimiento [cite: 21, 72]
    private String estado; // e.g., DEPOSITO, EN_TRANSITO, ENTREGADO

    // Asociación al Cliente (FK al Microservicio Usuarios)
    private String clienteKeycloakId;

    // Referencia al Depósito si el estado es DEPOSITO [cite: 22]
    private Long depositoActualId;
}