package app.model.estructuras;

// --- NODOS ---
class NodoLista<T> {
    T dato;
    NodoLista<T> siguiente;
    public NodoLista(T dato) { this.dato = dato; this.siguiente = null; }
}

class NodoCola<T> {
    T dato;
    NodoCola<T> siguiente;
    public NodoCola(T dato) { this.dato = dato; this.siguiente = null; }
}

class NodoPila<T> {
    T dato;
    NodoPila<T> siguiente;
    public NodoPila(T dato) { this.dato = dato; this.siguiente = null; }
}

// --- ESTRUCTURAS ---

public class ListaEnlazada<T> {
    private NodoLista<T> cabeza;
    private int tamano;

    public ListaEnlazada() { this.cabeza = null; this.tamano = 0; }

    public void agregar(T dato) {
        NodoLista<T> nuevo = new NodoLista<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoLista<T> actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
        tamano++;
    }

    // Método necesario para iterar externamente sin usar Iterator de Java
    public NodoLista<T> getCabeza() { return cabeza; }
    
    public int size() { return tamano; }
    
    public void recorrer() {
        NodoLista<T> actual = cabeza;
        while(actual != null) {
            System.out.println(actual.dato);
            actual = actual.siguiente;
        }
    }
    
    // Método auxiliar para eliminar (simplificado por referencia)
    public void eliminar(T dato) {
        if (cabeza == null) return;
        if (cabeza.dato.equals(dato)) {
            cabeza = cabeza.siguiente;
            tamano--;
            return;
        }
        NodoLista<T> actual = cabeza;
        while (actual.siguiente != null && !actual.siguiente.dato.equals(dato)) {
            actual = actual.siguiente;
        }
        if (actual.siguiente != null) {
            actual.siguiente = actual.siguiente.siguiente;
            tamano--;
        }
    }
}

public class ColaFIFO<T> {
    private NodoCola<T> frente;
    private NodoCola<T> finalCola;

    public void encolar(T dato) {
        NodoCola<T> nuevo = new NodoCola<>(dato);
        if (frente == null) {
            frente = nuevo;
            finalCola = nuevo;
        } else {
            finalCola.siguiente = nuevo;
            finalCola = nuevo;
        }
    }

    public T desencolar() {
        if (frente == null) return null;
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) finalCola = null;
        return dato;
    }
    
    public boolean estaVacia() { return frente == null; }
}

public class PilaLIFO<T> {
    private NodoPila<T> cima;

    public void apilar(T dato) {
        NodoPila<T> nuevo = new NodoPila<>(dato);
        nuevo.siguiente = cima;
        cima = nuevo;
    }

    public T desapilar() {
        if (cima == null) return null;
        T dato = cima.dato;
        cima = cima.siguiente;
        return dato;
    }
    
    public boolean estaVacia() { return cima == null; }
}
