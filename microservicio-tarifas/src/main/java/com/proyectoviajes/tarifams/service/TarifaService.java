package com.proyectoviajes.tarifams.service;

import com.proyectoviajes.tarifams.dto.CostoEstimadoRequest;
import com.proyectoviajes.tarifams.dto.CostoEstimadoResponse;
import com.proyectoviajes.tarifams.model.TarifaBase;
import com.proyectoviajes.tarifams.repository.TarifaBaseRepository;
import org.springframework.stereotype.Service;

@Service
public class TarifaService {

    private final TarifaBaseRepository tarifaBaseRepository;

    // Valor promedio de consumo para estimaciones (0.3 Litros por Km, es decir, 30L cada 100km)
    private static final Double CONSUMO_PROMEDIO_GENERAL_LTS_KM = 0.3;

    public TarifaService(TarifaBaseRepository tarifaBaseRepository) {
        this.tarifaBaseRepository = tarifaBaseRepository;
    }

    public CostoEstimadoResponse calcularCostoEstimado(CostoEstimadoRequest request) {

        Double volumen = request.getVolumenContenedorM3();
        Double distancia = request.getDistanciaKm();

        // 1. Buscar la tarifa aplicable según el volumen
        // Pasamos el volumen dos veces porque la query es: min <= vol AND max >= vol
        TarifaBase tarifa = tarifaBaseRepository
                .findFirstByVolumenMinimoM3LessThanEqualAndVolumenMaximoM3GreaterThanEqual(volumen, volumen)
                .orElseThrow(() -> new RuntimeException("No se encontró tarifa base para el volumen: " + volumen));

        // 2. Calcular Costo Base por Km (depende del volumen según la tarifa hallada)
        // Fórmula: Distancia * CostoKmBase
        Double costoRecorrido = distancia * tarifa.getCostoKmBase();

        // 3. Calcular Costo de Combustible Estimado
        // Fórmula: (ConsumoPromedio * PrecioLitro) * Distancia
        Double costoCombustible = (CONSUMO_PROMEDIO_GENERAL_LTS_KM * tarifa.getCostoLitroCombustible()) * distancia;

        // 4. Cargos de Gestión
        // Se cobra un cargo fijo por tramo (asumimos 1 tramo para la estimación global)
        Double cargosGestion = tarifa.getCargoGestionTramo();

        // 5. Suma Total
        Double costoTotal = costoRecorrido + costoCombustible + cargosGestion;

        // Retornar respuesta
        CostoEstimadoResponse response = new CostoEstimadoResponse();
        response.setCostoTotalEstimado(costoTotal);

        return response;
    }
}