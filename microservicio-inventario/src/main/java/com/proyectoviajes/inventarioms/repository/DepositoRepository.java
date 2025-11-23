package com.proyectoviajes.inventarioms.repository;

import com.proyectoviajes.inventarioms.model.Deposito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositoRepository extends JpaRepository<Deposito, Long> {

    // Spring Data JPA ya proporciona métodos como save(), findById(), findAll(), delete(), etc.

    // Opcional: Podrías añadir métodos personalizados si fuera necesario,
    // como buscar por coordenadas si el Depósito lo requiere.
    // Ejemplo: List<Deposito> findByLatitudAndLongitud(Double latitud, Double longitud);
}