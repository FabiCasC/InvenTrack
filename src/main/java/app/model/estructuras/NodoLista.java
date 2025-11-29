package app.model.estructuras;

import app.model.Movimientos;
import java.util.Date;

/**
 * Nodo para la estructura de datos Lista Enlazada
 * Utilizado para almacenar el historial de movimientos de inventario
 * Cada nodo contiene un movimiento y referencia al siguiente nodo
 */
public class NodoLista {
    private Movimientos movimiento;
    private NodoLista siguiente;
    
    /**
     * Constructor por defecto
     */
    public NodoLista() {
        this.movimiento = null;
        this.siguiente = null;
    }
    
    /**
     * Constructor con movimiento
     * @param movimiento El movimiento a almacenar en el nodo
     */
    public NodoLista(Movimientos movimiento) {
        this.movimiento = movimiento;
        this.siguiente = null;
    }
    
    /**
     * Constructor completo
     * @param movimiento El movimiento a almacenar en el nodo
     * @param siguiente Referencia al siguiente nodo en la lista
     */
    public NodoLista(Movimientos movimiento, NodoLista siguiente) {
        this.movimiento = movimiento;
        this.siguiente = siguiente;
    }
    
    /**
     * Constructor alternativo con datos del movimiento
     * @param movimientoId ID del movimiento
     * @param productoId ID del producto
     * @param tipoMovimiento Tipo de movimiento (entrada/salida)
     * @param cantidad Cantidad del movimiento
     * @param fecha Fecha del movimiento
     * @param usuarioId ID del usuario que realizó el movimiento
     * @param algoritmo Algoritmo utilizado
     */
    public NodoLista(String movimientoId, String productoId, String tipoMovimiento, 
                     int cantidad, Date fecha, String usuarioId, String algoritmo) {
        this.movimiento = new Movimientos(movimientoId, productoId, tipoMovimiento, 
                                         cantidad, fecha, usuarioId, algoritmo);
        this.siguiente = null;
    }

    /**
     * Obtiene el movimiento almacenado en el nodo
     * @return El movimiento del nodo
     */
    public Movimientos getMovimiento() {
        return movimiento;
    }

    /**
     * Establece el movimiento en el nodo
     * @param movimiento El movimiento a establecer
     */
    public void setMovimiento(Movimientos movimiento) {
        this.movimiento = movimiento;
    }

    /**
     * Obtiene la referencia al siguiente nodo
     * @return El siguiente nodo en la lista
     */
    public NodoLista getSiguiente() {
        return siguiente;
    }

    /**
     * Establece la referencia al siguiente nodo
     * @param siguiente El siguiente nodo a establecer
     */
    public void setSiguiente(NodoLista siguiente) {
        this.siguiente = siguiente;
    }
    
    /**
     * Verifica si el nodo tiene un siguiente nodo
     * @return true si tiene siguiente, false en caso contrario
     */
    public boolean tieneSiguiente() {
        return siguiente != null;
    }
    
    /**
     * Obtiene el tipo de movimiento del nodo
     * @return El tipo de movimiento o null si no existe
     */
    public String getTipoMovimiento() {
        return movimiento != null ? movimiento.getTipo_movimiento() : null;
    }
    
    /**
     * Obtiene la fecha del movimiento
     * @return La fecha del movimiento o null si no existe
     */
    public Date getFecha() {
        return movimiento != null ? movimiento.getFecha() : null;
    }
    
    /**
     * Obtiene la cantidad del movimiento
     * @return La cantidad del movimiento o 0 si no existe
     */
    public int getCantidad() {
        return movimiento != null ? movimiento.getCantidad() : 0;
    }
    
    @Override
    public String toString() {
        return "NodoLista{" + 
               "movimiento=" + (movimiento != null ? movimiento.toString() : "null") + 
               ", tieneSiguiente=" + tieneSiguiente() + 
               '}';
    }
}

