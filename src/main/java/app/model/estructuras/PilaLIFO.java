package app.model.estructuras;

import app.model.Lotes;

/**
 * Estructura de datos Pila LIFO (Last In, First Out)
 * Utilizada para productos no perecibles donde el último producto ingresado
 * es el primero en salir
 */
public class PilaLIFO {
    private NodoPila cima;
    private int tamaño;
    
    /**
     * Constructor por defecto - inicializa una pila vacía
     */
    public PilaLIFO() {
        this.cima = null;
        this.tamaño = 0;
    }
    
    /**
     * Apila un lote (añade en la cima de la pila)
     * @param lote El lote a apilar
     */
    public void push(Lotes lote) {
        if (lote == null) {
            throw new IllegalArgumentException("No se puede apilar un lote nulo");
        }
        
        NodoPila nuevoNodo = new NodoPila(lote, cima);
        cima = nuevoNodo;
        tamaño++;
    }
    
    /**
     * Desapila un lote (extrae de la cima de la pila)
     * @return El lote desapilado, o null si la pila está vacía
     */
    public Lotes pop() {
        if (estaVacia()) {
            return null;
        }
        
        Lotes loteDesapilado = cima.getLote();
        cima = cima.getSiguiente();
        tamaño--;
        
        return loteDesapilado;
    }
    
    /**
     * Verifica si la pila está vacía
     * @return true si la pila está vacía, false en caso contrario
     */
    public boolean estaVacia() {
        return cima == null;
    }
    
    /**
     * Obtiene el tamaño de la pila
     * @return El número de elementos en la pila
     */
    public int getTamaño() {
        return tamaño;
    }
    
    /**
     * Obtiene el lote de la cima sin eliminarlo
     * @return El lote de la cima, o null si la pila está vacía
     */
    public Lotes verCima() {
        if (estaVacia()) {
            return null;
        }
        return cima.getLote();
    }
    
    /**
     * Vacía la pila completamente
     */
    public void vaciar() {
        cima = null;
        tamaño = 0;
    }
    
    /**
     * Verifica si un lote específico existe en la pila
     * @param loteId ID del lote a buscar
     * @return true si existe, false en caso contrario
     */
    public boolean contiene(String loteId) {
        if (loteId == null || estaVacia()) {
            return false;
        }
        
        NodoPila actual = cima;
        while (actual != null) {
            if (actual.getLote() != null && loteId.equals(actual.getLote().getLoteId())) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        
        return false;
    }
}

