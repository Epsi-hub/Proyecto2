package com.example.demo;

public class Palabra implements Comparable<Palabra> {

    private int id = 0;
    private String palabra;
    private String significado;
    private int frecuencia;

    public Palabra(int id, String palabra, String significado, int frecuencia) {
        this.id = id;
        this.palabra = palabra;
        this.significado = significado;
        this.frecuencia = frecuencia;
    }

    // Comparable natural: alfabético
    @Override
    public int compareTo(Palabra other) {
        return this.palabra.compareToIgnoreCase(other.palabra);
    }

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

    public int getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(int frecuencia) {
        this.frecuencia = frecuencia;
    }
}