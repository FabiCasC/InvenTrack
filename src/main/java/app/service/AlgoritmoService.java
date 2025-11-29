package app.service;

import app.model.Lotes;
import app.model.Movimientos;
import app.model.Productos;
import app.model.estructuras.ColaFIFO;
import app.model.estructuras.PilaLIFO;
import app.repository.ProductoRepository;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Service para gestionar algoritmos de rotación (FIFO/LIFO/DRIFO) integrados con movimientos
 * RF4.1, RF4.2 - Integración de estructuras de datos con movimientos de inventario
 */
public class AlgoritmoService {
    private final Firestore db;
    private final ProductoRepository productoRepository;
    private static final String COLLECTION_PRODUCTOS = "Productos";
    private static final String COLLECTION_LOTES = "Lotes";

    public AlgoritmoService() {
        this.db = FirestoreClient.getFirestore();
        this.productoRepository = new ProductoRepository();
    }

    /**
     * Construye una Cola FIFO desde los lotes de un producto perecible
     * RF4.1 - Integración con movimientos
     * @param productoId ID del producto
     * @return Cola FIFO con lotes ordenados por fecha de entrada
     */
    public ColaFIFO construirColaFIFO(String productoId) throws ExecutionException, InterruptedException {
        Productos producto = productoRepository.findById(productoId);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado: " + productoId);
        }

        if (!"perecible".equalsIgnoreCase(producto.getTipo())) {
            throw new IllegalArgumentException("El producto debe ser perecible para usar FIFO");
        }

        ColaFIFO cola = new ColaFIFO();
        List<Lotes> lotes = obtenerLotesDelProducto(productoId);

        // Ordenar por fecha de entrada (más antiguo primero)
        lotes.sort(Comparator.comparing(Lotes::getFecha_Entrada));

        // Encolar todos los lotes
        for (Lotes lote : lotes) {
            cola.encolar(lote);
        }

        return cola;
    }

    /**
     * Construye una Pila LIFO desde los lotes de un producto no perecible
     * RF4.2 - Integración con movimientos
     * @param productoId ID del producto
     * @return Pila LIFO con lotes ordenados
     */
    public PilaLIFO construirPilaLIFO(String productoId) throws ExecutionException, InterruptedException {
        Productos producto = productoRepository.findById(productoId);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado: " + productoId);
        }

        if (!"no perecible".equalsIgnoreCase(producto.getTipo())) {
            throw new IllegalArgumentException("El producto debe ser no perecible para usar LIFO");
        }

        PilaLIFO pila = new PilaLIFO();
        List<Lotes> lotes = obtenerLotesDelProducto(productoId);

        // Ordenar por fecha de entrada (más reciente primero)
        lotes.sort(Comparator.comparing(Lotes::getFecha_Entrada).reversed());

        // Apilar todos los lotes
        for (Lotes lote : lotes) {
            pila.push(lote);
        }

        return pila;
    }

    /**
     * Procesa una salida usando algoritmo FIFO
     * RF4.1 - Integrado con movimientos
     * @param movimiento Movimiento de salida
     * @param cola Cola FIFO del producto
     * @return Lista de lotes procesados
     */
    public List<Lotes> procesarSalidaFIFO(Movimientos movimiento, ColaFIFO cola) {
        List<Lotes> lotesProcesados = new ArrayList<>();
        int cantidadRestante = movimiento.getCantidad();

        while (cantidadRestante > 0 && !cola.estaVacia()) {
            Lotes lote = cola.verFrente();
            
            if (lote.getCantidad() >= cantidadRestante) {
                // El lote tiene suficiente cantidad
                lote.setCantidad(lote.getCantidad() - cantidadRestante);
                lotesProcesados.add(lote);
                cantidadRestante = 0;
                
                if (lote.getCantidad() == 0) {
                    cola.desencolar(); // Desencolar si se agotó
                }
            } else {
                // Consumir todo el lote
                cantidadRestante -= lote.getCantidad();
                lotesProcesados.add(cola.desencolar());
            }
        }

        if (cantidadRestante > 0) {
            throw new IllegalStateException("Stock insuficiente. Faltan " + cantidadRestante + " unidades");
        }

        return lotesProcesados;
    }

    /**
     * Procesa una salida usando algoritmo LIFO
     * RF4.2 - Integrado con movimientos
     * @param movimiento Movimiento de salida
     * @param pila Pila LIFO del producto
     * @return Lista de lotes procesados
     */
    public List<Lotes> procesarSalidaLIFO(Movimientos movimiento, PilaLIFO pila) {
        List<Lotes> lotesProcesados = new ArrayList<>();
        int cantidadRestante = movimiento.getCantidad();

        while (cantidadRestante > 0 && !pila.estaVacia()) {
            Lotes lote = pila.verCima();
            
            if (lote.getCantidad() >= cantidadRestante) {
                // El lote tiene suficiente cantidad
                lote.setCantidad(lote.getCantidad() - cantidadRestante);
                lotesProcesados.add(lote);
                cantidadRestante = 0;
                
                if (lote.getCantidad() == 0) {
                    pila.pop(); // Desapilar si se agotó
                }
            } else {
                // Consumir todo el lote
                cantidadRestante -= lote.getCantidad();
                lotesProcesados.add(pila.pop());
            }
        }

        if (cantidadRestante > 0) {
            throw new IllegalStateException("Stock insuficiente. Faltan " + cantidadRestante + " unidades");
        }

        return lotesProcesados;
    }

    /**
     * Procesa una salida usando algoritmo DRIFO (por fecha de vencimiento)
     * @param movimiento Movimiento de salida
     * @param productoId ID del producto
     * @return Lista de lotes procesados
     */
    public List<Lotes> procesarSalidaDRIFO(Movimientos movimiento, String productoId) 
            throws ExecutionException, InterruptedException {
        List<Lotes> lotes = obtenerLotesDelProducto(productoId);
        
        // Ordenar por fecha de vencimiento (próximos a vencer primero)
        lotes.sort(Comparator.comparing(Lotes::getFecha_Vencimiento));
        
        List<Lotes> lotesProcesados = new ArrayList<>();
        int cantidadRestante = movimiento.getCantidad();

        for (Lotes lote : lotes) {
            if (cantidadRestante <= 0) break;
            
            if (lote.getCantidad() >= cantidadRestante) {
                lote.setCantidad(lote.getCantidad() - cantidadRestante);
                lotesProcesados.add(lote);
                cantidadRestante = 0;
            } else {
                cantidadRestante -= lote.getCantidad();
                lotesProcesados.add(lote);
            }
        }

        if (cantidadRestante > 0) {
            throw new IllegalStateException("Stock insuficiente. Faltan " + cantidadRestante + " unidades");
        }

        return lotesProcesados;
    }

    /**
     * Obtiene todos los lotes de un producto desde Firestore
     */
    private List<Lotes> obtenerLotesDelProducto(String productoId) throws ExecutionException, InterruptedException {
        List<Lotes> lotes = new ArrayList<>();
        
        QuerySnapshot snapshot = db.collection(COLLECTION_PRODUCTOS)
                .document(productoId)
                .collection(COLLECTION_LOTES)
                .get()
                .get();
        
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Lotes lote = doc.toObject(Lotes.class);
            if (lote != null) {
                lote.setLoteId(doc.getId());
                lotes.add(lote);
            }
        }
        
        return lotes;
    }

    /**
     * Construye estructura de datos según el algoritmo del producto
     * @param productoId ID del producto
     * @return ColaFIFO, PilaLIFO o null según el algoritmo
     */
    public Object construirEstructuraPorAlgoritmo(String productoId) throws ExecutionException, InterruptedException {
        Productos producto = productoRepository.findById(productoId);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado: " + productoId);
        }

        String algoritmo = producto.getMetodo_rotacion();
        
        if ("FIFO".equalsIgnoreCase(algoritmo)) {
            return construirColaFIFO(productoId);
        } else if ("LIFO".equalsIgnoreCase(algoritmo)) {
            return construirPilaLIFO(productoId);
        } else if ("DRIFO".equalsIgnoreCase(algoritmo)) {
            // DRIFO no usa estructura propia, se ordena dinámicamente
            return null;
        }
        
        return null;
    }
}

