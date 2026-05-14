package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class Benchmark {

    @Value("${benchmark.csv.path:benchmark.csv}")
    private String rutaBenchmarkCsv;

    public BenchmarkReporte inicializar() {
        List<Palabra> palabras = leerCsv(); //lectura de csv mediante metodo interno

        DiccionarioTemporal diccionarioTemporal = new DiccionarioTemporal();

        long tiempoTotalInserciones = 0;
        int totalInserciones = 0;
        long tiempoTotalBusquedas = 0;
        int totalBusquedas = 0;

        for (Palabra p : palabras) { //creación de todas las palabras
            CrearPalabraRequest req = new CrearPalabraRequest(p.getPalabra(),p.getSignificado()); //creación de objeto

            long inicio = System.nanoTime();
            diccionarioTemporal.crear(req);
            tiempoTotalInserciones += System.nanoTime() - inicio; //acumulación de delta t (tiempo de inserción)
            totalInserciones++;
        }

        for (Palabra p : palabras) { //busqueda de todas las palabras
            long inicio = System.nanoTime();
            diccionarioTemporal.buscarExacta(p.getPalabra());
            tiempoTotalBusquedas += System.nanoTime() - inicio; //acumulación de delta t (tiempo de búsqueda)
            totalBusquedas++;
        }

        long promedioInserciones = 0;
        long promedioBusquedas = 0;

        if (totalInserciones > 0) {
            promedioInserciones = tiempoTotalInserciones / totalInserciones;
        }
        if (totalBusquedas > 0) {
            promedioBusquedas = tiempoTotalBusquedas / totalBusquedas;
        }

        return new BenchmarkReporte(
                totalInserciones, tiempoTotalInserciones, promedioInserciones,
                totalBusquedas, tiempoTotalBusquedas, promedioBusquedas
        );
    }

    private List<Palabra> leerCsv() {
        List<Palabra> palabras = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaBenchmarkCsv))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }
                if (linea.isBlank()) continue;

                String[] partes = linea.split(",", 4);
                if (partes.length < 4) continue;

                int id = Integer.parseInt(partes[0].trim());
                String palabra = partes[1].trim();
                String significado = partes[2].trim();
                int frecuencia = Integer.parseInt(partes[3].trim());

                palabras.add(new Palabra(id, palabra, significado, frecuencia));
            }
        } catch (Exception e) {
            //System.err.println("Error leyendo csv en benchmark: " + e.getMessage());
        }

        return palabras;
    }

    private static class DiccionarioTemporal {
        private final Trie trie = new Trie();
        private final TablaHash<Palabra> tablaHash = new TablaHash<>(Palabra::getId, Palabra::getPalabra);
        private int contadorId = 0;

        public void crear(CrearPalabraRequest req) {
            String texto = req.getPalabra().trim().toLowerCase();
            contadorId++;
            Palabra p = new Palabra(contadorId, texto, req.getSignificado().trim(), 1);
            trie.insertar(p);
            tablaHash.insertar(p);
        }

        public Palabra buscarExacta(String texto) {
            return tablaHash.buscarPorPalabra(texto.toLowerCase().trim());
        }
    }
}