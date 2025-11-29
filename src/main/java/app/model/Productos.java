package app.model;

import java.util.Date;

/**
 * Modelo base para Productos
 * RF2.1 - Datos mínimos requeridos: id, nombre, categoría, stock actual, proveedor, fecha de ingreso
 */
public class Productos {
    private String productoId;
    private String nombre;
    private String tipo; // categoría: perecible / no perecible
    private String metodo_rotacion;
    private int stock_actual;
    private int stock_minimo;
    private int stock_maximo;
    private String proveedorId;
    private Date fechaIngreso;

    public Productos() {
    }

    public Productos(String productoId, String nombre, String tipo, String metodo_rotacion, int stock_actual, int stock_minimo, int stock_maximo, String proveedorId, Date fechaIngreso) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.tipo = tipo;
        this.metodo_rotacion = metodo_rotacion;
        this.stock_actual = stock_actual;
        this.stock_minimo = stock_minimo;
        this.stock_maximo = stock_maximo;
        this.proveedorId = proveedorId;
        this.fechaIngreso = fechaIngreso;
    }

    public String getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(String proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMetodo_rotacion() {
        return metodo_rotacion;
    }

    public void setMetodo_rotacion(String metodo_rotacion) {
        this.metodo_rotacion = metodo_rotacion;
    }

    public int getStock_actual() {
        return stock_actual;
    }

    public void setStock_actual(int stock_actual) {
        this.stock_actual = stock_actual;
    }

    public int getStock_minimo() {
        return stock_minimo;
    }

    public void setStock_minimo(int stock_minimo) {
        this.stock_minimo = stock_minimo;
    }

    public int getStock_maximo() {
        return stock_maximo;
    }

    public void setStock_maximo(int stock_maximo) {
        this.stock_maximo = stock_maximo;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    @Override
    public String toString() {
        return "Productos{" + "productoId=" + productoId + ", nombre=" + nombre + ", tipo=" + tipo + ", metodo_rotacion=" + metodo_rotacion + ", stock_actual=" + stock_actual + ", stock_minimo=" + stock_minimo + ", stock_maximo=" + stock_maximo + ", proveedorId=" + proveedorId + ", fechaIngreso=" + fechaIngreso + '}';
    }
    
    
}
