package app.repository;

import app.config.FirebaseConfig;
import app.model.Movimientos;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Repository para operaciones CRUD de Movimientos en Firestore
 */
public class MovimientoRepository {
    private final Firestore db;
    private static final String COLLECTION_NAME = "movimientos";

    public MovimientoRepository() {
        this.db = FirebaseConfig.getFirestore();
    }

    /**
     * Guarda un movimiento en Firestore
     * @param movimiento El movimiento a guardar
     * @return El ID del movimiento guardado
     */
    public String save(Movimientos movimiento) throws ExecutionException, InterruptedException {
        if (movimiento.getMovimientoId() == null || movimiento.getMovimientoId().trim().isEmpty()) {
            movimiento.setMovimientoId(generateMovimientoId());
        }

        Map<String, Object> movimientoData = new HashMap<>();
        movimientoData.put("movimientoId", movimiento.getMovimientoId());
        movimientoData.put("productoId", movimiento.getProductoId());
        movimientoData.put("tipo_movimiento", movimiento.getTipo_movimiento());
        movimientoData.put("cantidad", movimiento.getCantidad());
        movimientoData.put("fecha", movimiento.getFecha());
        movimientoData.put("usuarioId", movimiento.getUsuarioId());
        movimientoData.put("algoritmo", movimiento.getAlgoritmo());

        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(movimiento.getMovimientoId())
                .set(movimientoData);

        future.get();
        return movimiento.getMovimientoId();
    }

    /**
     * Busca un movimiento por su ID
     * @param movimientoId ID del movimiento
     * @return El movimiento encontrado o null si no existe
     */
    public Movimientos findById(String movimientoId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = db.collection(COLLECTION_NAME)
                .document(movimientoId)
                .get()
                .get();

        if (document.exists()) {
            Movimientos movimiento = document.toObject(Movimientos.class);
            if (movimiento != null) {
                movimiento.setMovimientoId(document.getId());
            }
            return movimiento;
        }

        return null;
    }

    /**
     * Obtiene todos los movimientos
     * @return Lista de todos los movimientos
     */
    public List<Movimientos> findAll() throws ExecutionException, InterruptedException {
        List<Movimientos> movimientos = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = query.get();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Movimientos movimiento = document.toObject(Movimientos.class);
            if (movimiento != null) {
                movimiento.setMovimientoId(document.getId());
                movimientos.add(movimiento);
            }
        }

        return movimientos;
    }

    /**
     * Busca movimientos por producto
     * @param productoId ID del producto
     * @return Lista de movimientos del producto
     */
    public List<Movimientos> findByProductoId(String productoId) throws ExecutionException, InterruptedException {
        List<Movimientos> movimientos = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME)
                .whereEqualTo("productoId", productoId)
                .get();

        QuerySnapshot querySnapshot = query.get();
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Movimientos movimiento = document.toObject(Movimientos.class);
            if (movimiento != null) {
                movimiento.setMovimientoId(document.getId());
                movimientos.add(movimiento);
            }
        }

        return movimientos;
    }

    /**
     * Busca movimientos por rango de fechas
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de movimientos en el rango
     */
    public List<Movimientos> findByFechaRange(Date fechaInicio, Date fechaFin) throws ExecutionException, InterruptedException {
        List<Movimientos> movimientos = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("fecha", fechaInicio)
                .whereLessThanOrEqualTo("fecha", fechaFin)
                .get();

        QuerySnapshot querySnapshot = query.get();
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Movimientos movimiento = document.toObject(Movimientos.class);
            if (movimiento != null) {
                movimiento.setMovimientoId(document.getId());
                movimientos.add(movimiento);
            }
        }

        return movimientos;
    }

    /**
     * Busca movimientos por tipo
     * @param tipoMovimiento Tipo de movimiento (entrada/salida)
     * @return Lista de movimientos del tipo especificado
     */
    public List<Movimientos> findByTipo(String tipoMovimiento) throws ExecutionException, InterruptedException {
        List<Movimientos> movimientos = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME)
                .whereEqualTo("tipo_movimiento", tipoMovimiento)
                .get();

        QuerySnapshot querySnapshot = query.get();
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Movimientos movimiento = document.toObject(Movimientos.class);
            if (movimiento != null) {
                movimiento.setMovimientoId(document.getId());
                movimientos.add(movimiento);
            }
        }

        return movimientos;
    }

    /**
     * Verifica si existe un movimiento con el ID dado
     * @param movimientoId ID del movimiento
     * @return true si existe, false en caso contrario
     */
    public boolean existsById(String movimientoId) throws ExecutionException, InterruptedException {
        return findById(movimientoId) != null;
    }

    /**
     * Genera un ID automático para un nuevo movimiento
     */
    private String generateMovimientoId() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = query.get();

        if (querySnapshot.isEmpty()) {
            return "MOV001";
        }

        int maxNumber = 0;
        for (QueryDocumentSnapshot doc : querySnapshot.getDocuments()) {
            String id = doc.getId();
            if (id != null && id.startsWith("MOV") && id.length() == 6) {
                try {
                    int number = Integer.parseInt(id.substring(3));
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar IDs que no sigan el formato MOV###
                }
            }
        }

        return String.format("MOV%03d", maxNumber + 1);
    }
}

