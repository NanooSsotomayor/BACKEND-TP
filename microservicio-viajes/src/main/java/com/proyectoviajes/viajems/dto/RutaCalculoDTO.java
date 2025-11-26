package com.proyectoviajes.viajems.dto;
import lombok.Data;
import java.util.List;

@Data
public class RutaCalculoDTO {
    private List<double[]> puntosRuta;
    // Estos nombres deben coincidir con lo que envía ms-solicitudes
    private Double pesoKg;     // En solicitudes se llama pesoKg
    private Double volumenM3;  // En solicitudes se llama volumenM3
}