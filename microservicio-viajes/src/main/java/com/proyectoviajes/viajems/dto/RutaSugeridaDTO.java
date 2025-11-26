package com.proyectoviajes.viajems.dto;

import lombok.Data;
import java.util.List;

@Data
public class RutaSugeridaDTO {
    private List<double[]> puntosRuta;
    private Double distanciaKm;
    private Double costoEstimado;
    private Long tiempoEstimadoHoras;
}

