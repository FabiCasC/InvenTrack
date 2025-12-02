package app.service;

import app.model.Lotes;
import app.model.Movimientos;
import app.model.Productos;
import app.structures.ColaFIFO;
import app.structures.PilaLIFO;
import app.structures.ListaEnlazadaMovimientos;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Gestor de estructuras de datos para algoritmos de inventario
 * Mantiene en memoria las estructuras FIFO, LIFO y lista enlazada de movimientos
 * RF4.1, RF4.2, RF4.3
 */
public class AlgoritmoManager {
    
    private static AlgoritmoManager instancia;
    
    // RF4.1 - Colas FIFO por producto (para productos perecibles)
    private Map<String, ColaFIFO> colasFIFO;
    
    // RF4.2 - Pilas LIFO por producto (para productos no perecibles)
    private Map<String, PilaLIFO> pilasLIFO;
    
    // RF4.3 - Lista enlazada global de movimientos
    private ListaEnlazadaMovimientos listaMovimientos;
    
    private final Firestore db;
    private final String COLLECTION_PRODUCTOS = "productos";
    private final String COLLECTION_LOTES = "Lotes";
    private final String COLLECTION_MOVIMIENTOS = "movimientos";
    
    private AlgoritmoManager() {
        this.colasFIFO = new HashMap<>();
        this.pilasLIFO = new HashMap<>();
        this.listaMovimientos = new ListaEnlazadaMovimientos();
        this.db = FirestoreClient.getFirestore();
    }
    
    /**
     * Singleton pattern - Obtener instancia única
     */
    public static synchronized AlgoritmoManager getInstance() {
        if (instancia == null) {
            instancia = new AlgoritmoManager();
        }
        return instancia;
    }
    
    /**
     * RF4.1 - Obtener o crear la cola FIFO para un producto perecible
     */
    public ColaFIFO obtenerColaFIFO(String productoId) {
        if (!colasFIFO.containsKey(productoId)) {
            colasFIFO.put(productoId, new ColaFIFO());
        }
        return colasFIFO.get(productoId);
    }
    
    /**
     * RF4.2 - Obtener o crear la pila LIFO para un producto no perecible
     */
    public PilaLIFO obtenerPilaLIFO(String productoId) {
        if (!pilasLIFO.containsKey(productoId)) {
            pilasLIFO.put(productoId, new PilaLIFO());
        }
        return pilasLIFO.get(productoId);
    }
    
    /**
     * RF4.3 - Agregar movimiento a la lista enlazada
     */
    public void agregarMovimientoALista(Movimientos movimiento) {
        listaMovimientos.agregar(movimiento);
    }
    
    /**
     * RF4.3 - Obtener la lista enlazada de movimientos
     */
    public ListaEnlazadaMovimientos obtenerListaMovimientos() {
        return listaMovimientos;
    }
    
    /**
     * RF4.1 - Cargar lotes de un producto perecible en la cola FIFO desde Firebase
     */
    public void cargarLotesEnColaFIFO(String productoId) throws ExecutionException, InterruptedException {
        ColaFIFO cola = obtenerColaFIFO(productoId);
        cola.limpiar(); // Limpiar antes de recargar
        
        QuerySnapshot snapshot = db.collection(COLLECTION_PRODUCTOS)
                .document(productoId)
                .collection(COLLECTION_LOTES)
                .get()
                .get();
        
        // Cargar lotes ordenados por fecha de entrada (más antiguos primero)
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Lotes lote = doc.toObject(Lotes.class);
            if (lote != null) {
                cola.encolar(lote);
            }
        }
    }
    
    /**
     * RF4.2 - Cargar lotes de un producto no perecible en la pila LIFO desde Firebase
     */
    public void cargarLotesEnPilaLIFO(String productoId) throws ExecutionException, InterruptedException {
        PilaLIFO pila = obtenerPilaLIFO(productoId);
        pila.limpiar(); // Limpiar antes de recargar
        
        QuerySnapshot snapshot = db.collection(COLLECTION_PRODUCTOS)
                .document(productoId)
                .collection(COLLECTION_LOTES)
                .get()
                .get();
        
        // Cargar lotes en orden inverso (más recientes primero para la pila)
        java.util.List<Lotes> lotes = new java.util.ArrayList<>();
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Lotes lote = doc.toObject(Lotes.class);
            if (lote != null) {
                lotes.add(lote);
            }
        }
        
        // Ordenar por fecha de entrada descendente (más recientes primero)
        lotes.sort((l1, l2) -> {
            if (l1.getFecha_Entrada() == null) return 1;
            if (l2.getFecha_Entrada() == null) return -1;
            return l2.getFecha_Entrada().compareTo(l1.getFecha_Entrada());
        });
        
        // Cargar en la pila (el más reciente queda en el tope)
        for (Lotes lote : lotes) {
            pila.push(lote);
        }
    }
    
    /**
     * Cargar todos los movimientos en la lista enlazada desde Firebase
     */
    public void cargarMovimientosEnLista() throws ExecutionException, InterruptedException {
        listaMovimientos.limpiar();
        
        QuerySnapshot snapshot = db.collection(COLLECTION_MOVIMIENTOS)
                .orderBy("fecha")
                .get()
                .get();
        
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Movimientos movimiento = doc.toObject(Movimientos.class);
            if (movimiento != null) {
                movimiento.setMovimientoId(doc.getId());
                listaMovimientos.agregar(movimiento);
            }
        }
    }
    
    /**
     * Inicializar estructuras para un producto según su método de rotación
     */
    public void inicializarEstructura(Productos producto) throws ExecutionException, InterruptedException {
        String metodo = producto.getMetodo_rotacion();
        
        if (metodo != null && metodo.equalsIgnoreCase("FIFO")) {
            // RF4.1 - Producto perecible: usar cola FIFO
            cargarLotesEnColaFIFO(producto.getProductoId());
        } else if (metodo != null && metodo.equalsIgnoreCase("LIFO")) {
            // RF4.2 - Producto no perecible: usar pila LIFO
            cargarLotesEnPilaLIFO(producto.getProductoId());
        }
    }
    
    /**
     * Limpiar estructuras de un producto
     */
    public void limpiarEstructuras(String productoId) {
        colasFIFO.remove(productoId);
        pilasLIFO.remove(productoId);
    }
}
