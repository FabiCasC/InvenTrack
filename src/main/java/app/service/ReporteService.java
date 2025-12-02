package app.service;

import app.model.Lotes;
import app.model.Movimientos;
import app.model.Productos;
import app.model.ReporteInventarioCritico;
import app.model.ReporteMovimientos;
import app.model.ReportePrediccion;
import app.controller.ProductoController;
import app.controller.ProveedorController;
import app.service.PrediccionService;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReporteService {
    private final Firestore db;
    private final String COLLECTION_PRODUCTOS = "productos";
    private final String COLLECTION_LOTES = "Lotes";
    private final String COLLECTION_MOVIMIENTOS = "movimientos";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final ProductoController productoController;
    private final ProveedorController proveedorController;
    private final PrediccionService prediccionService;

    public ReporteService() {
        this.db = FirestoreClient.getFirestore();
        this.productoController = new ProductoController();
        this.proveedorController = new ProveedorController();
        this.prediccionService = new PrediccionService();
    }

    /**
     * RF8.1 - Genera reporte de inventario crítico estructurado
     */
    public ReporteInventarioCritico generarReporteInventarioCritico() throws ExecutionException, InterruptedException {
        ReporteInventarioCritico reporte = new ReporteInventarioCritico();
        List<ReporteInventarioCritico.ItemInventarioCritico> items = new ArrayList<>();

        QuerySnapshot productosSnapshot = db.collection(COLLECTION_PRODUCTOS).get().get();
        Map<String, String> proveedoresMap = cargarProveedores();
        Date hoy = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(hoy);
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date dentro30Dias = cal.getTime();

        int productosStockBajo = 0;
        int productosPorVencer = 0;

        for (QueryDocumentSnapshot doc : productosSnapshot.getDocuments()) {
            Productos producto = doc.toObject(Productos.class);
            if (producto == null) continue;
            producto.setProductoId(doc.getId());

            boolean esCritico = false;
            String nivelCritico = "";
            String motivo = "";
            int diasParaVencimiento = Integer.MAX_VALUE;
            
            // Verificar stock bajo
            if (producto.getStock_actual() <= producto.getStock_minimo()) {
                esCritico = true;
                productosStockBajo++;
                nivelCritico = producto.getStock_actual() <= (producto.getStock_minimo() * 0.5) ? "CRÍTICO" : "ALERTA";
                motivo = "Stock bajo";
            }
            
            // Verificar lotes próximos a vencer
            try {
                QuerySnapshot lotesSnapshot = db.collection(COLLECTION_PRODUCTOS)
                        .document(producto.getProductoId())
                        .collection(COLLECTION_LOTES)
                        .get()
                        .get();

                for (QueryDocumentSnapshot loteDoc : lotesSnapshot.getDocuments()) {
                    Lotes lote = loteDoc.toObject(Lotes.class);
                    if (lote != null && lote.getFecha_Vencimiento() != null) {
                    Date fechaVenc = lote.getFecha_Vencimiento();
                        if (fechaVenc.after(hoy) && fechaVenc.before(dentro30Dias)) {
                        long diasRestantes = (fechaVenc.getTime() - hoy.getTime()) / (1000 * 60 * 60 * 24);
                            if (diasRestantes < diasParaVencimiento) {
                                diasParaVencimiento = (int) diasRestantes;
                            }
                            
                            if (!esCritico || diasRestantes <= 7) {
                                esCritico = true;
                                productosPorVencer++;
                                if (diasRestantes <= 7) {
                                    nivelCritico = "CRÍTICO";
                                } else if (nivelCritico.isEmpty()) {
                                    nivelCritico = "ALERTA";
                                }
                                
                                if (motivo.isEmpty()) {
                                    motivo = "Próximo a vencer";
                        } else {
                                    motivo = "Ambos";
                        }
                    }
                }
            }
        }
            } catch (Exception e) {
                // Ignorar errores al obtener lotes
            }
            
            if (esCritico) {
                ReporteInventarioCritico.ItemInventarioCritico item = new ReporteInventarioCritico.ItemInventarioCritico();
                item.setProductoId(producto.getProductoId());
                item.setNombre(producto.getNombre() != null ? producto.getNombre() : "Sin nombre");
                item.setTipo(producto.getTipo() != null ? producto.getTipo() : "N/A");
                item.setStockActual(producto.getStock_actual());
                item.setStockMinimo(producto.getStock_minimo());
                item.setStockMaximo(producto.getStock_maximo());
                item.setProveedor(proveedoresMap.getOrDefault(producto.getProveedorId(), "N/A"));
                item.setNivelCritico(nivelCritico.isEmpty() ? "ADVERTENCIA" : nivelCritico);
                item.setMotivo(motivo);
                item.setDiasParaVencimiento(diasParaVencimiento == Integer.MAX_VALUE ? 0 : diasParaVencimiento);
                items.add(item);
            }
        }

        reporte.setProductosCriticos(items);
        reporte.setTotalProductos((int) productosSnapshot.size());
        reporte.setProductosStockBajo(productosStockBajo);
        reporte.setProductosPorVencer(productosPorVencer);
        
        return reporte;
    }
    
    /**
     * RF8.2 - Genera reporte de predicción estructurado
     */
    public ReportePrediccion generarReportePrediccion(int diasProyeccion) throws ExecutionException, InterruptedException {
        ReportePrediccion reporte = new ReportePrediccion();
        List<ReportePrediccion.PrediccionProducto> predicciones = new ArrayList<>();
        
        List<Productos> productos = productoController.listarProductos();
        
        if (productos != null && !productos.isEmpty()) {
            for (Productos producto : productos) {
                try {
                    PrediccionService.PrediccionInventario pred = prediccionService.predecirInventario(
                        producto.getProductoId(), diasProyeccion
                    );
                    
                    ReportePrediccion.PrediccionProducto predItem = new ReportePrediccion.PrediccionProducto();
                    predItem.setProductoId(producto.getProductoId());
                    predItem.setNombre(producto.getNombre());
                    predItem.setStockActual(producto.getStock_actual());
                    predItem.setStockProyectado(pred.getStockProyectado());
                    predItem.setPrediccion(pred.getPrediccionDemanda());
                    
                    // Determinar riesgo
                    String riesgoStr = pred.getRiesgo();
                    if (riesgoStr != null && riesgoStr.contains("ALTO")) {
                        predItem.setRiesgo("ALTO");
                    } else if (riesgoStr != null && riesgoStr.contains("MEDIO")) {
                        predItem.setRiesgo("MEDIO");
        } else {
                        predItem.setRiesgo("BAJO");
                    }
                    
                    predItem.setRazon(riesgoStr);
                    predItem.setConfianza(0.75); // Valor fijo por ahora
                    
                    predicciones.add(predItem);
                } catch (Exception e) {
                    // Ignorar productos que no se pueden predecir
            }
        }
        }
        
        reporte.setFechaGeneracion(dateFormat.format(new Date()));
        reporte.setPredicciones(predicciones);
        reporte.setTotalProductosAnalizados(predicciones.size());
        
        return reporte;
    }

    /**
     * RF8.3 - Genera reporte de movimientos estructurado
     */
    public ReporteMovimientos generarReporteMovimientos(Date fechaInicio, Date fechaFin, String tipoFiltro) 
            throws ExecutionException, InterruptedException {
        ReporteMovimientos reporte = new ReporteMovimientos();
        List<ReporteMovimientos.ItemMovimiento> items = new ArrayList<>();
        ReporteMovimientos.ResumenMovimientos resumen = new ReporteMovimientos.ResumenMovimientos();
        
        Map<String, String> productosMap = cargarProductos();

        QuerySnapshot movimientosSnapshot = db.collection(COLLECTION_MOVIMIENTOS).get().get();

        int totalEntradas = 0;
        int totalSalidas = 0;
        int contadorEntradas = 0;
        int contadorSalidas = 0;

        for (QueryDocumentSnapshot doc : movimientosSnapshot.getDocuments()) {
            Movimientos mov = doc.toObject(Movimientos.class);
            if (mov == null) continue;
            mov.setMovimientoId(doc.getId());

            // Filtrar por fecha
            if (mov.getFecha() != null) {
                if (fechaInicio != null && mov.getFecha().before(fechaInicio)) continue;
                if (fechaFin != null && mov.getFecha().after(fechaFin)) continue;
            }
            
            // Filtrar por tipo
            if (tipoFiltro != null && !tipoFiltro.equals("Todos")) {
                if (!mov.getTipo_movimiento().equalsIgnoreCase(tipoFiltro)) continue;
            }
            
            ReporteMovimientos.ItemMovimiento item = new ReporteMovimientos.ItemMovimiento();
            item.setMovimientoId(mov.getMovimientoId());
            item.setProductoId(mov.getProductoId());
            item.setNombreProducto(productosMap.getOrDefault(mov.getProductoId(), "Producto desconocido"));
            item.setTipo(mov.getTipo_movimiento());
            item.setCantidad(mov.getCantidad());
            item.setFecha(mov.getFecha() != null ? dateFormat.format(mov.getFecha()) : "N/A");
            item.setAlgoritmo(mov.getAlgoritmo() != null ? mov.getAlgoritmo() : "N/A");
            item.setUsuarioId(mov.getUsuarioId() != null ? mov.getUsuarioId() : "N/A");
            
            items.add(item);

                if (mov.getTipo_movimiento().equalsIgnoreCase("entrada")) {
                    totalEntradas += mov.getCantidad();
                    contadorEntradas++;
                } else {
                    totalSalidas += mov.getCantidad();
                    contadorSalidas++;
                }
            }
        
        resumen.setTotalMovimientos(items.size());
        resumen.setTotalEntradas(contadorEntradas);
        resumen.setTotalSalidas(contadorSalidas);
        resumen.setCantidadTotalEntradas(totalEntradas);
        resumen.setCantidadTotalSalidas(totalSalidas);
        
        reporte.setFechaInicio(fechaInicio != null ? dateFormat.format(fechaInicio) : "N/A");
        reporte.setFechaFin(fechaFin != null ? dateFormat.format(fechaFin) : "N/A");
        reporte.setMovimientos(items);
        reporte.setResumen(resumen);
        
        return reporte;
    }
    
    private Map<String, String> cargarProveedores() {
        Map<String, String> map = new HashMap<>();
        try {
            List<app.model.Proveedores> proveedores = proveedorController.listarProveedores();
            if (proveedores != null) {
                for (app.model.Proveedores p : proveedores) {
                    map.put(p.getProveedorId(), p.getNombre());
                }
            }
        } catch (Exception e) {
            // Ignorar errores
        }
        return map;
    }
    
    private Map<String, String> cargarProductos() {
        Map<String, String> map = new HashMap<>();
        try {
            List<Productos> productos = productoController.listarProductos();
            if (productos != null) {
                for (Productos p : productos) {
                    map.put(p.getProductoId(), p.getNombre());
    }
            }
        } catch (Exception e) {
            // Ignorar errores
        }
        return map;
    }

    // Métodos de apoyo (mantener compatibilidad)
    private boolean tieneMetodoRotacion(String metodoRotacion) {
        return metodoRotacion != null
                && (metodoRotacion.equalsIgnoreCase("FIFO")
                || metodoRotacion.equalsIgnoreCase("LIFO")
                || metodoRotacion.equalsIgnoreCase("DRIFO"));
    }
}
