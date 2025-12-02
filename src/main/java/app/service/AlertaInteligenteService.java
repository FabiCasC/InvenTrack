package app.service;

import app.model.Movimientos;
import app.model.Productos;
import app.model.Proveedores;
import app.model.algoritmos.ArbolDecision;
import app.repository.MovimientoRepository;
import app.repository.ProductoRepository;
import app.repository.ProveedorRepository;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * RF4.6 - Sistema de Alertas Inteligentes
 * Emite alertas cuando:
 * - Stock proyectado llegue a nivel crítico
 * - Se detecten patrones de venta inusuales
 * - Un proveedor crítico tenga demoras
 */
public class AlertaInteligenteService {
    
    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;
    private final ProveedorRepository proveedorRepository;
    private final PrediccionService prediccionService;
    private final GrafoService grafoService;
    private final ArbolDecision arbolDecision;
    
    // RF4.6 - Registro de alertas emitidas
    private Map<String, Date> ultimasAlertas; // productoId -> fecha última alerta
    private Map<String, Integer> contadorAlertasProveedor; // proveedorId -> contador
    
    public AlertaInteligenteService() {
        this.productoRepository = new ProductoRepository();
        this.movimientoRepository = new MovimientoRepository();
        this.proveedorRepository = new ProveedorRepository();
        this.prediccionService = new PrediccionService();
        this.grafoService = new GrafoService();
        this.arbolDecision = new ArbolDecision();
        this.ultimasAlertas = new HashMap<>();
        this.contadorAlertasProveedor = new HashMap<>();
    }
    
    /**
     * RF4.6 - Genera todas las alertas inteligentes del sistema
     * @return Lista de alertas detectadas
     */
    public List<ArbolDecision.Alerta> generarAlertasInteligentes() throws ExecutionException, InterruptedException {
        List<ArbolDecision.Alerta> alertas = new ArrayList<>();
        
        // Entrenar árbol de decisión con datos actuales
        List<Movimientos> movimientos = movimientoRepository.findAll();
        List<Productos> productos = productoRepository.findAll();
        arbolDecision.entrenar(movimientos, productos);
        
        // RF4.6.1 - Alerta: Stock proyectado llegue a nivel crítico
        alertas.addAll(detectarStockProyectadoCritico(productos));
        
        // RF4.6.2 - Alerta: Patrones de venta inusuales
        alertas.addAll(detectarPatronesVentaInusuales(productos, movimientos));
        
        // RF4.6.3 - Alerta: Proveedor crítico con demoras
        alertas.addAll(detectarProveedorCriticoConDemoras());
        
        return alertas;
    }
    
    /**
     * RF4.6.1 - Detecta cuando el stock proyectado llegue a nivel crítico
     */
    private List<ArbolDecision.Alerta> detectarStockProyectadoCritico(List<Productos> productos) 
            throws ExecutionException, InterruptedException {
        List<ArbolDecision.Alerta> alertas = new ArrayList<>();
        
        for (Productos producto : productos) {
            try {
                // Proyectar stock para los próximos 7, 15 y 30 días
                int[] diasProyeccion = {7, 15, 30};
                
                for (int dias : diasProyeccion) {
                    PrediccionService.PrediccionInventario prediccion = 
                        prediccionService.predecirInventario(producto.getProductoId(), dias);
                    
                    int stockProyectado = prediccion.getStockProyectado();
                    int stockMinimo = producto.getStock_minimo();
                    
                    // Si el stock proyectado está por debajo del mínimo, generar alerta
                    if (stockProyectado < stockMinimo) {
                        ArbolDecision.Alerta alerta = new ArbolDecision.Alerta();
                        alerta.setProductoId(producto.getProductoId());
                        alerta.setProductoNombre(producto.getNombre());
                        alerta.setNivel("CRITICO");
                        alerta.setMensaje(String.format(
                            "Stock proyectado para %d días: %d unidades (mínimo: %d). " +
                            "Predicción de demanda: %s",
                            dias, stockProyectado, stockMinimo, prediccion.getPrediccionDemanda()
                        ));
                        alerta.setAccionRecomendada("Reabastecer urgentemente. " + prediccion.getRiesgo());
                        
                        // Evitar alertas duplicadas muy seguidas
                        if (!esAlertaReciente(producto.getProductoId())) {
                            alertas.add(alerta);
                            ultimasAlertas.put(producto.getProductoId(), new Date());
                        }
                        break; // Solo una alerta por producto
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al detectar stock crítico para producto " + producto.getProductoId() + ": " + e.getMessage());
            }
        }
        
        return alertas;
    }
    
    /**
     * RF4.6.2 - Detecta patrones de venta inusuales
     */
    private List<ArbolDecision.Alerta> detectarPatronesVentaInusuales(
            List<Productos> productos, List<Movimientos> movimientos) {
        List<ArbolDecision.Alerta> alertas = new ArrayList<>();
        
        for (Productos producto : productos) {
            // Filtrar movimientos de salida del producto
            List<Movimientos> salidasProducto = new ArrayList<>();
            for (Movimientos mov : movimientos) {
                if (mov.getProductoId().equals(producto.getProductoId()) && 
                    "salida".equalsIgnoreCase(mov.getTipo_movimiento())) {
                    salidasProducto.add(mov);
                }
            }
            
            if (salidasProducto.size() < 5) {
                continue; // Necesita al menos 5 movimientos para detectar patrones
            }
            
            // Calcular promedio de ventas en los últimos 30 días
            Date hace30Dias = calcularFechaPasada(30);
            int ventasUltimos30Dias = 0;
            int ventas30DiasAnteriores = 0;
            
            for (Movimientos mov : salidasProducto) {
                if (mov.getFecha().after(hace30Dias)) {
                    ventasUltimos30Dias += mov.getCantidad();
                } else {
                    Date hace60Dias = calcularFechaPasada(60);
                    if (mov.getFecha().after(hace60Dias)) {
                        ventas30DiasAnteriores += mov.getCantidad();
                    }
                }
            }
            
            // Detectar cambios significativos (más del 50% de aumento o disminución)
            if (ventas30DiasAnteriores > 0) {
                double cambioPorcentual = ((double) ventasUltimos30Dias - ventas30DiasAnteriores) / ventas30DiasAnteriores;
                
                if (Math.abs(cambioPorcentual) > 0.5) { // Cambio mayor al 50%
                    ArbolDecision.Alerta alerta = new ArbolDecision.Alerta();
                    alerta.setProductoId(producto.getProductoId());
                    alerta.setProductoNombre(producto.getNombre());
                    
                    if (cambioPorcentual > 0.5) {
                        alerta.setNivel("MEDIO");
                        alerta.setMensaje(String.format(
                            "Aumento inusual de ventas detectado: +%.1f%% en los últimos 30 días. " +
                            "Ventas recientes: %d vs anteriores: %d",
                            cambioPorcentual * 100, ventasUltimos30Dias, ventas30DiasAnteriores
                        ));
                        alerta.setAccionRecomendada("Aumentar stock para satisfacer demanda creciente");
                    } else {
                        alerta.setNivel("BAJO");
                        alerta.setMensaje(String.format(
                            "Disminución inusual de ventas detectada: %.1f%% en los últimos 30 días. " +
                            "Ventas recientes: %d vs anteriores: %d",
                            cambioPorcentual * 100, ventasUltimos30Dias, ventas30DiasAnteriores
                        ));
                        alerta.setAccionRecomendada("Revisar estrategia de marketing o considerar reducir compras");
                    }
                    
                    if (!esAlertaReciente(producto.getProductoId())) {
                        alertas.add(alerta);
                        ultimasAlertas.put(producto.getProductoId(), new Date());
                    }
                }
            }
        }
        
        return alertas;
    }
    
    /**
     * RF4.6.3 - Detecta cuando un proveedor crítico tenga demoras
     */
    private List<ArbolDecision.Alerta> detectarProveedorCriticoConDemoras() 
            throws ExecutionException, InterruptedException {
        List<ArbolDecision.Alerta> alertas = new ArrayList<>();
        
        try {
            // RF4.5 - Obtener proveedor crítico (mayor grado en el grafo)
            String proveedorCriticoId = grafoService.detectarProveedorCritico();
            
            if (proveedorCriticoId == null) {
                return alertas; // No hay proveedor crítico
            }
            
            // Obtener productos del proveedor crítico
            List<String> productosIds = grafoService.obtenerProductosDeProveedor(proveedorCriticoId);
            
            if (productosIds == null || productosIds.isEmpty()) {
                return alertas;
            }
            
            Proveedores proveedor = proveedorRepository.findById(proveedorCriticoId);
            if (proveedor == null) {
                return alertas;
            }
            
            // Analizar movimientos de entrada (pedidos) para detectar demoras
            // Una "demora" se detecta si hay más de 30 días sin recibir productos de este proveedor
            Date hace30Dias = calcularFechaPasada(30);
            boolean tieneDemoras = true;
            
            for (String productoId : productosIds) {
                List<Movimientos> entradas = movimientoRepository.findByProductoId(productoId);
                
                // Verificar si hay entradas recientes
                for (Movimientos entrada : entradas) {
                    if ("entrada".equalsIgnoreCase(entrada.getTipo_movimiento()) && 
                        entrada.getFecha().after(hace30Dias)) {
                        tieneDemoras = false;
                        break;
                    }
                }
                
                if (!tieneDemoras) {
                    break;
                }
            }
            
            // Si hay demoras y el proveedor es crítico, generar alerta
            if (tieneDemoras) {
                ArbolDecision.Alerta alerta = new ArbolDecision.Alerta();
                alerta.setProductoId("PROVEEDOR-" + proveedorCriticoId);
                alerta.setProductoNombre("Proveedor: " + proveedor.getNombre());
                alerta.setNivel("CRITICO");
                alerta.setMensaje(String.format(
                    "Proveedor crítico '%s' no ha entregado productos en los últimos 30 días. " +
                    "Afecta a %d producto(s).",
                    proveedor.getNombre(), productosIds.size()
                ));
                alerta.setAccionRecomendada("Contactar proveedor inmediatamente y buscar alternativas");
                
                // Contar alertas para este proveedor
                contadorAlertasProveedor.put(proveedorCriticoId, 
                    contadorAlertasProveedor.getOrDefault(proveedorCriticoId, 0) + 1);
                
                alertas.add(alerta);
            }
            
        } catch (Exception e) {
            System.err.println("Error al detectar demoras de proveedor crítico: " + e.getMessage());
        }
        
        return alertas;
    }
    
    /**
     * Verifica si ya se emitió una alerta reciente para evitar spam
     */
    private boolean esAlertaReciente(String productoId) {
        Date ultimaAlerta = ultimasAlertas.get(productoId);
        if (ultimaAlerta == null) {
            return false;
        }
        
        // No emitir alerta si ya se emitió una en las últimas 24 horas
        long diferenciaHoras = (new Date().getTime() - ultimaAlerta.getTime()) / (1000 * 60 * 60);
        return diferenciaHoras < 24;
    }
    
    /**
     * Calcula una fecha pasada
     */
    private Date calcularFechaPasada(int dias) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -dias);
        return cal.getTime();
    }
    
    /**
     * Obtiene alertas para un producto específico
     */
    public List<ArbolDecision.Alerta> obtenerAlertasProducto(String productoId) 
            throws ExecutionException, InterruptedException {
        List<ArbolDecision.Alerta> alertas = new ArrayList<>();
        
        Productos producto = productoRepository.findById(productoId);
        if (producto == null) {
            return alertas;
        }
        
        // Alerta del árbol de decisión
        List<Movimientos> movimientos = movimientoRepository.findByProductoId(productoId);
        List<Productos> productos = productoRepository.findAll();
        arbolDecision.entrenar(movimientos, productos);
        
        ArbolDecision.Alerta alertaArbol = arbolDecision.generarAlerta(productoId, producto);
        if (alertaArbol != null && !"BAJO".equals(alertaArbol.getNivel())) {
            alertas.add(alertaArbol);
        }
        
        // Alerta de stock proyectado crítico
        try {
            PrediccionService.PrediccionInventario prediccion = 
                prediccionService.predecirInventario(productoId, 15);
            
            if (prediccion.getStockProyectado() < producto.getStock_minimo()) {
                ArbolDecision.Alerta alertaStock = new ArbolDecision.Alerta();
                alertaStock.setProductoId(productoId);
                alertaStock.setProductoNombre(producto.getNombre());
                alertaStock.setNivel("ALTO");
                alertaStock.setMensaje("Stock proyectado crítico en 15 días: " + prediccion.getStockProyectado());
                alertaStock.setAccionRecomendada("Planificar reabastecimiento");
                alertas.add(alertaStock);
            }
        } catch (Exception e) {
            // Ignorar errores de predicción
        }
        
        return alertas;
    }
}
