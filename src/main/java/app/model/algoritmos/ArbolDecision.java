package app.model.algoritmos;

import app.model.Movimientos;
import app.model.Productos;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Árbol de Decisión para Alertas y Predicciones
 * RF4.4, RF4.6 - Basado en ventas históricas, categorías, estacionalidad, comportamiento de pedidos
 */
public class ArbolDecision {
    // Estadísticas acumuladas para el árbol
    private Map<String, EstadisticaProducto> estadisticas;
    
    public ArbolDecision() {
        this.estadisticas = new HashMap<>();
    }
    
    /**
     * Entrena el árbol con nuevos datos de movimientos
     * RF4.4 - Debe entrenarse cada vez que se acumulen nuevos datos
     */
    public void entrenar(List<Movimientos> movimientos, List<Productos> productos) {
        if (movimientos == null || productos == null) {
            return;
        }
        
        // Inicializar estadísticas si no existen
        for (Productos producto : productos) {
            if (!estadisticas.containsKey(producto.getProductoId())) {
                estadisticas.put(producto.getProductoId(), new EstadisticaProducto(producto.getProductoId()));
            }
        }
        
        // Procesar movimientos para actualizar estadísticas
        for (Movimientos movimiento : movimientos) {
            String productoId = movimiento.getProductoId();
            if (estadisticas.containsKey(productoId)) {
                EstadisticaProducto stats = estadisticas.get(productoId);
                
                if ("entrada".equalsIgnoreCase(movimiento.getTipo_movimiento())) {
                    stats.agregarEntrada(movimiento.getCantidad(), movimiento.getFecha());
                } else if ("salida".equalsIgnoreCase(movimiento.getTipo_movimiento())) {
                    stats.agregarSalida(movimiento.getCantidad(), movimiento.getFecha());
                }
            }
        }
    }
    
    /**
     * Genera predicción de demanda
     * RF4.4 - Predicción: "subirá", "bajará" o "estable"
     */
    public String predecirDemanda(String productoId, Productos producto) {
        if (!estadisticas.containsKey(productoId)) {
            return "estable"; // Sin datos históricos
        }
        
        EstadisticaProducto stats = estadisticas.get(productoId);
        return stats.predecirDemanda(producto);
    }
    
    /**
     * Genera alerta basada en árbol de decisión
     * RF4.6 - Sistema de Alertas Inteligentes
     */
    public Alerta generarAlerta(String productoId, Productos producto) {
        if (producto == null) {
            return null;
        }
        
        Alerta alerta = new Alerta();
        alerta.setProductoId(productoId);
        alerta.setProductoNombre(producto.getNombre());
        
        // Evaluar condiciones del árbol de decisión
        boolean stockCritico = producto.getStock_actual() <= producto.getStock_minimo();
        boolean stockAlto = producto.getStock_actual() >= (producto.getStock_maximo() * 0.9);
        boolean tendenciaBajista = false;
        
        if (estadisticas.containsKey(productoId)) {
            EstadisticaProducto stats = estadisticas.get(productoId);
            tendenciaBajista = stats.tieneTendenciaBajista();
        }
        
        // Árbol de decisión simple
        if (stockCritico && tendenciaBajista) {
            alerta.setNivel("CRITICO");
            alerta.setMensaje("Stock crítico con tendencia bajista - Reabastecimiento urgente requerido");
            alerta.setAccionRecomendada("Reabastecer inmediatamente");
        } else if (stockCritico) {
            alerta.setNivel("ALTO");
            alerta.setMensaje("Stock crítico - Reabastecer pronto");
            alerta.setAccionRecomendada("Planificar reabastecimiento");
        } else if (stockAlto) {
            alerta.setNivel("MEDIO");
            alerta.setMensaje("Stock cerca del máximo - Considerar reducir compras");
            alerta.setAccionRecomendada("Revisar estrategia de compras");
        } else if (tendenciaBajista) {
            alerta.setNivel("MEDIO");
            alerta.setMensaje("Tendencia bajista detectada - Monitorear de cerca");
            alerta.setAccionRecomendada("Aumentar frecuencia de monitoreo");
        } else {
            alerta.setNivel("BAJO");
            alerta.setMensaje("Estado normal");
            alerta.setAccionRecomendada("Continuar monitoreo regular");
        }
        
        return alerta;
    }
    
    /**
     * Clase para almacenar estadísticas de un producto
     */
    public static class EstadisticaProducto {
        private String productoId;
        private int totalEntradas;
        private int totalSalidas;
        private Date ultimaEntrada;
        private Date ultimaSalida;
        private double promedioMensualSalidas;
        private int movimientosUltimos30Dias;
        
        public EstadisticaProducto(String productoId) {
            this.productoId = productoId;
            this.totalEntradas = 0;
            this.totalSalidas = 0;
        }
        
        public void agregarEntrada(int cantidad, Date fecha) {
            totalEntradas += cantidad;
            if (ultimaEntrada == null || fecha.after(ultimaEntrada)) {
                ultimaEntrada = fecha;
            }
        }
        
        public void agregarSalida(int cantidad, Date fecha) {
            totalSalidas += cantidad;
            if (ultimaSalida == null || fecha.after(ultimaSalida)) {
                ultimaSalida = fecha;
            }
            
            // Calcular promedio mensual (simplificado)
            movimientosUltimos30Dias++;
            if (movimientosUltimos30Dias > 0) {
                promedioMensualSalidas = totalSalidas / 30.0; // Promedio diario * 30
            }
        }
        
        public String predecirDemanda(Productos producto) {
            if (totalSalidas == 0) {
                return "estable";
            }
            
            double ratioEntradaSalida = (double) totalEntradas / totalSalidas;
            int stockActual = producto.getStock_actual();
            int stockMinimo = producto.getStock_minimo();
            
            if (stockActual <= stockMinimo && ratioEntradaSalida < 1.0) {
                return "subirá"; // Necesita más stock
            } else if (stockActual > (producto.getStock_maximo() * 0.8) && ratioEntradaSalida > 1.5) {
                return "bajará"; // Exceso de stock
            } else {
                return "estable";
            }
        }
        
        public boolean tieneTendenciaBajista() {
            // Tendencia bajista si las salidas superan las entradas consistentemente
            return totalSalidas > totalEntradas && promedioMensualSalidas > 0;
        }
        
        // Getters
        public String getProductoId() { return productoId; }
        public int getTotalEntradas() { return totalEntradas; }
        public int getTotalSalidas() { return totalSalidas; }
        public double getPromedioMensualSalidas() { return promedioMensualSalidas; }
    }
    
    /**
     * Clase para representar una alerta
     */
    public static class Alerta {
        private String productoId;
        private String productoNombre;
        private String nivel; // CRITICO, ALTO, MEDIO, BAJO
        private String mensaje;
        private String accionRecomendada;
        private Date fecha;
        
        public Alerta() {
            this.fecha = new Date();
        }
        
        // Getters y Setters
        public String getProductoId() { return productoId; }
        public void setProductoId(String productoId) { this.productoId = productoId; }
        
        public String getProductoNombre() { return productoNombre; }
        public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
        
        public String getNivel() { return nivel; }
        public void setNivel(String nivel) { this.nivel = nivel; }
        
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        
        public String getAccionRecomendada() { return accionRecomendada; }
        public void setAccionRecomendada(String accionRecomendada) { this.accionRecomendada = accionRecomendada; }
        
        public Date getFecha() { return fecha; }
        public void setFecha(Date fecha) { this.fecha = fecha; }
    }
}
