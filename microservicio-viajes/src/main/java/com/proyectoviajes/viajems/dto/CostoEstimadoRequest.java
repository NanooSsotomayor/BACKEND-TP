package com.proyectoviajes.viajems.dto;

import lombok.Data;

// Nota: Esta clase tiene los campos que el MS Tarifas necesita para su cálculo.
@Data
public class CostoEstimadoRequest {
    private Double distanciaKm;
    private Double pesoContenedorKg;
    private Double volumenContenedorM3;
}