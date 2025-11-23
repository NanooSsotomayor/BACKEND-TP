package com.proyectoviajes.solicitudms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tramos")
@Data
public class Tramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rutaId; // FK a la ruta a la que pertenece
    private Integer numeroSecuencia; // Orden del tramo dentro de la ruta

    // Puntos del tramo
    private Long origenDepositoId; // Nullable si es Origen del viaje
    private Long destinoDepositoId; // Nullable si es Destino final del viaje
    private String tipo; // e.g., ORIGEN_DEPOSITO, DEPOSITO_DEPOSITO, DEPOSITO_FINAL

    // Asignación de Camión (FK al MS Inventario)
    private String camionDominio;

    // Estado y Fechas
    private String estado; // e.g., ASIGNADO, INICIADO, FINALIZADO
    private LocalDateTime fechaHoraInicioEstimada;
    private LocalDateTime fechaHoraFinEstimada;
    private LocalDateTime fechaHoraInicioReal; // Registro del Transportista (Punto 7 TPI)
    private LocalDateTime fechaHoraFinReal; // Registro del Transportista (Punto 7 TPI)

    // Costos y Distancia (Calculados o Registrados)
    private Double distanciaKm; // Calculado con OSRM por el MS Viajes
    private Double costoAproximado;
    private Double costoReal;
}