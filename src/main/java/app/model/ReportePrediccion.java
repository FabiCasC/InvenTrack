package app.model;

import java.util.List;

/**
 * Modelo para el reporte de predicción (RF8.2)
 */
public class ReportePrediccion {
    private String fechaGeneracion;
    private List<PrediccionProducto> predicciones;
    private int totalProductosAnalizados;
    
    public static class PrediccionProducto {
        private String productoId;
        private String nombre;
        private int stockActual;
        private int stockProyectado;
        private String prediccion; // "subirá", "bajará", "estable"
        private String riesgo; // "BAJO", "MEDIO", "ALTO"
        private String razon;
        private double confianza; // 0.0 a 1.0
        
        // Getters y setters
        public String getProductoId() { return productoId; }
        public void setProductoId(String productoId) { this.productoId = productoId; }
        
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        
        public int getStockActual() { return stockActual; }
        public void setStockActual(int stockActual) { this.stockActual = stockActual; }
        
        public int getStockProyectado() { return stockProyectado; }
        public void setStockProyectado(int stockProyectado) { this.stockProyectado = stockProyectado; }
        
        public String getPrediccion() { return prediccion; }
        public void setPrediccion(String prediccion) { this.prediccion = prediccion; }
        
        public String getRiesgo() { return riesgo; }
        public void setRiesgo(String riesgo) { this.riesgo = riesgo; }
        
        public String getRazon() { return razon; }
        public void setRazon(String razon) { this.razon = razon; }
        
        public double getConfianza() { return confianza; }
        public void setConfianza(double confianza) { this.confianza = confianza; }
    }
    
    // Getters y setters
    public String getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(String fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    
    public List<PrediccionProducto> getPredicciones() { return predicciones; }
    public void setPredicciones(List<PrediccionProducto> predicciones) { this.predicciones = predicciones; }
    
    public int getTotalProductosAnalizados() { return totalProductosAnalizados; }
    public void setTotalProductosAnalizados(int totalProductosAnalizados) { this.totalProductosAnalizados = totalProductosAnalizados; }
}

