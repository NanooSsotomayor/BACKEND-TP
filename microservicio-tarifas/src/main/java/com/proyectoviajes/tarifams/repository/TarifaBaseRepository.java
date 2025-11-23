package com.proyectoviajes.tarifams.repository;

import com.proyectoviajes.tarifams.model.TarifaBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarifaBaseRepository extends JpaRepository<TarifaBase, Long> {

    /**
     * Requisito: Encontrar la tarifa base aplicable para un volumen dado de contenedor.
     */
    TarifaBase findFirstByVolumenMinimoM3LessThanEqualAndVolumenMaximoM3GreaterThanEqual(
            Double volumen, Double volumen2);
}