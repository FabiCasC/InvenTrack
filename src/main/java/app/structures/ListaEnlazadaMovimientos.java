package app.structures;

import app.model.Movimientos;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * RF4.3 - Lista enlazada para movimientos
 * Estructura interna que almacena cada movimiento
 * Permite recorrer el historial en tiempo lineal
 */
public class ListaEnlazadaMovimientos {
    
    /**
     * Nodo de la lista enlazada
     * RF4.3 - Cada nodo contiene: tipo, fecha, cantidad y referencia al siguiente
     */
    private class NodoMovimiento {
        String tipo;           // tipo de movimiento (entrada/salida)
        Date fecha;            // fecha del movimiento
        int cantidad;          // cantidad del movimiento
        String movimientoId;   // ID del movimiento para referencia
        String productoId;     // ID del producto
        NodoMovimiento siguiente; // RF4.3 - Referencia al siguiente nodo
        
        public NodoMovimiento(String tipo, Date fecha, int cantidad, String movimientoId, String productoId) {
            this.tipo = tipo;
            this.fecha = fecha;
            this.cantidad = cantidad;
            this.movimientoId = movimientoId;
            this.productoId = productoId;
            this.siguiente = null;
        }
        
        // Constructor desde objeto Movimientos
        public NodoMovimiento(Movimientos movimiento) {
            this.tipo = movimiento.getTipo_movimiento();
            this.fecha = movimiento.getFecha();
            this.cantidad = movimiento.getCantidad();
            this.movimientoId = movimiento.getMovimientoId();
            this.productoId = movimiento.getProductoId();
            this.siguiente = null;
        }
    }
    
    private NodoMovimiento cabeza; // Primer nodo de la lista
    private int tamaño;
    
    /**
     * Constructor - Inicializa una lista vacía
     */
    public ListaEnlazadaMovimientos() {
        this.cabeza = null;
        this.tamaño = 0;
    }
    
    /**
     * RF4.3 - Agregar movimiento al final de la lista
     * Permite recorrer el historial en tiempo lineal
     * @param movimiento Movimiento a agregar
     */
    public void agregar(Movimientos movimiento) {
        NodoMovimiento nuevoNodo = new NodoMovimiento(movimiento);
        
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            NodoMovimiento actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
        tamaño++;
    }
    
    /**
     * RF4.3 - Agregar movimiento con datos individuales
     * @param tipo Tipo de movimiento (entrada/salida)
     * @param fecha Fecha del movimiento
     * @param cantidad Cantidad del movimiento
     * @param movimientoId ID del movimiento
     * @param productoId ID del producto
     */
    public void agregar(String tipo, Date fecha, int cantidad, String movimientoId, String productoId) {
        NodoMovimiento nuevoNodo = new NodoMovimiento(tipo, fecha, cantidad, movimientoId, productoId);
        
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            NodoMovimiento actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
        tamaño++;
    }
    
    /**
     * RF4.3 - Recorrer la lista y obtener todos los movimientos
     * Permite recorrer el historial en tiempo lineal O(n)
     * @return Lista con todos los movimientos
     */
    public List<Movimientos> obtenerMovimientos() {
        List<Movimientos> movimientos = new ArrayList<>();
        NodoMovimiento actual = cabeza;
        
        while (actual != null) {
            Movimientos mov = new Movimientos();
            mov.setMovimientoId(actual.movimientoId);
            mov.setProductoId(actual.productoId);
            mov.setTipo_movimiento(actual.tipo);
            mov.setFecha(actual.fecha);
            mov.setCantidad(actual.cantidad);
            movimientos.add(mov);
            actual = actual.siguiente;
        }
        
        return movimientos;
    }
    
    /**
     * RF4.3 - Recorrer la lista y mostrar todos los movimientos
     * Permite recorrer el historial en tiempo lineal
     */
    public void recorrer() {
        if (cabeza == null) {
            System.out.println("Lista de movimientos vacía");
            return;
        }
        
        System.out.println("Lista Enlazada de Movimientos (Cabeza -> Final):");
        NodoMovimiento actual = cabeza;
        int posicion = 1;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        while (actual != null) {
            System.out.println("  " + posicion + ". [" + actual.tipo.toUpperCase() + "] " +
                             "Fecha: " + sdf.format(actual.fecha) + 
                             " - Cantidad: " + actual.cantidad +
                             " - Producto: " + actual.productoId +
                             " - ID: " + actual.movimientoId);
            actual = actual.siguiente;
            posicion++;
        }
    }
    
    /**
     * Obtener movimientos por tipo
     * @param tipo Tipo de movimiento (entrada/salida)
     * @return Lista de movimientos del tipo especificado
     */
    public List<Movimientos> obtenerMovimientosPorTipo(String tipo) {
        List<Movimientos> movimientos = new ArrayList<>();
        NodoMovimiento actual = cabeza;
        
        while (actual != null) {
            if (actual.tipo.equalsIgnoreCase(tipo)) {
                Movimientos mov = new Movimientos();
                mov.setMovimientoId(actual.movimientoId);
                mov.setProductoId(actual.productoId);
                mov.setTipo_movimiento(actual.tipo);
                mov.setFecha(actual.fecha);
                mov.setCantidad(actual.cantidad);
                movimientos.add(mov);
            }
            actual = actual.siguiente;
        }
        
        return movimientos;
    }
    
    /**
     * Obtener movimientos por rango de fechas
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de movimientos en el rango especificado
     */
    public List<Movimientos> obtenerMovimientosPorFechas(Date fechaInicio, Date fechaFin) {
        List<Movimientos> movimientos = new ArrayList<>();
        NodoMovimiento actual = cabeza;
        
        while (actual != null) {
            if (actual.fecha != null && 
                !actual.fecha.before(fechaInicio) && 
                !actual.fecha.after(fechaFin)) {
                Movimientos mov = new Movimientos();
                mov.setMovimientoId(actual.movimientoId);
                mov.setProductoId(actual.productoId);
                mov.setTipo_movimiento(actual.tipo);
                mov.setFecha(actual.fecha);
                mov.setCantidad(actual.cantidad);
                movimientos.add(mov);
            }
            actual = actual.siguiente;
        }
        
        return movimientos;
    }
    
    /**
     * Obtener movimientos por producto
     * @param productoId ID del producto
     * @return Lista de movimientos del producto especificado
     */
    public List<Movimientos> obtenerMovimientosPorProducto(String productoId) {
        List<Movimientos> movimientos = new ArrayList<>();
        NodoMovimiento actual = cabeza;
        
        while (actual != null) {
            if (actual.productoId.equals(productoId)) {
                Movimientos mov = new Movimientos();
                mov.setMovimientoId(actual.movimientoId);
                mov.setProductoId(actual.productoId);
                mov.setTipo_movimiento(actual.tipo);
                mov.setFecha(actual.fecha);
                mov.setCantidad(actual.cantidad);
                movimientos.add(mov);
            }
            actual = actual.siguiente;
        }
        
        return movimientos;
    }
    
    /**
     * Verifica si la lista está vacía
     * @return true si está vacía, false en caso contrario
     */
    public boolean estaVacia() {
        return cabeza == null;
    }
    
    /**
     * Obtiene el tamaño de la lista
     * @return Número de elementos en la lista
     */
    public int getTamaño() {
        return tamaño;
    }
    
    /**
     * Limpia toda la lista
     */
    public void limpiar() {
        cabeza = null;
        tamaño = 0;
    }
}
