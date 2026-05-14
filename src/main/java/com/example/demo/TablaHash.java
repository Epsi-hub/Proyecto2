package com.example.demo;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class TablaHash<T> {

    private static final int MAX_SIZE = 1001;

    private final List<T>[] hashTable;
    private int size;
    private final ToIntFunction<T> extractorId; //metodo para obtencion directa de Id (intermediaro usado en diccionario y aqui)
    private final Function<T, String> extractorPalabra; //metodo para obtencion de directa de palabra (intermediaro usado en diccionario)

    @SuppressWarnings("unchecked")
    public TablaHash(ToIntFunction<T> extractorId, Function<T, String> extractorPalabra) {
        this.hashTable = new List[MAX_SIZE];
        this.extractorId = extractorId;
        this.extractorPalabra = extractorPalabra;
    }

    public void insertar(T elemento) {
        int id = extractorId.applyAsInt(elemento);
        int posicion = hash(id);

        if (hashTable[posicion] == null) {
            hashTable[posicion] = new LinkedList<>(); //si no existe, se crea una lista enlazada
        }

        for (T existente : hashTable[posicion]) { //si ya existe, se sobreescribe
            if (extractorId.applyAsInt(existente) == id) {
                hashTable[posicion].remove(existente);
                hashTable[posicion].add(elemento);
                return;
            }
        }

        hashTable[posicion].add(elemento); //si no existe pero la posición no está vacia, se añad
    }

    public T buscarPorId(int id) {
        int posicion = hash(id);
        if (hashTable[posicion] == null) return null;

        for (T elemento : hashTable[posicion]) { //busqueda lineal
            if (extractorId.applyAsInt(elemento) == id) {
                return elemento; //hasta encontrar el id se retorna un nodo
            }
        }
        return null;
    }

    public T buscarPorPalabra(String palabra) {
        for (List<T> elementos : hashTable) {
            if (elementos == null) continue;
            for (T elemento : elementos) {
                if (extractorPalabra.apply(elemento).equalsIgnoreCase(palabra)) { //obtención y comparación entre palabras
                    return elemento;
                }
            }
        }
        return null;
    }

    public boolean eliminar(int id) {
        int hashKey = hash(id);
        if (hashTable[hashKey] == null) return false; //verificación de validez

        for (T elemento : hashTable[hashKey]) {
            if (extractorId.applyAsInt(elemento) == id) { //si se encuentra el elemento, se remueve de la lista enlazada
                hashTable[hashKey].remove(elemento);
                return true;
            }
        }
        return false;
    }

    public boolean contienePorClave(String clave) {
        return buscarPorPalabra(clave) != null;
    }

    private int hash(int id) {
        return Math.abs(id) % hashTable.length;
    }

}