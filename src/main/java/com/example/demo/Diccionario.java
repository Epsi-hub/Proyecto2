package com.example.demo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class Diccionario {

    private final Trie trie;
    private final TablaHash<Palabra> tablaHash;
    private final CSV csv;
    private int contadorId = 0;

    @Value("${diccionario.csv.path:diccionario.csv}")
    private String rutaCsvDefault;

    public Diccionario(CSV csv) {
        this.csv = csv;
        this.trie = new Trie();
        this.tablaHash = new TablaHash<>(Palabra::getId, Palabra::getPalabra);
    }

    @PostConstruct //ocurrido al iniciar el programa, por lo que no necesita ser usado por otros métodos
    public void cargarDesdeCsv() {
        List<Palabra> palabras = csv.importar();
        for (Palabra p : palabras) { //se visita los elementos de lista de palabras para su inserción
            trie.insertar(p);
            tablaHash.insertar(p);
            if (p.getId() > contadorId) {
                contadorId = p.getId(); //el contador Id se actualiza según el id más alto existente en el csv
            }
        }
        System.out.println("Cargadas " + palabras.size() + " palabras.");
    }

    @PreDestroy //ocurrido al parar el programa
    public void guardarEnCsv() {
        exportar(); //clase interna de Diccionario
        //System.out.println(trie.listaPalabrasTrie().size() + " palabras guardadas.");
    }

    public int importar() {
        return importar(rutaCsvDefault); //llamada a metodo recursivo
    }

    public int importar(String rutaArchivo) { //importación específica
        List<Palabra> palabras = csv.importar(rutaArchivo);
        int importadas = 0;
        for (Palabra p : palabras) { //se visita los elementos de lista de palabras para su inserción
            if (!tablaHash.contienePorClave(p.getPalabra())) { // se actualiza contadorId según el Id más alto de la importación
                if (p.getId() > contadorId) contadorId = p.getId();
                trie.insertar(p);
                tablaHash.insertar(p);
                importadas++;
            }
        }
        //System.out.println("Importadas " + importadas + " palabras desde " + rutaArchivo);
        return importadas;
    }

    public void exportar() { //utilizado automaticamente para guardar palabras al terminar ejecución
        csv.exportar(trie.listaPalabrasTrie());
    }

    public void exportar(String rutaArchivo) { //exportación a un csv específico
        csv.exportar(trie.listaPalabrasTrie(), rutaArchivo);
    }

    public Palabra crear(CrearPalabraRequest req) {//insersión de un objeto Palabra en base a un request
        String texto = req.getPalabra().trim().toLowerCase();
        if (tablaHash.contienePorClave(texto)) { //la palabra existe
            throw new IllegalArgumentException("La palabra '" + texto + "' ya existe.");
        }

        contadorId++;
        Palabra p = new Palabra(contadorId, texto, req.getSignificado().trim(), 1);
        trie.insertar(p);
        tablaHash.insertar(p);

        return p;
    }

    public Palabra actualizar(ActualizarPalabraRequest palabraNueva) {
        if (palabraNueva.getId() <= 0) throw new IllegalArgumentException("Se requiere un id valido.");  //verificaciones de parámetros  (ID)

        Palabra existente = tablaHash.buscarPorId(palabraNueva.getId());
        if (existente == null) throw new IllegalArgumentException("No existe el id " + palabraNueva.getId());

        String textoAnterior = existente.getPalabra();

        if (palabraNueva.getPalabra() != null && !palabraNueva.getPalabra().isBlank()) //si el parametro palabra del viejo y el nuevo existen
            existente.setPalabra(palabraNueva.getPalabra().trim().toLowerCase());
        if (palabraNueva.getSignificado() != null) //actualizacion parametro significado
            existente.setSignificado(palabraNueva.getSignificado().trim());
        if (palabraNueva.getFrecuencia() != null) //actualizacion parametro frecuencia
            existente.setFrecuencia(palabraNueva.getFrecuencia());

        if (!textoAnterior.equals(existente.getPalabra())) {
            trie.eliminar(textoAnterior); //elinación en el caso del trie
        }


        trie.insertar(existente); //insersión de datos (despues de la eliminación del viejo)
        tablaHash.insertar(existente); //actualización de datos
        return existente;
    }

    public void eliminarPorId(int id) {
        Palabra p = tablaHash.buscarPorId(id); //obtencion de p mediante id
        if (p == null) throw new IllegalArgumentException("No existe el id " + id);

        trie.eliminar(p.getPalabra());
        tablaHash.eliminar(id);
    }

    public void eliminarPorPalabra(String texto) {
        String palabra = texto.toLowerCase().trim();
        Palabra p = tablaHash.buscarPorPalabra(palabra); //obtencion de p mediante palabra
        if (p == null) throw new IllegalArgumentException("No existe la palabra'" + palabra + "'");

        trie.eliminar(palabra);
        tablaHash.eliminar(p.getId());
    }

    public Palabra buscarPorId(int id) {
        Palabra resultado = tablaHash.buscarPorId(id);
        if (resultado != null) {
            incrementarFrecuencia(resultado);
        }
        return resultado;
    }

    public Palabra buscarExacta(String texto) {
        Palabra resultado = tablaHash.buscarPorPalabra(texto.toLowerCase().trim());
        if (resultado != null) {
            incrementarFrecuencia(resultado);
        }
        return resultado;
    }

    public List<Palabra> buscarPorPrefijo(String prefijo, int k, String ordenarPor, String orden) {
        Comparator<Palabra> comp = resolverComparador(ordenarPor, orden); //asingación de comparador para heap
        List<Palabra> palabras = trie.buscarPorPrefijo(prefijo.toLowerCase().trim()); //lista de prefijos (sin criterios aparte del prefijo)

        Cola<Palabra> heap = new Cola<>(comp);
        for (Palabra p : palabras) { //insersión a heap
            heap.insertar(p);
        }

        int limite;
        if (k > 0 && k < heap.size()) {
            limite = k;
        } else {
            limite = heap.size(); //no hay un k definido
        }

        List<Palabra> resultado = new ArrayList<>();
        for (int i = 0; i < limite; i++) {
            resultado.add(heap.extraer()); //recopilación de dato
        }
        return resultado;
    }

    public List<Palabra> buscarComodin(String patron, int k, String ordenarPor, String orden) {
        Comparator<Palabra> comp = resolverComparador(ordenarPor, orden);
        List<Palabra> candidatos = trie.buscarComodin(patron); //lista de palabras acorde a comodin  (sin limite o orden)

        Cola<Palabra> heap = new Cola<>(comp);
        for (Palabra p : candidatos) { //insersión a heap
            heap.insertar(p);
        }

        int limite;
        if (k > 0 && k < heap.size()) {
            limite = k;
        } else {
            limite = heap.size(); //no hay un k definido
        }

        List<Palabra> resultado = new ArrayList<>();
        for (int i = 0; i < limite; i++) {
            resultado.add(heap.extraer()); //recopilación de datos
        }
        return resultado;
    }

    public List<Palabra> topK(int k, String ordenarPor, String orden) {
        Comparator<Palabra> comp = resolverComparador(ordenarPor, orden);

        Cola<Palabra> maxHeap = new Cola<>(comp);
        for (Palabra p : trie.listaPalabrasTrie()) { //insersión a heap
            maxHeap.insertar(p);
        }

        int limite;
        if (k > 0 && k < maxHeap.size()) {
            limite = k;
        } else {
            limite = maxHeap.size(); //no hay un k definido
        }

        List<Palabra> resultado = new ArrayList<>();
        for (int i = 0; i < limite; i++) {
            resultado.add(maxHeap.extraer()); //recopilación de datos
        }
        return resultado;
    }

    public List<Palabra> listaPalabras(String ordenarPor, String orden) {
        Comparator<Palabra> comp = resolverComparador(ordenarPor, orden); //comparador para heap

        Cola<Palabra> heap = new Cola<>(comp);
        for (Palabra p : trie.listaPalabrasTrie()) { //insersión en heap de todas las palabras del diccioanrio
            heap.insertar(p);
        }

        List<Palabra> resultado = new ArrayList<>();
        while (!heap.isEmpty()) {
            resultado.add(heap.extraer());
        }
        return resultado;
    }

    private Comparator<Palabra> resolverComparador(String ordenarPor, String orden) {
        boolean asc = !"desc".equalsIgnoreCase(orden);

        if ("frecuencia".equalsIgnoreCase(ordenarPor)) {
            if (asc) {
                return (a, b) -> a.getFrecuencia() - b.getFrecuencia(); //frecuencia ascendente
            } else {
                return (a, b) -> b.getFrecuencia() - a.getFrecuencia(); //frecuencia descendente
            }
        } else {
            if (asc) {
                return (a, b) -> a.getPalabra().compareToIgnoreCase(b.getPalabra()); //alfabeto ascendente
            } else {
                return (a, b) -> b.getPalabra().compareToIgnoreCase(a.getPalabra()); //alfabeto descendente
            }
        }
    }

    private void incrementarFrecuencia(Palabra p) {
        p.setFrecuencia(p.getFrecuencia() + 1);
        trie.insertar(p);
        tablaHash.insertar(p);
    }
}