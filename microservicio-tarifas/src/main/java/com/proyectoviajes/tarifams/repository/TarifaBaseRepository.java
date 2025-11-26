package com.proyectoviajes.tarifams.repository;

import com.proyectoviajes.tarifams.model.TarifaBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TarifaBaseRepository extends JpaRepository<TarifaBase, Long> {

    /**
     * Busca la tarifa base cuyo rango de volumen incluya el volumen del contenedor solicitado.
     * Ejemplo: Si el contenedor tiene 15m3, busca una tarifa con min <= 15 y max >= 15.
     */
    Optional<TarifaBase> findFirstByVolumenMinimoM3LessThanEqualAndVolumenMaximoM3GreaterThanEqual(Double volumen, Double volumenMismo);
}