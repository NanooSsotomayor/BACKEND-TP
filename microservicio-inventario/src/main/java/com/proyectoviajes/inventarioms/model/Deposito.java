package com.proyectoviajes.inventarioms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "depositos")
@Data
public class Deposito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // Dirección textual y geolocalización, clave para OSRM/Rutas
    private String direccion;
    private Double latitud;
    private Double longitud;

    // Costo de estadía diario, según regla de negocio [cite: 66]
    private Double costoEstadiaDiario;
}