package app.structures;

import app.model.Lotes;

/**
 * RF4.1 - Cola FIFO para productos perecibles
 * Estructura de datos que implementa First In, First Out
 * El primer producto ingresado es el primero en salir
 */
public class ColaFIFO {
    
    /**
     * Nodo interno de la cola
     */
    private class NodoCola {
        Lotes lote;
        NodoCola siguiente;
        
        public NodoCola(Lotes lote) {
            this.lote = lote;
            this.siguiente = null;
        }
    }
    
    private NodoCola frente; // Primer elemento (primer en salir)
    private NodoCola finalCola; // Último elemento (último en entrar)
    private int tamaño;
    
    /**
     * Constructor - Inicializa una cola vacía
     */
    public ColaFIFO() {
        this.frente = null;
        this.finalCola = null;
        this.tamaño = 0;
    }
    
    /**
     * RF4.1 - Encolar: Agrega un nuevo lote al final de la cola
     * Cada entrada genera un nodo en la cola
     * @param lote Lote a agregar
     */
    public void encolar(Lotes lote) {
        NodoCola nuevoNodo = new NodoCola(lote);
        
        if (estaVacia()) {
            frente = nuevoNodo;
            finalCola = nuevoNodo;
        } else {
            finalCola.siguiente = nuevoNodo;
            finalCola = nuevoNodo;
        }
        tamaño++;
    }
    
    /**
     * RF4.1 - Desencolar: Remueve y retorna el primer lote de la cola
     * El "primer producto ingresado" es el primero en salir
     * @return El lote que estaba al frente (el más antiguo), o null si está vacía
     */
    public Lotes desencolar() {
        if (estaVacia()) {
            return null;
        }
        
        Lotes lote = frente.lote;
        frente = frente.siguiente;
        
        if (frente == null) {
            finalCola = null; // La cola quedó vacía
        }
        
        tamaño--;
        return lote;
    }
    
    /**
     * Ver el frente de la cola sin removerlo
     * @return El lote al frente de la cola, o null si está vacía
     */
    public Lotes verFrente() {
        if (estaVacia()) {
            return null;
        }
        return frente.lote;
    }
    
    /**
     * Verifica si la cola está vacía
     * @return true si está vacía, false en caso contrario
     */
    public boolean estaVacia() {
        return frente == null;
    }
    
    /**
     * Obtiene el tamaño de la cola
     * @return Número de elementos en la cola
     */
    public int getTamaño() {
        return tamaño;
    }
    
    /**
     * Recorre la cola y muestra todos los lotes
     * Útil para depuración y visualización
     */
    public void recorrer() {
        if (estaVacia()) {
            System.out.println("Cola FIFO vacía");
            return;
        }
        
        System.out.println("Cola FIFO (Frente -> Final):");
        NodoCola actual = frente;
        int posicion = 1;
        while (actual != null) {
            System.out.println("  " + posicion + ". Lote: " + actual.lote.getLoteId() + 
                             " - Cantidad: " + actual.lote.getCantidad());
            actual = actual.siguiente;
            posicion++;
        }
    }
    
    /**
     * Limpia toda la cola
     */
    public void limpiar() {
        frente = null;
        finalCola = null;
        tamaño = 0;
    }
}
