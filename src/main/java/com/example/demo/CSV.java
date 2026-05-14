package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSV {

    private static final String ENCABEZADO = "id,palabra,significado,frecuencia";

    @Value("${diccionario.csv.path:diccionario.csv}")
    private String rutaCsvDefault;

    public List<Palabra> importar() { //caso promedio
        return importar(rutaCsvDefault);
    }

    public List<Palabra> importar(String rutaArchivo) {
        List<Palabra> palabras = new ArrayList<>();
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) { //el csv no existe
            System.err.println("[CSV] No existe: " + rutaArchivo);
            return palabras;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = reader.readLine()) != null) { //existe texto en la linea
                if (primeraLinea) { //caso especial (ignora la primera linea)
                    primeraLinea = false;
                    continue;
                }
                if (linea.isBlank()) continue; //esta vacío

                Palabra p = procesarLinea(linea); //crea un objeto con el texto en la linea
                if (p != null) palabras.add(p); //se añade a la lista si se logro crear el objeto
            }
        } catch (IOException e) {  //BufferedReader no logró procesar el documento
            System.err.println("[CSV] Error importando: " + e.getMessage());
        }

        return palabras;
    }

    public void exportar(List<Palabra> palabras) { //caso promedio
        exportar(palabras, rutaCsvDefault);
    }

    public void exportar(List<Palabra> palabras, String rutaArchivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo))) {
            writer.write(ENCABEZADO);
            writer.newLine();
            for (Palabra p : palabras) {
                writer.write(formatearLinea(p));
                writer.newLine();
            }
            System.out.println("[CSV] Exportadas " + palabras.size() + " palabras a " + rutaArchivo);
        } catch (IOException e) {
            System.err.println("[CSV] Error exportando: " + e.getMessage());
        }
    }

    private Palabra procesarLinea(String linea) {
        try {
            String[] partes = linea.split(",", 4); //separa el texto y mete las palabras en una lista segun el uso de coma
            if (partes.length < 4) return null; // cumple el criterio  (4 parametros)
            int id = Integer.parseInt(partes[0].trim());  //obtencion de id
            String palabra = partes[1].trim().replace("\"", ""); //obtencion de palabra
            String significado = partes[2].trim().replace("\"", ""); //obtencion de significado
            int frecuencia = Integer.parseInt(partes[3].trim()); // obtencion de frecuencia
            return new Palabra(id, palabra, significado, frecuencia); // generación de objeto en base a los 4 parámetros
        } catch (Exception e) {
            System.err.println("[CSV] Linea invalida: " + linea);
            return null;
        }
    }

    private String formatearLinea(Palabra p) { //a base de un objeto palabra crea su contraparte en formato csv
        String sig;
        if (p.getSignificado() == null) {
            sig = "";
        } else {
            sig = p.getSignificado();
        }
        if (sig.contains(",")) { //si el significado tiene comas, se agregan comillas para evitar que se confunda con otro parametro
            sig = "\"" + sig + "\"";
        }
        return p.getId() + "," + p.getPalabra() + "," + sig + "," + p.getFrecuencia(); //retorna el formato para ingresarlo al .csv
    }
}