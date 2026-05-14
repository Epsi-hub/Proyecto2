package com.example.demo;

public class BenchmarkReporte {

    private int totalInserciones;
    private long tiempoTotalInserciones;
    private long promedioInserciones;

    private int totalBusquedas;
    private long tiempoTotalBusquedas;
    private long promedioBusquedas;

    public BenchmarkReporte(
            int totalInserciones, long tiempoTotalInserciones, long promedioInserciones,
            int totalBusquedas, long tiempoTotalBusquedas, long promedioBusquedas) {
        this.totalInserciones = totalInserciones;
        this.tiempoTotalInserciones = tiempoTotalInserciones;
        this.promedioInserciones = promedioInserciones;
        this.totalBusquedas = totalBusquedas;
        this.tiempoTotalBusquedas = tiempoTotalBusquedas;
        this.promedioBusquedas = promedioBusquedas;
    }

    public int getTotalInserciones() { return totalInserciones; }
    public long getTiempoTotalInserciones() { return tiempoTotalInserciones; }
    public long getPromedioInserciones() { return promedioInserciones; }

    public int getTotalBusquedas() { return totalBusquedas; }
    public long getTiempoTotalBusquedas() { return tiempoTotalBusquedas; }
    public long getPromedioBusquedas() { return promedioBusquedas; }
}