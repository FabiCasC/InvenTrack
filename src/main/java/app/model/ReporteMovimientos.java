package app.model;

import java.util.List;

/**
 * Modelo para el reporte de movimientos (RF8.3)
 */
public class ReporteMovimientos {
    private String fechaInicio;
    private String fechaFin;
    private List<ItemMovimiento> movimientos;
    private ResumenMovimientos resumen;
    
    public static class ItemMovimiento {
        private String movimientoId;
        private String productoId;
        private String nombreProducto;
        private String tipo; // "entrada" o "salida"
        private int cantidad;
        private String fecha;
        private String algoritmo;
        private String usuarioId;
        
        // Getters y setters
        public String getMovimientoId() { return movimientoId; }
        public void setMovimientoId(String movimientoId) { this.movimientoId = movimientoId; }
        
        public String getProductoId() { return productoId; }
        public void setProductoId(String productoId) { this.productoId = productoId; }
        
        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
        
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        
        public String getFecha() { return fecha; }
        public void setFecha(String fecha) { this.fecha = fecha; }
        
        public String getAlgoritmo() { return algoritmo; }
        public void setAlgoritmo(String algoritmo) { this.algoritmo = algoritmo; }
        
        public String getUsuarioId() { return usuarioId; }
        public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    }
    
    public static class ResumenMovimientos {
        private int totalMovimientos;
        private int totalEntradas;
        private int totalSalidas;
        private int cantidadTotalEntradas;
        private int cantidadTotalSalidas;
        
        // Getters y setters
        public int getTotalMovimientos() { return totalMovimientos; }
        public void setTotalMovimientos(int totalMovimientos) { this.totalMovimientos = totalMovimientos; }
        
        public int getTotalEntradas() { return totalEntradas; }
        public void setTotalEntradas(int totalEntradas) { this.totalEntradas = totalEntradas; }
        
        public int getTotalSalidas() { return totalSalidas; }
        public void setTotalSalidas(int totalSalidas) { this.totalSalidas = totalSalidas; }
        
        public int getCantidadTotalEntradas() { return cantidadTotalEntradas; }
        public void setCantidadTotalEntradas(int cantidadTotalEntradas) { this.cantidadTotalEntradas = cantidadTotalEntradas; }
        
        public int getCantidadTotalSalidas() { return cantidadTotalSalidas; }
        public void setCantidadTotalSalidas(int cantidadTotalSalidas) { this.cantidadTotalSalidas = cantidadTotalSalidas; }
    }
    
    // Getters y setters
    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    
    public List<ItemMovimiento> getMovimientos() { return movimientos; }
    public void setMovimientos(List<ItemMovimiento> movimientos) { this.movimientos = movimientos; }
    
    public ResumenMovimientos getResumen() { return resumen; }
    public void setResumen(ResumenMovimientos resumen) { this.resumen = resumen; }
}

