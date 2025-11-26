package com.proyectoviajes.viajems.dto;

import lombok.Data;

@Data
public class CalculoEstimadoResponse {
    private Double distanciaTotalKm;
    private Double tiempoEstimadoHoras;
    private Double costoEstimado;
}