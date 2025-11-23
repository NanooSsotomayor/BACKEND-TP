package com.proyectoviajes.inventarioms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "camiones")
@Data
public class Camion {

    @Id
    private String dominio; // Patente o identificador único [cite: 76]

    private Double capacidadPesoKg; // Capacidad máxima en peso [cite: 17]
    private Double capacidadVolumenM3; // Capacidad máxima en volumen [cite: 17]

    // Indica si el camión puede ser asignado a un tramo [cite: 16]
    private Boolean disponible = true;

    // Costos asociados al camión [cite: 67, 68]
    private Double costoBaseKm;
    private Double consumoCombustiblePromedioLts; // Consumo por kilómetro [cite: 17]

    // Datos del transportista asociado (FK al Microservicio Usuarios)
    private String transportistaKeycloakId;
}