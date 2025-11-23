package com.proyectoviajes.inventarioms.repository;

import com.proyectoviajes.inventarioms.model.Contenedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {

    // Spring Data JPA ya proporciona métodos como save(), findById(), findAll(), delete(), etc.
}