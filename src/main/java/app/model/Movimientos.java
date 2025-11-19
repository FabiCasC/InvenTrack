
package app.model;

import java.util.Date;

public class Movimientos {
    private String movimientoId;
    private String productoId;
    private String tipo_movimiento;
    private int cantidad;
    private Date fecha;
    private String algoritmo;

    public Movimientos() {
    }

    public Movimientos(String movimientoId, String productoId, String tipo_movimiento, int cantidad, Date fecha, String algoritmo) {
        this.movimientoId = movimientoId;
        this.productoId = productoId;
        this.tipo_movimiento = tipo_movimiento;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.algoritmo = algoritmo;
    }

    public String getAlgoritmo() {
        return algoritmo;
    }

    public void setAlgoritmo(String algoritmo) {
        this.algoritmo = algoritmo;
    }

    public String getMovimientoId() {
        return movimientoId;
    }

    public void setMovimientoId(String movimientoId) {
        this.movimientoId = movimientoId;
    }

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public String getTipo_movimiento() {
        return tipo_movimiento;
    }

    public void setTipo_movimiento(String tipo_movimiento) {
        this.tipo_movimiento = tipo_movimiento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Movimientos{" + "movimientoId=" + movimientoId + ", productoId=" + productoId + ", tipo_movimiento=" + tipo_movimiento + ", cantidad=" + cantidad + ", fecha=" + fecha + ", algoritmo=" + algoritmo + '}';
    }
    
}
