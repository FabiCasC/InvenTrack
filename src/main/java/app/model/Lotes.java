
package app.model;

import java.util.Date;

public class Lotes {
    private String loteId;
    private int cantidad;
    private Date fecha_Entrada;
    private Date fecha_Vencimiento;

    public Lotes() {
    }

    public Lotes(String loteId, int cantidad, Date fecha_Entrada, Date fecha_Vencimiento) {
        this.loteId = loteId;
        this.cantidad = cantidad;
        this.fecha_Entrada = fecha_Entrada;
        this.fecha_Vencimiento = fecha_Vencimiento;
    }

    public Date getFecha_Vencimiento() {
        return fecha_Vencimiento;
    }

    public void setFecha_Vencimiento(Date fecha_Vencimiento) {
        this.fecha_Vencimiento = fecha_Vencimiento;
    }

    public String getLoteId() {
        return loteId;
    }

    public void setLoteId(String loteId) {
        this.loteId = loteId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Date getFecha_Entrada() {
        return fecha_Entrada;
    }

    public void setFecha_Entrada(Date fecha_Entrada) {
        this.fecha_Entrada = fecha_Entrada;
    }

    @Override
    public String toString() {
        return "Lotes{" + "loteId=" + loteId + ", cantidad=" + cantidad + ", fecha_Entrada=" + fecha_Entrada + ", fecha_Vencimiento=" + fecha_Vencimiento + '}';
    }
    
     
}
