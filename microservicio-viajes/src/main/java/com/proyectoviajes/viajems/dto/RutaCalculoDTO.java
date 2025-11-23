package com.proyectoviajes.viajems.dto;

import lombok.Data;
import java.util.List;

@Data
public class RutaCalculoDTO {
    // Lista de pares [latitud, longitud] que definen la ruta (origen, depósitos, destino).
    private List<double[]> puntosRuta;

    // Peso y volumen del contenedor, necesario para validar camiones y calcular tarifas.
    private Double pesoContenedorKg;
    private Double volumenContenedorM3;
}