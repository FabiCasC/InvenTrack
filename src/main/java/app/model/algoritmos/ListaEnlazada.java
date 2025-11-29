package app.model.estructuras;

// Definición del Nodo creado por Fabiana
class NodoLista<T> {
    T dato;
    NodoLista<T> siguiente;

    public NodoLista(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}

// Estructura solicitada
public class ListaEnlazada<T> {
    private NodoLista<T> cabeza;

    public ListaEnlazada() {
        this.cabeza = null;
    }

    // Método Agregar (Insertar al final)
    public void agregar(T dato) {
        NodoLista<T> nuevoNodo = new NodoLista<>(dato);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            NodoLista<T> actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
    }

    // Método Eliminar (Busca el dato y lo saca)
    public void eliminar(T dato) {
        if (cabeza == null) return;

        // Si es el primero
        if (cabeza.dato.equals(dato)) {
            cabeza = cabeza.siguiente;
            return;
        }

        NodoLista<T> actual = cabeza;
        while (actual.siguiente != null) {
            if (actual.siguiente.dato.equals(dato)) {
                actual.siguiente = actual.siguiente.siguiente;
                return;
            }
            actual = actual.siguiente;
        }
    }

    // Método Recorrer (Imprime los datos)
    public void recorrer() {
        NodoLista<T> actual = cabeza;
        System.out.print("Lista: ");
        while (actual != null) {
            System.out.print(actual.dato + " -> ");
            actual = actual.siguiente;
        }
        System.out.println("null");
    }
}
