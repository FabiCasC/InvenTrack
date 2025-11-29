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
        // TODO: Implementar lógica de push
    }
    
    /**
     * Desapila un lote (extrae de la cima de la pila)
     * @return El lote desapilado, o null si la pila está vacía
     */
    public Lotes pop() {
        // TODO: Implementar lógica de pop
        return null;
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
        // TODO: Implementar visualización de la cima
        return null;
    }
    
    /**
     * Vacía la pila completamente
     */
    public void vaciar() {
        // TODO: Implementar vaciado de la pila
    }
    
    /**
     * Verifica si un lote específico existe en la pila
     * @param loteId ID del lote a buscar
     * @return true si existe, false en caso contrario
     */
    public boolean contiene(String loteId) {
        // TODO: Implementar búsqueda de lote
        return false;
    }
}

