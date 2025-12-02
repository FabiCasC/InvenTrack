package app.model;

import java.util.List;

/**
 * Modelo para el reporte de inventario crítico (RF8.1)
 */
public class ReporteInventarioCritico {
    private List<ItemInventarioCritico> productosCriticos;
    private int totalProductos;
    private int productosStockBajo;
    private int productosPorVencer;
    
    public static class ItemInventarioCritico {
        private String productoId;
        private String nombre;
        private String tipo;
        private int stockActual;
        private int stockMinimo;
        private int stockMaximo;
        private String proveedor;
        private String nivelCritico; // "CRÍTICO", "ALERTA", "ADVERTENCIA"
        private String motivo; // "Stock bajo", "Próximo a vencer", "Ambos"
        private int diasParaVencimiento;
        
        // Getters y setters
        public String getProductoId() { return productoId; }
        public void setProductoId(String productoId) { this.productoId = productoId; }
        
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        
        public int getStockActual() { return stockActual; }
        public void setStockActual(int stockActual) { this.stockActual = stockActual; }
        
        public int getStockMinimo() { return stockMinimo; }
        public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
        
        public int getStockMaximo() { return stockMaximo; }
        public void setStockMaximo(int stockMaximo) { this.stockMaximo = stockMaximo; }
        
        public String getProveedor() { return proveedor; }
        public void setProveedor(String proveedor) { this.proveedor = proveedor; }
        
        public String getNivelCritico() { return nivelCritico; }
        public void setNivelCritico(String nivelCritico) { this.nivelCritico = nivelCritico; }
        
        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
        
        public int getDiasParaVencimiento() { return diasParaVencimiento; }
        public void setDiasParaVencimiento(int diasParaVencimiento) { this.diasParaVencimiento = diasParaVencimiento; }
    }
    
    // Getters y setters
    public List<ItemInventarioCritico> getProductosCriticos() { return productosCriticos; }
    public void setProductosCriticos(List<ItemInventarioCritico> productosCriticos) { this.productosCriticos = productosCriticos; }
    
    public int getTotalProductos() { return totalProductos; }
    public void setTotalProductos(int totalProductos) { this.totalProductos = totalProductos; }
    
    public int getProductosStockBajo() { return productosStockBajo; }
    public void setProductosStockBajo(int productosStockBajo) { this.productosStockBajo = productosStockBajo; }
    
    public int getProductosPorVencer() { return productosPorVencer; }
    public void setProductosPorVencer(int productosPorVencer) { this.productosPorVencer = productosPorVencer; }
}

