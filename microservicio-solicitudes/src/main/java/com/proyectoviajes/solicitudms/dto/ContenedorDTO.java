package com.proyectoviajes.solicitudms.dto;

import lombok.Data;

@Data
public class ContenedorDTO {
    private Long id;
    private Double pesoKg;
    private Double volumenM3;
    private String estado;
    private String clienteKeycloakId;
}