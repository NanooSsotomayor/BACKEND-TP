package com.proyectoviajes.solicitudms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "rutas")
@Data
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK a la solicitud que esta ruta satisface
    private Long solicitudId;

    private Integer cantidadTramos;
    private Integer cantidadDepositos;

    // El costo y tiempo total de la ruta (redundante, pero útil para consultas)
    private Double costoTotalEstimado;
    private Long tiempoTotalEstimadoHoras;
}