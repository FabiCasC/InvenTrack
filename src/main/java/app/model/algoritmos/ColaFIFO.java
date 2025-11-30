package app.model.estructuras;

// Definición del Nodo creado por Fabiana
class NodoCola<T> {
    T dato;
    NodoCola<T> siguiente;

    public NodoCola(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}

// Estructura solicitada
public class ColaFIFO<T> {
    private NodoCola<T> frente;
    private NodoCola<T> finalCola;

    public ColaFIFO() {
        this.frente = null;
        this.finalCola = null;
    }

    // Método Encolar (Agregar al final)
    public void encolar(T dato) {
        NodoCola<T> nuevoNodo = new NodoCola<>(dato);
        if (frente == null) {
            frente = nuevoNodo;
            finalCola = nuevoNodo;
        } else {
            finalCola.siguiente = nuevoNodo;
            finalCola = nuevoNodo;
        }
    }

    // Método Desencolar (Sacar del frente)
    public T desencolar() {
        if (frente == null) {
            return null; // La cola está vacía
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        
        if (frente == null) {
            finalCola = null;
        }
        return dato;
    }
}
