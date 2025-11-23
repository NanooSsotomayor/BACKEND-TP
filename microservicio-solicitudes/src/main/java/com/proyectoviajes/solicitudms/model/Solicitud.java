package com.proyectoviajes.solicitudms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
@Data
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Asociaciones a otros microservicios (IDs)
    private Long contenedorId; // FK al MS Inventario
    private String clienteKeycloakId; // FK al MS Usuarios

    // Ubicaciones de origen y destino (geolocalización clave para rutas)
    private String origenDireccion;
    private Double origenLatitud;
    private Double origenLongitud;
    private String destinoDireccion;
    private Double destinoLatitud;
    private Double destinoLongitud;

    // Estado del ciclo de vida
    private String estado; // e.g., PROGRAMADA, EN_TRANSITO, ENTREGADA
    private LocalDateTime fechaCreacion;

    // Costos y tiempos (calculados por MS Viajes y MS Tarifas)
    private Double costoEstimado;
    private Long tiempoEstimadoHoras;
    private Double costoFinal;
    private Long tiempoRealHoras;
}