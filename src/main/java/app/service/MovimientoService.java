
package app.service;
import app.config.FirebaseConfig;
import app.model.Lotes;
import app.model.Movimientos;
import app.model.Productos;
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
    private final String COLLECTION_MOVIMIENTOS = "Movimientos";
    private final String COLLECTION_PRODUCTOS = "Productos";
    private final String COLLECTION_LOTES = "Lotes";
    
    public MovimientoService() {
        this.db = FirestoreClient.getFirestore();
    }
    
    //registrar movimientos 
    public Movimientos registrarMovimiento(Movimientos movimiento, String loteId) throws ExecutionException, InterruptedException {
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
            procesarEntrada(movimiento, producto, loteId);
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
        
        System.out.println("Movimiento registrado: " + movimiento.getMovimientoId());
        return movimiento;
    }
    
    private void procesarEntrada(Movimientos movimiento, Productos producto, String loteId) throws ExecutionException, InterruptedException {
        //validar que stock no sea maximo
        int nuevoStock = producto.getStock_actual() + movimiento.getCantidad();
        if (nuevoStock > producto.getStock_maximo()) {
            throw new IllegalArgumentException("La entrada excede el stock máximo (" + producto.getStock_maximo() + ")");
        }
        
        //si tiene metodo de rotacion crear o actualizar lote
        if (tieneMetodoRotacion(producto.getMetodo_rotacion())) {
            if (loteId == null || loteId.trim().isEmpty()) {
                loteId = generarLoteId(producto.getProductoId());
            }
            
            //crea un nuevo lote con una cantidad de entrada
            Lotes nuevoLote = new Lotes();
            nuevoLote.setLoteId(loteId);
            nuevoLote.setCantidad(movimiento.getCantidad());
            nuevoLote.setFecha_Entrada(movimiento.getFecha());
            
            //calculo de fecha de vencimiento
            Calendar cal = Calendar.getInstance();
            cal.setTime(movimiento.getFecha());
            cal.add(Calendar.DAY_OF_MONTH, 30);
            nuevoLote.setFecha_Vencimiento(cal.getTime());
            
            //guardar lote
            db.collection(COLLECTION_PRODUCTOS)
                    .document(producto.getProductoId())
                    .collection(COLLECTION_LOTES)
                    .document(loteId)
                    .set(nuevoLote)
                    .get();
        }
        
        //actualizar stock del producto
        actualizarStockProducto(producto.getProductoId(), nuevoStock);
        
        System.out.println("Entrada procesada stock actual: " + nuevoStock);
    }
    
    
    private void procesarSalida(Movimientos movimiento, Productos producto) throws ExecutionException, InterruptedException {
        //validar que hay stock suficiente
        if (producto.getStock_actual() < movimiento.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + producto.getStock_actual());
        }
        
        //si tiene metodo de rotacion aplicar uno fifo lifo etc
        if (tieneMetodoRotacion(producto.getMetodo_rotacion())) {
            aplicarAlgoritmoRotacion(producto, movimiento.getCantidad());
        }
        
        // actualizar stock del producto
        int nuevoStock = producto.getStock_actual() - movimiento.getCantidad();
        actualizarStockProducto(producto.getProductoId(), nuevoStock);
        
        System.out.println("Salida procesada. Nuevo stock: " + nuevoStock);
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
                //lote con stock suficiente
                lote.setCantidad(lote.getCantidad() - cantidadRestante);
                actualizarLote(producto.getProductoId(), lote);
                cantidadRestante = 0;
            } else {
                //agotar con el lote y seguir con el siguiente
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
            lotes.add(doc.toObject(Lotes.class));
        }
        
        //ordenar los algoritmos
        String algoritmo = producto.getMetodo_rotacion();
        
        if (algoritmo.equalsIgnoreCase("FIFO")) {
            //FIFO
            lotes.sort(Comparator.comparing(Lotes::getFecha_Entrada));
        } else if (algoritmo.equalsIgnoreCase("LIFO")) {
            //LIFO
            lotes.sort(Comparator.comparing(Lotes::getFecha_Entrada).reversed());
        } else if (algoritmo.equalsIgnoreCase("DRIFO")) {
            //DRIFO
            lotes.sort(Comparator.comparing(Lotes::getFecha_Vencimiento));
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
   
}
