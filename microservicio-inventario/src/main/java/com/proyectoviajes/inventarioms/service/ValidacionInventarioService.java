package com.proyectoviajes.inventarioms.service;

import com.proyectoviajes.inventarioms.model.Camion;
import org.springframework.stereotype.Service;

@Service
public class ValidacionInventarioService {

    // Metodo que verifica si el contenedor cabe en el camión
    public boolean validarCapacidad(Camion camion, double pesoContenedor, double volumenContenedor) {

        // Regla 1: No exceder el peso
        if (pesoContenedor > camion.getCapacidadPesoKg()) {
            return false;
        }

        // Regla 2: No exceder el volumen
        if (volumenContenedor > camion.getCapacidadVolumenM3()) {
            return false;
        }

        return true;
    }
}