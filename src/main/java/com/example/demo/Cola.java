package com.example.demo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Cola<T> {

    private List<T> heap;
    private Comparator<T> comparador; //utilizado para definir el orden


    public Cola(Comparator<T> comparador) {
        this.heap = new ArrayList<>();
        this.comparador = comparador;
    }


    public void insertar(T elemento) {
        heap.add(elemento);
        reposicionarArriba(heap.size() - 1); //insersión y reordenamiento por el nuevo dato
    }

    public T extraer() {
        if (heap.isEmpty()) return null;

        T raiz = heap.get(0); // se guarda raiz
        T ultimo = heap.remove(heap.size() - 1);  // se guarda el ultimo elemento y se borra del heap

        if (!heap.isEmpty()) {
            heap.set(0, ultimo); //se sobreescribe la posición raiz por el ultimo nodo
            reposicionarAbajo(0); //se reordena hacia abajo debido a la eliminación
        }
        return raiz;
    }

    private void reposicionarArriba(int indice) {
        while (indice > 0) {
            int padre = (indice - 1) / 2; //indice padre
            if (comparador.compare(heap.get(indice), heap.get(padre)) < 0) { //si el hijo es menor que padre
                intercambiar(indice, padre); //intercambio de posiciones
                indice = padre; //se actualiza indice a la nueva posición del hijo (lo que en su momento fue la posición padre)
            } else {
                break;
            }
        }
    }

    private void reposicionarAbajo(int indice) {
        int size = heap.size();
        while (true) {
            int izquierdo = 2 * indice + 1;
            int derecho = 2 * indice + 2;
            int menor = indice; //menor será el inidice padre

            if (izquierdo < size && comparador.compare(heap.get(izquierdo), heap.get(menor)) < 0) { //izquierdo existe y es menor que "menor"
                menor = izquierdo;
            }
            if (derecho < size && comparador.compare(heap.get(derecho), heap.get(menor)) < 0) {  //derecho existe y es menor que "menor"
                menor = derecho;
            }

            // menor no es padre (producto de las condicionales anteriores)
            if (menor != indice) {
                intercambiar(indice, menor); //intercambio de posiciones
                indice = menor;
            } else {
                break;
            }
        }
    }

    // Intercambio entre 2 elementos
    private void intercambiar(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int size() {
        return heap.size();
    }
}