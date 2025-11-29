package app.model.estructuras;

import app.model.Lotes;

/**
 * Estructura de datos Cola FIFO (First In, First Out)
 * Utilizada para productos perecibles donde el primer producto ingresado
 * es el primero en salir
 */
public class ColaFIFO {
    private NodoCola frente;
    private NodoCola finalCola;
    private int tamaño;
    
    /**
     * Constructor por defecto - inicializa una cola vacía
     */
    public ColaFIFO() {
        this.frente = null;
        this.finalCola = null;
        this.tamaño = 0;
    }
    
    /**
     * Encola un lote (añade al final de la cola)
     * @param lote El lote a encolar
     */
    public void encolar(Lotes lote) {
        // TODO: Implementar lógica de encolar
    }
    
    /**
     * Desencola un lote (extrae del frente de la cola)
     * @return El lote desencolado, o null si la cola está vacía
     */
    public Lotes desencolar() {
        // TODO: Implementar lógica de desencolar
        return null;
    }
    
    /**
     * Verifica si la cola está vacía
     * @return true si la cola está vacía, false en caso contrario
     */
    public boolean estaVacia() {
        return frente == null;
    }
    
    /**
     * Obtiene el tamaño de la cola
     * @return El número de elementos en la cola
     */
    public int getTamaño() {
        return tamaño;
    }
    
    /**
     * Obtiene el lote del frente sin eliminarlo
     * @return El lote del frente, o null si la cola está vacía
     */
    public Lotes verFrente() {
        // TODO: Implementar visualización del frente
        return null;
    }
    
    /**
     * Vacía la cola completamente
     */
    public void vaciar() {
        // TODO: Implementar vaciado de la cola
    }
    
    /**
     * Verifica si un lote específico existe en la cola
     * @param loteId ID del lote a buscar
     * @return true si existe, false en caso contrario
     */
    public boolean contiene(String loteId) {
        // TODO: Implementar búsqueda de lote
        return false;
    }
}
