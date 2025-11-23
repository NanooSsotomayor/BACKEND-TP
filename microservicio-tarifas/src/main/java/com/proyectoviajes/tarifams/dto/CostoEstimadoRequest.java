package com.proyectoviajes.tarifams.dto;

import lombok.Data;

@Data
public class CostoEstimadoRequest {
    private Double distanciaKm;
    private Double pesoContenedorKg;
    private Double volumenContenedorM3;
}