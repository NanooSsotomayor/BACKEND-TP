package com.proyectoviajes.solicitudms.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Data
public class RutaCalculoDTO {
    private List<double[]> puntosRuta;
    private Double pesoContenedorKg;
    private Double volumenContenedorM3;

    // Campos alternativos esperados por otros servicios (serializan lo mismo)
    @JsonProperty("pesoKg")
    public Double getPesoKg() {
        return pesoContenedorKg;
    }

    @JsonProperty("pesoKg")
    public void setPesoKg(Double pesoKg) {
        this.pesoContenedorKg = pesoKg;
    }

    @JsonProperty("volumenM3")
    public Double getVolumenM3() {
        return volumenContenedorM3;
    }

    @JsonProperty("volumenM3")
    public void setVolumenM3(Double volumenM3) {
        this.volumenContenedorM3 = volumenM3;
    }
}