package com.example.demo;

import java.util.ArrayList;
import java.util.List;

public class Trie {

    private final Nodo raiz;

    public Trie() {
        raiz = new Nodo();
    }

    public void insertar(Palabra p) {
        Nodo actual = raiz;
        for (char c : p.getPalabra().toLowerCase().toCharArray()) {
            if (c - 'a' < 0 || c - 'a' >= 26) continue;
            if (actual.hijos[c - 'a'] == null) {
                actual.hijos[c - 'a'] = new Nodo(); //creación de nodo de caracter en espacio vacío
            }
            actual = actual.hijos[c - 'a'];
        }
        actual.esUltimo = true; //certificación de que es una palabra
        actual.palabra = p;
    }


    public List<Palabra> buscarPorPrefijo(String prefijo) {
        Nodo nodo = encontrar(prefijo); //obtención de nodo por prefijo
        if (nodo == null) return List.of();
        List<Palabra> resultado = new ArrayList<>();
        recopilarPalabras(nodo, resultado); //recopilación a base del nodo recuperado
        return resultado;
    }

    public List<Palabra> buscarComodin(String patron) {
        List<Palabra> resultado = new ArrayList<>();
        buscarComodines(raiz, patron.toLowerCase(), 0, resultado); //llamada a su metodo recursivo
        return resultado;
    }

    private void buscarComodines(Nodo nodo, String patron, int indice, List<Palabra> resultado) {
        if (indice == patron.length()) { //se llego a su "limite"
            if (nodo.esUltimo) {
                resultado.add(nodo.palabra); //se guarda si es palabra
            }
            return;
        }

        char c = patron.charAt(indice);

        if (c == '*') {
            buscarComodines(nodo, patron, indice + 1, resultado);
            for (int i = 0; i < 26; i++) { //si el caracter el valido (letra inglesa)
                if (nodo.hijos[i] != null) {
                    buscarComodines(nodo.hijos[i], patron, indice, resultado); //se continua con los hijos, en si se mantiene en el mismo indice
                }
            }
        } else if (c == '.') {
            for (int i = 0; i < 26; i++) {
                if (nodo.hijos[i] != null) {
                    buscarComodines(nodo.hijos[i], patron, indice + 1, resultado); //se continua con los hijos, se avanza de indice al ser solo un carater
                }
            }
        } else { //caso de caracter comun
            if (c - 'a' >= 0 && c - 'a' < 26 && nodo.hijos[c - 'a'] != null) {
                buscarComodines(nodo.hijos[c - 'a'], patron, indice + 1, resultado); //se continua con los hijos, se avanza de indice
            }
        }
    }

    public boolean eliminar(String texto) {
        return eliminar(raiz, texto.toLowerCase(), 0); //llamada a metodo recursivo
    }

    private boolean eliminar(Nodo nodo, String texto, int indice) {
        if (indice == texto.length()) { //se ha recorrido por completo
            if (!nodo.esUltimo) return false; //si no es palabra se ignora
            // desarme de componentes clave
            nodo.esUltimo = false;
            nodo.palabra = null;
            return true;
        }

        int i = texto.charAt(indice) - 'a';
        if (i < 0 || i >= 26 || nodo.hijos[i] == null) return false; //no es un nodo valido

        boolean eliminado = eliminar(nodo.hijos[i], texto, indice + 1); //se recorren los hijos hasta llegar al caso base

        if (eliminado && !nodo.hijos[i].esUltimo && !tieneHijos(nodo.hijos[i])) {
            nodo.hijos[i] = null; //segunda parte de desarme de componentes clave (en este caso se deshecha los nodos no necesarios)
        }
        return eliminado;
    }

    private Nodo encontrar(String texto) {
        Nodo actual = raiz;
        for (char c : texto.toLowerCase().toCharArray()) {
            if (c - 'a' < 0 || c - 'a' >= 26 || actual.hijos[c - 'a'] == null) return null; //verificación si es válido
            actual = actual.hijos[c - 'a']; //se actualiza hasta llegar al último nodo posible
        }
        return actual;
    }

    private void recopilarPalabras(Nodo nodo, List<Palabra> resultado) {
        if (nodo.esUltimo) {
            resultado.add(nodo.palabra); //obtención de la palabra original
        }
        for (Nodo hijo : nodo.hijos) {
            if (hijo != null) {
                recopilarPalabras(hijo, resultado); //obtención de los hijos de la palabra original
            }
        }
    }

    private boolean tieneHijos(Nodo nodo) {
        for (Nodo h : nodo.hijos) {
            if (h != null) return true;
        }
        return false;
    }

    public List<Palabra> listaPalabrasTrie() {
        List<Palabra> resultado = new ArrayList<>();
        recopilarPalabras(raiz, resultado); //aplicación de recopilarPalabras, llamada a su contraparte recursiva
        return resultado;
    }

    private static class Nodo {
        Nodo[] hijos = new Nodo[26]; //segun la cantidad de letras del ingles
        boolean esUltimo = false;
        Palabra palabra = null;
    }
}