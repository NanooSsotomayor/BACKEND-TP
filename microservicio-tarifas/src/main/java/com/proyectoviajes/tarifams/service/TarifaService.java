package com.proyectoviajes.tarifams.service;

import com.proyectoviajes.tarifams.dto.CostoEstimadoRequest;
import com.proyectoviajes.tarifams.dto.CostoEstimadoResponse;
import com.proyectoviajes.tarifams.repository.TarifaBaseRepository;
import org.springframework.stereotype.Service;

@Service
public class TarifaService {

    private final TarifaBaseRepository tarifaBaseRepository;
    private static final Double CARGO_FIJO_GESTION = 500.0; // Valor de ejemplo

    public TarifaService(TarifaBaseRepository tarifaBaseRepository) {
        this.tarifaBaseRepository = tarifaBaseRepository;
    }

    public CostoEstimadoResponse calcularCostoEstimado(CostoEstimadoRequest request) {

        // 1. OBTENER TARIFA BASE (Según volumen del contenedor)
        // La entidad TarifaBase tiene el método findFirstByVolumen...
        // Nota: Asumiremos por simplicidad que encontramos una tarifa.
        Double costoKmBase = 10.0; // Simulamos la tarifa si no se encuentra en DB
        Double costoLitroCombustible = 500.0; // Simulamos el valor del litro

        // 2. CALCULAR COSTO DE RECORRIDO BASE
        Double costoRecorrido = request.getDistanciaKm() * costoKmBase;

        // 3. CALCULAR COSTO DE COMBUSTIBLE (Usando consumo promedio general, según TPI)
        // Nota: El consumo promedio del camión (ej: 0.2 L/Km) debería venir de MS Inventario/Camiones.
        Double consumoPromedioLts = 0.2; // 200 ml por Km (ejemplo)
        Double costoCombustible = request.getDistanciaKm() * consumoPromedioLts * costoLitroCombustible;

        // 4. CALCULAR CARGOS DE GESTIÓN (Asumiendo 1 tramo)
        Double cargosGestion = CARGO_FIJO_GESTION * 1.0;

        Double costoTotal = costoRecorrido + costoCombustible + cargosGestion;

        CostoEstimadoResponse response = new CostoEstimadoResponse();
        response.setCostoTotalEstimado(costoTotal);
        return response;
    }
}