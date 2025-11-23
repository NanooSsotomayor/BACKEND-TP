package com.proyectoviajes.solicitudms.dto;

import lombok.Data;
import java.util.List;

@Data
public class RutaCalculoDTO {
    private List<double[]> puntosRuta;
    private Double pesoContenedorKg;
    private Double volumenContenedorM3;
}