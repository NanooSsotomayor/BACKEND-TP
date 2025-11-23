package com.proyectoviajes.viajems.dto;

import lombok.Data;

@Data
public class CalculoEstimadoResponse {
    private Double distanciaTotalKm;
    private Long tiempoEstimadoHoras;
    private Double costoEstimado;
}