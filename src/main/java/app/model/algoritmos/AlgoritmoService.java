package app.service;

import app.model.Lotes;
import app.model.Productos;
// Importamos tus estructuras de datos personalizadas
import app.model.estructuras.ColaFIFO;
import app.model.estructuras.PilaLIFO;
import app.model.estructuras.ListaEnlazada;
import app.repository.ProductoRepository;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * SERVICIO DE ALGORITMOS ESTRUCTURADOS
 * Centraliza la lógica para obtener el siguiente producto según FIFO/LIFO
 * y generar reportes mediante Listas Enlazadas.
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

    // ====================================================================
    // 1. MÉTODO: SIGUIENTE PRODUCTO EN FIFO (First-In, First-Out)
    // ====================================================================
    /**
     * Identifica cuál es el próximo lote a salir según la lógica FIFO (El más antiguo).
     * @param productoId El ID del producto a consultar.
     * @return El objeto Lotes que está al frente de la cola (o null si no hay).
     */
    public Lotes obtenerSiguienteEnFIFO(String productoId) throws ExecutionException, InterruptedException {
        // A. Validar producto
        validarExistenciaProducto(productoId);

        // B. Obtener lotes y ordenarlos por fecha (Antiguo -> Nuevo)
        List<Lotes> lotesRaw = obtenerLotesDesdeBD(productoId);
        lotesRaw.sort(Comparator.comparing(Lotes::getFecha_Entrada));

        // C. Construir la Cola FIFO
        ColaFIFO cola = new ColaFIFO();
        for (Lotes lote : lotesRaw) {
            cola.encolar(lote);
        }

        // D. Retornar el frente (Peek) sin eliminarlo
        return cola.verFrente();
    }

    // ====================================================================
    // 2. MÉTODO: SIGUIENTE PRODUCTO EN LIFO (Last-In, First-Out)
    // ====================================================================
    /**
     * Identifica cuál es el próximo lote a salir según la lógica LIFO (El más reciente).
     * @param productoId El ID del producto a consultar.
     * @return El objeto Lotes que está en la cima de la pila (o null si no hay).
     */
    public Lotes obtenerSiguienteEnLIFO(String productoId) throws ExecutionException, InterruptedException {
        // A. Validar producto
        validarExistenciaProducto(productoId);

        // B. Obtener lotes y ordenarlos por fecha (Antiguo -> Nuevo)
        // Nota: Al apilar en orden cronológico, el más nuevo queda en la CIMA.
        List<Lotes> lotesRaw = obtenerLotesDesdeBD(productoId);
        lotesRaw.sort(Comparator.comparing(Lotes::getFecha_Entrada));

        // C. Construir la Pila LIFO
        PilaLIFO pila = new PilaLIFO();
        for (Lotes lote : lotesRaw) {
            pila.apilar(lote); 
        }

        // D. Retornar la cima (Peek) sin eliminarlo
        return pila.verCima(); // Asegúrate de tener este método en PilaLIFO
    }

    // ====================================================================
    // 3. MÉTODO: INVENTARIO ORDENADO (LISTA ENLAZADA)
    // ====================================================================
    /**
     * Devuelve todo el inventario de un producto ordenado por fecha de entrada
     * encapsulado en una Lista Enlazada personalizada.
     * @param productoId El ID del producto.
     * @return Una ListaEnlazada conteniendo los lotes ordenados.
     */
    public ListaEnlazada obtenerInventarioOrdenado(String productoId) throws ExecutionException, InterruptedException {
        // A. Validar producto
        validarExistenciaProducto(productoId);

        // B. Obtener lotes y ordenarlos por fecha
        List<Lotes> lotesRaw = obtenerLotesDesdeBD(productoId);
        lotesRaw.sort(Comparator.comparing(Lotes::getFecha_Entrada));

        // C. Construir la Lista Enlazada
        ListaEnlazada lista = new ListaEnlazada();
        for (Lotes lote : lotesRaw) {
            lista.agregar(lote); // Asegúrate de tener este método en ListaEnlazada
        }

        return lista;
    }

    // ====================================================================
    // MÉTODOS PRIVADOS AUXILIARES (Conexión a BD)
    // ====================================================================

    /**
     * Descarga los lotes crudos desde Firestore.
     */
    private List<Lotes> obtenerLotesDesdeBD(String productoId) throws ExecutionException, InterruptedException {
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
                // Solo agregamos lotes que tengan stock positivo
                if (lote.getCantidad() > 0) {
                    lotes.add(lote);
                }
            }
        }
        return lotes;
    }

    /**
     * Verifica que el producto exista antes de procesar.
     */
    private void validarExistenciaProducto(String productoId) throws ExecutionException, InterruptedException {
        if (productoRepository.findById(productoId) == null) {
            throw new IllegalArgumentException("El producto con ID " + productoId + " no existe.");
        }
    }
}
