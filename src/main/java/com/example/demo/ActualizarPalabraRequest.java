package com.example.demo;

public class ActualizarPalabraRequest {

    private int id;
    private String palabra;
    private String significado;
    private Integer frecuencia;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPalabra() {
        return palabra;
    }
    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }
    public String getSignificado() {
        return significado;
    }
    public void setSignificado(String significado) {
        this.significado = significado;
    }
    public Integer getFrecuencia() {
        return frecuencia;
    }
    public void setFrecuencia(Integer frecuencia) {
        this.frecuencia = frecuencia;
    }
}