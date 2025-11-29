package app.service;

import app.model.Movimientos;
import app.model.Productos;
import app.model.algoritmos.ArbolDecision;
import app.repository.MovimientoRepository;
import app.repository.ProductoRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Service para predicción básica de inventario
 * RF4.4 - Predicción basada en movimientos históricos y tendencias
 */
public class PrediccionService {
    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;
    private final ArbolDecision arbolDecision;

    public PrediccionService() {
        this.productoRepository = new ProductoRepository();
        this.movimientoRepository = new MovimientoRepository();
        this.arbolDecision = new ArbolDecision();
    }

    /**
     * Genera predicción de inventario para un producto
     * @param productoId ID del producto
     * @param diasProyeccion Días a proyectar en el futuro
     * @return Predicción con stock proyectado
     */
    public PrediccionInventario predecirInventario(String productoId, int diasProyeccion) 
            throws ExecutionException, InterruptedException {
        
        Productos producto = productoRepository.findById(productoId);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado: " + productoId);
        }
        
        // Obtener movimientos históricos
        List<Movimientos> movimientos = movimientoRepository.findByProductoId(productoId);
        List<Productos> productos = productoRepository.findAll();
        
        // Entrenar árbol de decisión
        arbolDecision.entrenar(movimientos, productos);
        
        // Calcular proyección
        int stockActual = producto.getStock_actual();
        double consumoPromedioDiario = calcularConsumoPromedioDiario(movimientos);
        int stockProyectado = (int) (stockActual - (consumoPromedioDiario * diasProyeccion));
        
        // Generar predicción de demanda
        String prediccionDemanda = arbolDecision.predecirDemanda(productoId, producto);
        
        // Crear objeto de predicción
        PrediccionInventario prediccion = new PrediccionInventario();
        prediccion.setProductoId(productoId);
        prediccion.setProductoNombre(producto.getNombre());
        prediccion.setStockActual(stockActual);
        prediccion.setStockProyectado(stockProyectado);
        prediccion.setDiasProyeccion(diasProyeccion);
        prediccion.setConsumoPromedioDiario(consumoPromedioDiario);
        prediccion.setPrediccionDemanda(prediccionDemanda);
        prediccion.setFechaProyeccion(calcularFechaFutura(diasProyeccion));
        
        // Calcular riesgo
        if (stockProyectado <= producto.getStock_minimo()) {
            prediccion.setRiesgo("ALTO - Stock proyectado por debajo del mínimo");
        } else if (stockProyectado <= (producto.getStock_minimo() * 1.5)) {
            prediccion.setRiesgo("MEDIO - Stock proyectado cerca del mínimo");
        } else {
            prediccion.setRiesgo("BAJO - Stock proyectado en rango seguro");
        }
        
        return prediccion;
    }

    /**
     * Calcula el consumo promedio diario basado en movimientos históricos
     */
    private double calcularConsumoPromedioDiario(List<Movimientos> movimientos) {
        if (movimientos == null || movimientos.isEmpty()) {
            return 0.0;
        }
        
        // Filtrar solo salidas
        int totalSalidas = 0;
        Date fechaMasAntigua = null;
        Date fechaMasReciente = null;
        
        for (Movimientos movimiento : movimientos) {
            if ("salida".equalsIgnoreCase(movimiento.getTipo_movimiento())) {
                totalSalidas += movimiento.getCantidad();
                
                if (fechaMasAntigua == null || movimiento.getFecha().before(fechaMasAntigua)) {
                    fechaMasAntigua = movimiento.getFecha();
                }
                if (fechaMasReciente == null || movimiento.getFecha().after(fechaMasReciente)) {
                    fechaMasReciente = movimiento.getFecha();
                }
            }
        }
        
        if (fechaMasAntigua == null || fechaMasReciente == null) {
            return 0.0;
        }
        
        // Calcular días entre primera y última salida
        long milisegundos = fechaMasReciente.getTime() - fechaMasAntigua.getTime();
        long dias = milisegundos / (1000 * 60 * 60 * 24);
        
        if (dias == 0) {
            dias = 1; // Evitar división por cero
        }
        
        return (double) totalSalidas / dias;
    }

    /**
     * Calcula una fecha futura
     */
    private Date calcularFechaFutura(int dias) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }

    /**
     * Clase para representar una predicción de inventario
     */
    public static class PrediccionInventario {
        private String productoId;
        private String productoNombre;
        private int stockActual;
        private int stockProyectado;
        private int diasProyeccion;
        private double consumoPromedioDiario;
        private String prediccionDemanda; // "subirá", "bajará", "estable"
        private String riesgo;
        private Date fechaProyeccion;
        
        // Getters y Setters
        public String getProductoId() { return productoId; }
        public void setProductoId(String productoId) { this.productoId = productoId; }
        
        public String getProductoNombre() { return productoNombre; }
        public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
        
        public int getStockActual() { return stockActual; }
        public void setStockActual(int stockActual) { this.stockActual = stockActual; }
        
        public int getStockProyectado() { return stockProyectado; }
        public void setStockProyectado(int stockProyectado) { this.stockProyectado = stockProyectado; }
        
        public int getDiasProyeccion() { return diasProyeccion; }
        public void setDiasProyeccion(int diasProyeccion) { this.diasProyeccion = diasProyeccion; }
        
        public double getConsumoPromedioDiario() { return consumoPromedioDiario; }
        public void setConsumoPromedioDiario(double consumoPromedioDiario) { 
            this.consumoPromedioDiario = consumoPromedioDiario; 
        }
        
        public String getPrediccionDemanda() { return prediccionDemanda; }
        public void setPrediccionDemanda(String prediccionDemanda) { 
            this.prediccionDemanda = prediccionDemanda; 
        }
        
        public String getRiesgo() { return riesgo; }
        public void setRiesgo(String riesgo) { this.riesgo = riesgo; }
        
        public Date getFechaProyeccion() { return fechaProyeccion; }
        public void setFechaProyeccion(Date fechaProyeccion) { this.fechaProyeccion = fechaProyeccion; }
        
        @Override
        public String toString() {
            return String.format(
                "Predicción para %s:\n" +
                "  Stock Actual: %d\n" +
                "  Stock Proyectado (%d días): %d\n" +
                "  Consumo Promedio Diario: %.2f\n" +
                "  Predicción Demanda: %s\n" +
                "  Riesgo: %s",
                productoNombre, stockActual, diasProyeccion, stockProyectado,
                consumoPromedioDiario, prediccionDemanda, riesgo
            );
        }
    }
}

