
package app.service;
import app.config.FirebaseConfig;
import app.model.Lotes;
import app.model.Movimientos;
import app.model.Productos;
import app.structures.ColaFIFO;
import app.structures.PilaLIFO;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MovimientoService {
    private final Firestore db;
    private final String COLLECTION_MOVIMIENTOS = "movimientos";
    private final String COLLECTION_PRODUCTOS = "productos";
    private final String COLLECTION_LOTES = "Lotes";
    private final AlgoritmoManager algoritmoManager;
    
    public MovimientoService() {
        this.db = FirestoreClient.getFirestore();
        this.algoritmoManager = AlgoritmoManager.getInstance();
    }
    
    //registrar movimientos 
    public Movimientos registrarMovimiento(Movimientos movimiento, String loteId, Date fechaVencimiento) throws ExecutionException, InterruptedException {
        validarMovimiento(movimiento);
        
        //validar que el producto existe y obtener datos
        Productos producto = obtenerProducto(movimiento.getProductoId());
        
        //generar id 
        if (movimiento.getMovimientoId() == null || movimiento.getMovimientoId().trim().isEmpty()) {
            movimiento.setMovimientoId(generarMovimientoId());
        }
        
        //asignar fecha actual si no tiene
        if (movimiento.getFecha() == null) {
            movimiento.setFecha(new Date());
        }
        
        //comparar metodo de rotacion y algoritmo 
        if (movimiento.getAlgoritmo() != null && !movimiento.getAlgoritmo().trim().isEmpty()) {
            // Si viene con algoritmo, verificar que coincida con el del producto
            if (!movimiento.getAlgoritmo().equalsIgnoreCase(producto.getMetodo_rotacion())) {
                throw new IllegalArgumentException(
                    " El algoritmo del movimiento '" + movimiento.getAlgoritmo() + 
                    "' no coincide con el método de rotación del producto '" + producto.getMetodo_rotacion() + "'. " +
                    "El producto " + producto.getProductoId() + " usa " + producto.getMetodo_rotacion()
                );
            }
            System.out.println("Algoritmo validado: " + movimiento.getAlgoritmo() + " coincide con el producto");
        } else {
            //si algoritmo esta en null asignar el del producto
            movimiento.setAlgoritmo(producto.getMetodo_rotacion());
            System.out.println("Algoritmo asignado automáticamente: " + producto.getMetodo_rotacion());
        }
        
        //procesar segun el movimiento
        if (movimiento.getTipo_movimiento().equalsIgnoreCase("entrada")) {
            procesarEntrada(movimiento, producto, loteId, fechaVencimiento);
        } else if (movimiento.getTipo_movimiento().equalsIgnoreCase("salida")) {
            procesarSalida(movimiento, producto);
        } else {
            throw new IllegalArgumentException("Tipo de movimiento inválido. Use 'entrada' o 'salida'");
        }
        
        //guardar en la base de datos
        db.collection(COLLECTION_MOVIMIENTOS)
                .document(movimiento.getMovimientoId())
                .set(movimiento)
                .get();
        
        // RF4.3 - Agregar movimiento a la lista enlazada
        algoritmoManager.agregarMovimientoALista(movimiento);
        
        // RF4.1 y RF4.2 - Inicializar estructuras para el producto si es necesario
        algoritmoManager.inicializarEstructura(producto);
        
        System.out.println("Movimiento registrado: " + movimiento.getMovimientoId());
        return movimiento;
    }
    
    private void procesarEntrada(Movimientos movimiento, Productos producto, String loteId, Date fechaVencimiento) throws ExecutionException, InterruptedException {
        //validar que stock no sea maximo
        int stockActual = calcularStockDesdeLotes(producto.getProductoId());
        int nuevoStock = stockActual + movimiento.getCantidad();
        
        if (nuevoStock > producto.getStock_maximo()) {
            throw new IllegalArgumentException("La entrada excede el stock máximo (" + producto.getStock_maximo() + ")");
        }
        
        // OBLIGATORIO: Siempre crear un lote para cada entrada
            if (loteId == null || loteId.trim().isEmpty()) {
                loteId = generarLoteId(producto.getProductoId());
            }
            
        // Crear un nuevo lote con la cantidad de entrada
            Lotes nuevoLote = new Lotes();
            nuevoLote.setLoteId(loteId);
            nuevoLote.setCantidad(movimiento.getCantidad());
            nuevoLote.setFecha_Entrada(movimiento.getFecha());
            
        // Calcular o usar fecha de vencimiento
        if (fechaVencimiento != null) {
            nuevoLote.setFecha_Vencimiento(fechaVencimiento);
        } else {
            // Si no se proporciona, calcular automáticamente según el tipo de producto
            Calendar cal = Calendar.getInstance();
            cal.setTime(movimiento.getFecha());
            
            // Perecibles: 30 días, No perecibles: 365 días
            if (producto.getTipo() != null && producto.getTipo().equalsIgnoreCase("Perecible")) {
            cal.add(Calendar.DAY_OF_MONTH, 30);
            } else {
                cal.add(Calendar.DAY_OF_YEAR, 365);
            }
            nuevoLote.setFecha_Vencimiento(cal.getTime());
        }
            
        // Guardar lote en Firebase
            db.collection(COLLECTION_PRODUCTOS)
                    .document(producto.getProductoId())
                    .collection(COLLECTION_LOTES)
                    .document(loteId)
                    .set(nuevoLote)
                    .get();
        
        // RF4.1 y RF4.2 - Agregar lote a la estructura correspondiente según el método de rotación
        String metodo = producto.getMetodo_rotacion();
        if (metodo != null && metodo.equalsIgnoreCase("FIFO")) {
            // RF4.1 - Producto perecible: encolar en la cola FIFO
            ColaFIFO cola = algoritmoManager.obtenerColaFIFO(producto.getProductoId());
            cola.encolar(nuevoLote);
            System.out.println("Lote agregado a Cola FIFO. Tamaño de cola: " + cola.getTamaño());
        } else if (metodo != null && metodo.equalsIgnoreCase("LIFO")) {
            // RF4.2 - Producto no perecible: push en la pila LIFO
            PilaLIFO pila = algoritmoManager.obtenerPilaLIFO(producto.getProductoId());
            pila.push(nuevoLote);
            System.out.println("Lote agregado a Pila LIFO. Tamaño de pila: " + pila.getTamaño());
        }
        
        // Actualizar stock del producto como suma de todos los lotes
        actualizarStockDesdeLotes(producto.getProductoId());
        
        System.out.println("Entrada procesada. Lote creado: " + loteId + ", Nuevo stock: " + calcularStockDesdeLotes(producto.getProductoId()));
    }
    
    
    private void procesarSalida(Movimientos movimiento, Productos producto) throws ExecutionException, InterruptedException {
        // Calcular stock actual desde los lotes
        int stockActual = calcularStockDesdeLotes(producto.getProductoId());
        
        //validar que hay stock suficiente
        if (stockActual < movimiento.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + stockActual);
        }
        
        // RF4.1 y RF4.2 - Usar estructuras de datos según el método de rotación
        String metodo = producto.getMetodo_rotacion();
        
        if (metodo != null && metodo.equalsIgnoreCase("FIFO")) {
            // RF4.1 - Producto perecible: usar cola FIFO
            procesarSalidaFIFO(producto, movimiento.getCantidad());
        } else if (metodo != null && metodo.equalsIgnoreCase("LIFO")) {
            // RF4.2 - Producto no perecible: usar pila LIFO
            procesarSalidaLIFO(producto, movimiento.getCantidad());
        } else {
            // DRIFO o sin método: usar algoritmo tradicional
            aplicarAlgoritmoRotacion(producto, movimiento.getCantidad());
        }
        
        // Actualizar stock del producto como suma de todos los lotes restantes
        actualizarStockDesdeLotes(producto.getProductoId());
        
        int nuevoStock = calcularStockDesdeLotes(producto.getProductoId());
        System.out.println("Salida procesada. Nuevo stock: " + nuevoStock);
    }
    
    /**
     * RF4.1 - Procesar salida usando Cola FIFO para productos perecibles
     * El primer producto ingresado es el primero en salir
     */
    private void procesarSalidaFIFO(Productos producto, int cantidadSalida) throws ExecutionException, InterruptedException {
        ColaFIFO cola = algoritmoManager.obtenerColaFIFO(producto.getProductoId());
        
        // Si la cola está vacía, recargar desde Firebase
        if (cola.estaVacia()) {
            algoritmoManager.cargarLotesEnColaFIFO(producto.getProductoId());
            cola = algoritmoManager.obtenerColaFIFO(producto.getProductoId());
        }
        
        int cantidadRestante = cantidadSalida;
        
        // RF4.1 - Desencolar lotes hasta cubrir la cantidad solicitada
        while (cantidadRestante > 0 && !cola.estaVacia()) {
            Lotes lote = cola.desencolar(); // El primer producto ingresado sale primero
            
            if (lote.getCantidad() >= cantidadRestante) {
                // Lote con stock suficiente - reducir cantidad
                lote.setCantidad(lote.getCantidad() - cantidadRestante);
                
                if (lote.getCantidad() > 0) {
                    // Actualizar lote si aún tiene stock
                    actualizarLote(producto.getProductoId(), lote);
                    // Volver a encolar el lote restante
                    cola.encolar(lote);
                } else {
                    // Eliminar lote si se agotó completamente
                    eliminarLote(producto.getProductoId(), lote.getLoteId());
                }
                cantidadRestante = 0;
            } else {
                // Agotar completamente el lote y seguir con el siguiente
                cantidadRestante -= lote.getCantidad();
                eliminarLote(producto.getProductoId(), lote.getLoteId());
            }
        }
        
        if (cantidadRestante > 0) {
            throw new IllegalStateException("No hay suficientes lotes para cubrir la salida");
        }
    }
    
    /**
     * RF4.2 - Procesar salida usando Pila LIFO para productos no perecibles
     * El último producto ingresado es el primero en salir
     */
    private void procesarSalidaLIFO(Productos producto, int cantidadSalida) throws ExecutionException, InterruptedException {
        PilaLIFO pila = algoritmoManager.obtenerPilaLIFO(producto.getProductoId());
        
        // Si la pila está vacía, recargar desde Firebase
        if (pila.estaVacia()) {
            algoritmoManager.cargarLotesEnPilaLIFO(producto.getProductoId());
            pila = algoritmoManager.obtenerPilaLIFO(producto.getProductoId());
        }
        
        int cantidadRestante = cantidadSalida;
        java.util.Stack<Lotes> lotesTemporales = new java.util.Stack<>();
        
        // RF4.2 - Desapilar lotes hasta cubrir la cantidad solicitada
        while (cantidadRestante > 0 && !pila.estaVacia()) {
            Lotes lote = pila.pop(); // El último producto ingresado sale primero
            
            if (lote.getCantidad() >= cantidadRestante) {
                // Lote con stock suficiente - reducir cantidad
                lote.setCantidad(lote.getCantidad() - cantidadRestante);
                
                if (lote.getCantidad() > 0) {
                    // Guardar para volver a apilar después
                    lotesTemporales.push(lote);
                } else {
                    // Eliminar lote si se agotó completamente
                    eliminarLote(producto.getProductoId(), lote.getLoteId());
                }
                cantidadRestante = 0;
            } else {
                // Agotar completamente el lote
                cantidadRestante -= lote.getCantidad();
                eliminarLote(producto.getProductoId(), lote.getLoteId());
            }
        }
        
        // Volver a apilar los lotes que quedaron con stock
        while (!lotesTemporales.isEmpty()) {
            pila.push(lotesTemporales.pop());
        }
        
        if (cantidadRestante > 0) {
            throw new IllegalStateException("No hay suficientes lotes para cubrir la salida");
        }
    }
    
    private void aplicarAlgoritmoRotacion(Productos producto, int cantidadSalida) throws ExecutionException, InterruptedException {
        List<Lotes> lotes = obtenerLotesOrdenados(producto);
        
        if (lotes.isEmpty()) {
            throw new IllegalStateException("No hay lotes disponibles para procesar la salida");
        }
        
        int cantidadRestante = cantidadSalida;
        
        for (Lotes lote : lotes) {
            if (cantidadRestante <= 0) break;
            
            if (lote.getCantidad() >= cantidadRestante) {
                // Lote con stock suficiente - reducir cantidad
                lote.setCantidad(lote.getCantidad() - cantidadRestante);
                
                if (lote.getCantidad() > 0) {
                    // Actualizar lote si aún tiene stock
                actualizarLote(producto.getProductoId(), lote);
                } else {
                    // Eliminar lote si se agotó completamente
                    eliminarLote(producto.getProductoId(), lote.getLoteId());
                }
                cantidadRestante = 0;
            } else {
                // Agotar completamente el lote y seguir con el siguiente
                cantidadRestante -= lote.getCantidad();
                eliminarLote(producto.getProductoId(), lote.getLoteId());
            }
        }
        
        if (cantidadRestante > 0) {
            throw new IllegalStateException("No hay suficientes lotes para cubrir la salida");
        }
    }
    
    private List<Lotes> obtenerLotesOrdenados(Productos producto) throws ExecutionException, InterruptedException {
        List<Lotes> lotes = new ArrayList<>();
        
        QuerySnapshot snapshot = db.collection(COLLECTION_PRODUCTOS)
                .document(producto.getProductoId())
                .collection(COLLECTION_LOTES)
                .get()
                .get();
        
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Lotes lote = doc.toObject(Lotes.class);
            if (lote != null) {
                lotes.add(lote);
            }
        }
        
        // Ordenar según algoritmo de rotación
        String algoritmo = producto.getMetodo_rotacion();
        
        if (algoritmo != null) {
        if (algoritmo.equalsIgnoreCase("FIFO")) {
                // FIFO: Primero en entrar, primero en salir
            lotes.sort(Comparator.comparing(Lotes::getFecha_Entrada));
        } else if (algoritmo.equalsIgnoreCase("LIFO")) {
                // LIFO: Último en entrar, primero en salir
            lotes.sort(Comparator.comparing(Lotes::getFecha_Entrada).reversed());
        } else if (algoritmo.equalsIgnoreCase("DRIFO")) {
                // DRIFO: Ordenar por fecha de vencimiento (lo que vence primero sale primero)
                // Manejar nulls: lotes sin fecha de vencimiento van al final
                lotes.sort(Comparator.comparing(
                    Lotes::getFecha_Vencimiento, 
                    Comparator.nullsLast(Comparator.naturalOrder())
                ));
            } else {
                // Si no hay algoritmo válido, usar FIFO por defecto
                lotes.sort(Comparator.comparing(Lotes::getFecha_Entrada));
            }
        } else {
            // Si no hay algoritmo, usar FIFO por defecto
            lotes.sort(Comparator.comparing(Lotes::getFecha_Entrada));
        }
        
        return lotes;
    }
    
    // metodos de apoyo
    
    private void validarMovimiento(Movimientos movimiento) {
        if (movimiento.getProductoId() == null || movimiento.getProductoId().trim().isEmpty()) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }
        
        if (movimiento.getTipo_movimiento() == null || movimiento.getTipo_movimiento().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio");
        }
        
        if (movimiento.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
    }
    
    private Productos obtenerProducto(String productoId) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db.collection(COLLECTION_PRODUCTOS)
                .document(productoId)
                .get()
                .get();
        
        if (!doc.exists()) {
            throw new IllegalArgumentException("El producto no existe: " + productoId);
        }
        
        return doc.toObject(Productos.class);
    }
    
    private void actualizarStockProducto(String productoId, int nuevoStock) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION_PRODUCTOS)
                .document(productoId)
                .update("stock_actual", nuevoStock)
                .get();
    }
    
    /**
     * Calcula el stock actual sumando todos los lotes del producto
     * @param productoId ID del producto
     * @return Suma de todas las cantidades de los lotes
     */
    private int calcularStockDesdeLotes(String productoId) throws ExecutionException, InterruptedException {
        QuerySnapshot snapshot = db.collection(COLLECTION_PRODUCTOS)
                .document(productoId)
                .collection(COLLECTION_LOTES)
                .get()
                .get();
        
        int stockTotal = 0;
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Lotes lote = doc.toObject(Lotes.class);
            if (lote != null) {
                stockTotal += lote.getCantidad();
            }
        }
        
        return stockTotal;
    }
    
    /**
     * Actualiza el stock del producto calculándolo desde la suma de todos los lotes
     * Esto garantiza que stock_actual = suma de todos los lotes
     */
    private void actualizarStockDesdeLotes(String productoId) throws ExecutionException, InterruptedException {
        int nuevoStock = calcularStockDesdeLotes(productoId);
        actualizarStockProducto(productoId, nuevoStock);
    }
    
    private void actualizarLote(String productoId, Lotes lote) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION_PRODUCTOS)
                .document(productoId)
                .collection(COLLECTION_LOTES)
                .document(lote.getLoteId())
                .set(lote)
                .get();
    }
    
    private void eliminarLote(String productoId, String loteId) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION_PRODUCTOS)
                .document(productoId)
                .collection(COLLECTION_LOTES)
                .document(loteId)
                .delete()
                .get();
    }
    
    private boolean tieneMetodoRotacion(String metodoRotacion) {
        return metodoRotacion != null && 
               (metodoRotacion.equalsIgnoreCase("FIFO") || 
                metodoRotacion.equalsIgnoreCase("LIFO") || 
                metodoRotacion.equalsIgnoreCase("DRIFO"));
    }
    
    private String generarMovimientoId() throws ExecutionException, InterruptedException {
        int total = (int) db.collection(COLLECTION_MOVIMIENTOS).get().get().size();
        String nuevoId;
        
        do {
            total++;
            nuevoId = String.format("MOV%03d", total);
        } while (existeMovimiento(nuevoId));
        
        return nuevoId;
    }
    
    private String generarLoteId(String productoId) throws ExecutionException, InterruptedException {
        int total = (int) db.collection(COLLECTION_PRODUCTOS)
                .document(productoId)
                .collection(COLLECTION_LOTES)
                .get()
                .get()
                .size();
        
        return String.format("LOT-%s-%03d", productoId, total + 1);
    }
    
    private boolean existeMovimiento(String movimientoId) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db.collection(COLLECTION_MOVIMIENTOS)
                .document(movimientoId)
                .get()
                .get();
        return doc.exists();
    }
    
    /**
     * Obtiene un movimiento por su ID
     */
    public Movimientos obtenerMovimiento(String movimientoId) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db.collection(COLLECTION_MOVIMIENTOS)
                .document(movimientoId)
                .get()
                .get();
        
        if (!doc.exists()) {
            throw new IllegalArgumentException("El movimiento no existe: " + movimientoId);
        }
        
        Movimientos movimiento = doc.toObject(Movimientos.class);
        if (movimiento != null) {
            movimiento.setMovimientoId(doc.getId());
        }
        return movimiento;
    }
    
    /**
     * Lista todos los movimientos
     */
    public List<Movimientos> listarMovimientos() throws ExecutionException, InterruptedException {
        List<Movimientos> movimientos = new ArrayList<>();
        QuerySnapshot snapshot = db.collection(COLLECTION_MOVIMIENTOS).get().get();
        
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Movimientos movimiento = doc.toObject(Movimientos.class);
            if (movimiento != null) {
                movimiento.setMovimientoId(doc.getId());
                movimientos.add(movimiento);
            }
        }
        
        return movimientos;
    }
    
    /**
     * Lista movimientos por producto
     */
    public List<Movimientos> listarMovimientosPorProducto(String productoId) throws ExecutionException, InterruptedException {
        List<Movimientos> movimientos = new ArrayList<>();
        QuerySnapshot snapshot = db.collection(COLLECTION_MOVIMIENTOS)
                .whereEqualTo("productoId", productoId)
                .get()
                .get();
        
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Movimientos movimiento = doc.toObject(Movimientos.class);
            if (movimiento != null) {
                movimiento.setMovimientoId(doc.getId());
                movimientos.add(movimiento);
            }
        }
        
        return movimientos;
    }
    
    /**
     * Lista movimientos por rango de fechas
     */
    public List<Movimientos> listarMovimientosPorFechas(Date fechaInicio, Date fechaFin) throws ExecutionException, InterruptedException {
        List<Movimientos> movimientos = new ArrayList<>();
        QuerySnapshot snapshot = db.collection(COLLECTION_MOVIMIENTOS)
                .whereGreaterThanOrEqualTo("fecha", fechaInicio)
                .whereLessThanOrEqualTo("fecha", fechaFin)
                .get()
                .get();
        
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Movimientos movimiento = doc.toObject(Movimientos.class);
            if (movimiento != null) {
                movimiento.setMovimientoId(doc.getId());
                movimientos.add(movimiento);
            }
        }
        
        return movimientos;
    }
    
    /**
     * Lista movimientos por tipo
     */
    public List<Movimientos> listarMovimientosPorTipo(String tipoMovimiento) throws ExecutionException, InterruptedException {
        List<Movimientos> movimientos = new ArrayList<>();
        QuerySnapshot snapshot = db.collection(COLLECTION_MOVIMIENTOS)
                .whereEqualTo("tipo_movimiento", tipoMovimiento)
                .get()
                .get();
        
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Movimientos movimiento = doc.toObject(Movimientos.class);
            if (movimiento != null) {
                movimiento.setMovimientoId(doc.getId());
                movimientos.add(movimiento);
            }
        }
        
        return movimientos;
    }
    
    /**
     * Elimina movimientos de productos que no existen
     * @return Número de movimientos eliminados
     */
    public int eliminarMovimientosProductosDesconocidos() throws ExecutionException, InterruptedException {
        List<Movimientos> todosMovimientos = listarMovimientos();
        List<String> idsParaEliminar = new ArrayList<>();
        
        for (Movimientos movimiento : todosMovimientos) {
            if (movimiento.getProductoId() != null && !movimiento.getProductoId().trim().isEmpty()) {
                // Verificar si el producto existe
                DocumentSnapshot docProducto = db.collection(COLLECTION_PRODUCTOS)
                        .document(movimiento.getProductoId())
                        .get()
                        .get();
                
                if (!docProducto.exists()) {
                    // El producto no existe, marcar movimiento para eliminar
                    idsParaEliminar.add(movimiento.getMovimientoId());
                }
            }
        }
        
        // Eliminar los movimientos de productos desconocidos
        int eliminados = 0;
        for (String movimientoId : idsParaEliminar) {
            try {
                db.collection(COLLECTION_MOVIMIENTOS)
                        .document(movimientoId)
                        .delete()
                        .get();
                eliminados++;
            } catch (Exception e) {
                System.err.println("Error al eliminar movimiento " + movimientoId + ": " + e.getMessage());
            }
        }
        
        return eliminados;
    }
   
}
