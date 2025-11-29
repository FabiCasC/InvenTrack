package app.model.estructuras;

import app.model.Lotes;

/**
 * Nodo para la estructura de datos Pila LIFO (Last In, First Out)
 * Utilizado para productos no perecibles donde el último producto ingresado
 * es el primero en salir (LIFO)
 */
public class NodoPila {
    private Lotes lote;
    private NodoPila siguiente;
    
    /**
     * Constructor por defecto
     */
    public NodoPila() {
        this.lote = null;
        this.siguiente = null;
    }
    
    /**
     * Constructor con lote
     * @param lote El lote a almacenar en el nodo
     */
    public NodoPila(Lotes lote) {
        this.lote = lote;
        this.siguiente = null;
    }
    
    /**
     * Constructor completo
     * @param lote El lote a almacenar en el nodo
     * @param siguiente Referencia al siguiente nodo en la pila
     */
    public NodoPila(Lotes lote, NodoPila siguiente) {
        this.lote = lote;
        this.siguiente = siguiente;
    }

    /**
     * Obtiene el lote almacenado en el nodo
     * @return El lote del nodo
     */
    public Lotes getLote() {
        return lote;
    }

    /**
     * Establece el lote en el nodo
     * @param lote El lote a establecer
     */
    public void setLote(Lotes lote) {
        this.lote = lote;
    }

    /**
     * Obtiene la referencia al siguiente nodo
     * @return El siguiente nodo en la pila
     */
    public NodoPila getSiguiente() {
        return siguiente;
    }

    /**
     * Establece la referencia al siguiente nodo
     * @param siguiente El siguiente nodo a establecer
     */
    public void setSiguiente(NodoPila siguiente) {
        this.siguiente = siguiente;
    }
    
    /**
     * Verifica si el nodo tiene un siguiente nodo
     * @return true si tiene siguiente, false en caso contrario
     */
    public boolean tieneSiguiente() {
        return siguiente != null;
    }
    
    @Override
    public String toString() {
        return "NodoPila{" + 
               "lote=" + (lote != null ? lote.toString() : "null") + 
               ", tieneSiguiente=" + tieneSiguiente() + 
               '}';
    }
}

