package com.proyectoviajes.viajems.dto;

import lombok.Data;

@Data
public class CostoEstimadoRequest {

    // Distancia calculada por OSRM
    private Double distanciaKm;

    // Volumen del contenedor (para determinar la tarifa base aplicable)
    private Double volumenContenedorM3;

    // Nota: Aunque el peso no se usa directamente en la fórmula de la TarifaBase,
    // se puede incluir si se requiere lógica más compleja o validación.
    private Double pesoContenedorKg;

    // Constructor auxiliar para simplificar la creación del objeto
    public CostoEstimadoRequest(Double distanciaKm, Double volumenContenedorM3, Double pesoContenedorKg) {
        this.distanciaKm = distanciaKm;
        this.volumenContenedorM3 = volumenContenedorM3;
        this.pesoContenedorKg = pesoContenedorKg;
    }

    // Constructor vacío para Jackson
    public CostoEstimadoRequest() {}
}