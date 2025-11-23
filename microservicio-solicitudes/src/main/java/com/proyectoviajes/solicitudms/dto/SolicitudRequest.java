package com.proyectoviajes.solicitudms.dto;

import lombok.Data;

@Data
public class SolicitudRequest {
    // Datos del contenedor a crear (Requisito 1.1 TPI)
    private Double pesoKg;
    private Double volumenM3;

    // Datos del cliente a crear/validar (Requisito 1.2 TPI)
    private String clienteEmail;
    private String clienteTelefono;
    private String clienteNombre;

    // Ubicaciones de la solicitud
    private String origenDireccion;
    private Double origenLatitud;
    private Double origenLongitud;
    private String destinoDireccion;
    private Double destinoLatitud;
    private Double destinoLongitud;
}