package com.proyectoviajes.inventarioms.repository;

import com.proyectoviajes.inventarioms.model.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CamionRepository extends JpaRepository<Camion, Long> {

    // Spring Data JPA ya proporciona métodos como save(), findById(), findAll(), delete(), etc.
}