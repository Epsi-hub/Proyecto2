package com.example.demo;

import jakarta.validation.constraints.NotBlank;

public class CrearPalabraRequest {

    @NotBlank(message = "La palabra no puede estar vacía")
    private String palabra;

    @NotBlank(message = "El significado no puede estar vacío")
    private String significado;

    public String getPalabra() { return palabra; }
    public void setPalabra(String palabra) { this.palabra = palabra; }

    public String getSignificado() { return significado; }
    public void setSignificado(String significado) { this.significado = significado; }

    public CrearPalabraRequest(String palabra, String significado) {
        this.palabra = palabra;
        this.significado = significado;
    }
}
