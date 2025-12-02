package app.structures;

import app.model.Lotes;

/**
 * RF4.2 - Pila LIFO para productos no perecibles
 * Estructura de datos que implementa Last In, First Out
 * El último producto ingresado es el primero en salir
 */
public class PilaLIFO {
    
    /**
     * Nodo interno de la pila
     */
    private class NodoPila {
        Lotes lote;
        NodoPila siguiente;
        
        public NodoPila(Lotes lote) {
            this.lote = lote;
            this.siguiente = null;
        }
    }
    
    private NodoPila tope; // Último elemento agregado (primer en salir)
    private int tamaño;
    
    /**
     * Constructor - Inicializa una pila vacía
     */
    public PilaLIFO() {
        this.tope = null;
        this.tamaño = 0;
    }
    
    /**
     * RF4.2 - Push: Agrega un nuevo lote en la cima de la pila
     * Push al recibir nuevos lotes
     * @param lote Lote a agregar
     */
    public void push(Lotes lote) {
        NodoPila nuevoNodo = new NodoPila(lote);
        nuevoNodo.siguiente = tope;
        tope = nuevoNodo;
        tamaño++;
    }
    
    /**
     * RF4.2 - Pop: Remueve y retorna el lote del tope de la pila
     * Pop al despachar
     * El último producto ingresado es el primero en salir
     * @return El lote del tope (el más reciente), o null si está vacía
     */
    public Lotes pop() {
        if (estaVacia()) {
            return null;
        }
        
        Lotes lote = tope.lote;
        tope = tope.siguiente;
        tamaño--;
        return lote;
    }
    
    /**
     * Ver el tope de la pila sin removerlo
     * @return El lote en el tope, o null si está vacía
     */
    public Lotes verTope() {
        if (estaVacia()) {
            return null;
        }
        return tope.lote;
    }
    
    /**
     * Verifica si la pila está vacía
     * @return true si está vacía, false en caso contrario
     */
    public boolean estaVacia() {
        return tope == null;
    }
    
    /**
     * Obtiene el tamaño de la pila
     * @return Número de elementos en la pila
     */
    public int getTamaño() {
        return tamaño;
    }
    
    /**
     * Recorre la pila y muestra todos los lotes
     * Útil para depuración y visualización
     * Muestra desde el tope hacia abajo
     */
    public void recorrer() {
        if (estaVacia()) {
            System.out.println("Pila LIFO vacía");
            return;
        }
        
        System.out.println("Pila LIFO (Tope -> Base):");
        NodoPila actual = tope;
        int posicion = 1;
        while (actual != null) {
            System.out.println("  " + posicion + ". Lote: " + actual.lote.getLoteId() + 
                             " - Cantidad: " + actual.lote.getCantidad());
            actual = actual.siguiente;
            posicion++;
        }
    }
    
    /**
     * Limpia toda la pila
     */
    public void limpiar() {
        tope = null;
        tamaño = 0;
    }
}
