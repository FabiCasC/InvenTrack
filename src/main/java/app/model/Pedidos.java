
package app.model;

import java.util.Date;


public class Pedidos {
    private String pedidoId;
    private String productoId;
    private int cantidadSolicitada;
    private String estado;
    private Date fecha;

    public Pedidos() {
    }

    public Pedidos(String pedidoId, String productoId, int cantidadSolicitada, String estado, Date fecha) {
        this.pedidoId = pedidoId;
        this.productoId = productoId;
        this.cantidadSolicitada = cantidadSolicitada;
        this.estado = estado;
        this.fecha = fecha;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(String pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public int getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    public void setCantidadSolicitada(int cantidadSolicitada) {
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Pedidos{" + "pedidoId=" + pedidoId + ", productoId=" + productoId + ", cantidadSolicitada=" + cantidadSolicitada + ", estado=" + estado + ", fecha=" + fecha + '}';
    }
    
    
}
