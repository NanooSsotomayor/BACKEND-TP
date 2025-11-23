package com.proyectoviajes.usuarioms.dto;

import lombok.Data;

@Data
public class ClienteDTO {
    private String keycloakId;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
}