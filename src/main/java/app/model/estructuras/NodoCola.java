package app.model.estructuras;

import app.model.Lotes;

/**
 * Nodo para la estructura de datos Cola FIFO (First In, First Out)
 * Utilizado para productos perecibles donde el primer producto ingresado
 * es el primero en salir (FIFO)
 */
public class NodoCola {
    private Lotes lote;
    private NodoCola siguiente;
    
    /**
     * Constructor por defecto
     */
    public NodoCola() {
        this.lote = null;
        this.siguiente = null;
    }
    
    /**
     * Constructor con lote
     * @param lote El lote a almacenar en el nodo
     */
    public NodoCola(Lotes lote) {
        this.lote = lote;
        this.siguiente = null;
    }
    
    /**
     * Constructor completo
     * @param lote El lote a almacenar en el nodo
     * @param siguiente Referencia al siguiente nodo en la cola
     */
    public NodoCola(Lotes lote, NodoCola siguiente) {
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
     * @return El siguiente nodo en la cola
     */
    public NodoCola getSiguiente() {
        return siguiente;
    }

    /**
     * Establece la referencia al siguiente nodo
     * @param siguiente El siguiente nodo a establecer
     */
    public void setSiguiente(NodoCola siguiente) {
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
        return "NodoCola{" + 
               "lote=" + (lote != null ? lote.toString() : "null") + 
               ", tieneSiguiente=" + tieneSiguiente() + 
               '}';
    }
}

