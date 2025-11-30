package app.model.estructuras;

// Definición del Nodo creado por Fabiana
class NodoPila<T> {
    T dato;
    NodoPila<T> siguiente;

    public NodoPila(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}

// Estructura solicitada
public class PilaLIFO<T> {
    private NodoPila<T> cima;

    public PilaLIFO() {
        this.cima = null;
    }

    // Método Apilar (Push - Poner arriba)
    public void apilar(T dato) {
        NodoPila<T> nuevoNodo = new NodoPila<>(dato);
        nuevoNodo.siguiente = cima;
        cima = nuevoNodo;
    }

    // Método Desapilar (Pop - Sacar de arriba)
    public T desapilar() {
        if (cima == null) {
            return null; // La pila está vacía
        }
        T dato = cima.dato;
        cima = cima.siguiente;
        return dato;
    }
}
