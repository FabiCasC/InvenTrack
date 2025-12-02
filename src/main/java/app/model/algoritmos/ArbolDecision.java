package app.model.algoritmos;

import app.model.Movimientos;
import app.model.Productos;
import java.util.Calendar;
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
                EstadisticaProducto stats = new EstadisticaProducto(producto.getProductoId());
                stats.setCategoria(producto.getTipo()); // RF4.4 - Categoría
                estadisticas.put(producto.getProductoId(), stats);
            } else {
                // Actualizar categoría si cambió
                estadisticas.get(producto.getProductoId()).setCategoria(producto.getTipo());
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
     * RF4.4 - Incluye ventas históricas, categorías, estacionalidad, comportamiento de pedidos
     */
    public static class EstadisticaProducto {
        private String productoId;
        private String categoria; // Perecible / No Perecible
        private int totalEntradas;
        private int totalSalidas;
        private Date ultimaEntrada;
        private Date ultimaSalida;
        private double promedioMensualSalidas;
        private int movimientosUltimos30Dias;
        
        // RF4.4 - Ventas históricas por mes para detectar estacionalidad
        private Map<Integer, Integer> ventasPorMes; // Mes (1-12) -> Cantidad vendida
        
        // RF4.4 - Comportamiento de pedidos
        private int frecuenciaPedidos; // Número de pedidos realizados
        private double promedioCantidadPorPedido;
        
        public EstadisticaProducto(String productoId) {
            this.productoId = productoId;
            this.totalEntradas = 0;
            this.totalSalidas = 0;
            this.ventasPorMes = new HashMap<>();
            this.frecuenciaPedidos = 0;
            this.promedioCantidadPorPedido = 0.0;
        }
        
        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }
        
        public void agregarEntrada(int cantidad, Date fecha) {
            totalEntradas += cantidad;
            frecuenciaPedidos++;
            promedioCantidadPorPedido = totalEntradas / (double) Math.max(1, frecuenciaPedidos);
            
            if (ultimaEntrada == null || fecha.after(ultimaEntrada)) {
                ultimaEntrada = fecha;
            }
        }
        
        public void agregarSalida(int cantidad, Date fecha) {
            totalSalidas += cantidad;
            if (ultimaSalida == null || fecha.after(ultimaSalida)) {
                ultimaSalida = fecha;
            }
            
            // RF4.4 - Estacionalidad: registrar ventas por mes
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(fecha);
            int mes = cal.get(Calendar.MONTH) + 1; // 1-12
            ventasPorMes.put(mes, ventasPorMes.getOrDefault(mes, 0) + cantidad);
            
            // Calcular promedio mensual
            movimientosUltimos30Dias++;
            if (movimientosUltimos30Dias > 0) {
                promedioMensualSalidas = totalSalidas / 30.0;
            }
        }
        
        /**
         * RF4.4 - Predicción de demanda basada en:
         * - Ventas históricas
         * - Categorías
         * - Estacionalidad
         * - Comportamiento de pedidos
         */
        public String predecirDemanda(Productos producto) {
            if (totalSalidas == 0) {
                return "estable";
            }
            
            // Factor 1: Ratio entrada/salida
            double ratioEntradaSalida = (double) totalEntradas / totalSalidas;
            
            // Factor 2: Stock actual vs mínimos/máximos
            int stockActual = producto.getStock_actual();
            int stockMinimo = producto.getStock_minimo();
            int stockMaximo = producto.getStock_maximo();
            double porcentajeStock = (double) stockActual / stockMaximo;
            
            // Factor 3: RF4.4 - Estacionalidad - detectar si estamos en temporada alta
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int mesActual = cal.get(Calendar.MONTH) + 1;
            int ventasMesActual = ventasPorMes.getOrDefault(mesActual, 0);
            double promedioVentasMensuales = calcularPromedioVentasMensuales();
            boolean temporadaAlta = promedioVentasMensuales > 0 && 
                                   ventasMesActual > promedioVentasMensuales * 1.2;
            
            // Factor 4: RF4.4 - Categoría (perecibles tienen demanda más variable)
            boolean esPerecible = "Perecible".equalsIgnoreCase(categoria);
            double factorCategoria = esPerecible ? 1.2 : 1.0;
            
            // Factor 5: RF4.4 - Comportamiento de pedidos (frecuencia alta indica demanda estable)
            double factorFrecuencia = frecuenciaPedidos > 10 ? 1.1 : 0.9;
            
            // Árbol de decisión con factores considerados
            if (stockActual <= stockMinimo) {
                // Stock crítico: considerar temporada y frecuencia
                if (temporadaAlta || (ratioEntradaSalida < 0.8 * factorCategoria)) {
                    return "subirá"; // Crítico: bajo stock + temporada alta o alta variabilidad
                }
                return "subirá"; // Bajo stock requiere reabastecimiento
            } else if (stockActual >= stockMaximo * 0.9) {
                // Stock alto: considerar si es temporada baja y frecuencia estable
                if (!temporadaAlta && ratioEntradaSalida > 1.5 / factorFrecuencia) {
                    return "bajará"; // Exceso de stock fuera de temporada con frecuencia estable
                }
                return "estable"; // Stock alto pero puede ser necesario
            } else if (temporadaAlta && porcentajeStock < (0.6 * factorCategoria)) {
                return "subirá"; // Temporada alta y stock medio-bajo (ajustado por categoría)
            } else if (!temporadaAlta && porcentajeStock > 0.7 && ratioEntradaSalida > (1.3 / factorFrecuencia)) {
                return "bajará"; // Fuera de temporada con stock suficiente y frecuencia estable
            } else {
                return "estable"; // Estado equilibrado
            }
        }
        
        /**
         * RF4.4 - Calcular promedio de ventas mensuales para detectar estacionalidad
         */
        private double calcularPromedioVentasMensuales() {
            if (ventasPorMes.isEmpty()) {
                return 0.0;
            }
            int total = 0;
            for (int ventas : ventasPorMes.values()) {
                total += ventas;
            }
            return total / (double) ventasPorMes.size();
        }
        
        /**
         * RF4.4 - Detectar si hay patrón estacional
         */
        public boolean tienePatronEstacional() {
            if (ventasPorMes.size() < 3) {
                return false; // Necesita al menos 3 meses de datos
            }
            double promedio = calcularPromedioVentasMensuales();
            if (promedio == 0) {
                return false;
            }
            
            // Verificar si hay variación significativa (más del 30%)
            for (int ventas : ventasPorMes.values()) {
                double variacion = Math.abs(ventas - promedio) / promedio;
                if (variacion > 0.3) {
                    return true; // Hay estacionalidad
                }
            }
            return false;
        }
        
        public boolean tieneTendenciaBajista() {
            return totalSalidas > totalEntradas && promedioMensualSalidas > 0;
        }
        
        // Getters
        public String getProductoId() { return productoId; }
        public String getCategoria() { return categoria; }
        public int getTotalEntradas() { return totalEntradas; }
        public int getTotalSalidas() { return totalSalidas; }
        public double getPromedioMensualSalidas() { return promedioMensualSalidas; }
        public int getFrecuenciaPedidos() { return frecuenciaPedidos; }
        public double getPromedioCantidadPorPedido() { return promedioCantidadPorPedido; }
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
