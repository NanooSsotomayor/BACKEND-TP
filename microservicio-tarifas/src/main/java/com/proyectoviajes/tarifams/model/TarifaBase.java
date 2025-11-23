package com.proyectoviajes.tarifams.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tarifas_base")
@Data
public class TarifaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Rango de volumen al que aplica esta tarifa (ej: 0-10 m3)
    private Double volumenMinimoM3;
    private Double volumenMaximoM3;

    // Valor fijo por kilómetro (dependiente del volumen)
    private Double costoKmBase;

    // Costo del litro de combustible configurado
    private Double costoLitroCombustible;

    // Costo fijo por gestión (según cantidad de tramos) [cite: 103]
    private Double cargoGestionTramo;
}