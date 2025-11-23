package com.proyectoviajes.solicitudms.dto;

import lombok.Data;

@Data
public class CalculoEstimadoResponse {
    private Double distanciaTotalKm;
    private Long tiempoEstimadoHoras;
    private Double costoEstimado;
}