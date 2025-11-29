package app.repository;

import app.config.FirebaseConfig;
import app.model.Pedidos;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Repository para operaciones CRUD de Pedidos en Firestore
 */
public class PedidoRepository {
    private final Firestore db;
    private static final String COLLECTION_NAME = "Pedidos";

    public PedidoRepository() {
        this.db = FirebaseConfig.getFirestore();
    }

    /**
     * Guarda un pedido en Firestore
     * @param pedido El pedido a guardar
     * @return El ID del pedido guardado
     */
    public String save(Pedidos pedido) throws ExecutionException, InterruptedException {
        if (pedido.getPedidoId() == null || pedido.getPedidoId().trim().isEmpty()) {
            pedido.setPedidoId(generatePedidoId());
        }

        Map<String, Object> pedidoData = new HashMap<>();
        pedidoData.put("pedidoId", pedido.getPedidoId());
        pedidoData.put("productoId", pedido.getProductoId());
        pedidoData.put("cliente", pedido.getCliente());
        pedidoData.put("cantidadSolicitada", pedido.getCantidadSolicitada());
        pedidoData.put("estado", pedido.getEstado());
        pedidoData.put("fecha", pedido.getFecha());

        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(pedido.getPedidoId())
                .set(pedidoData);

        future.get();
        return pedido.getPedidoId();
    }

    /**
     * Busca un pedido por su ID
     * @param pedidoId ID del pedido
     * @return El pedido encontrado o null si no existe
     */
    public Pedidos findById(String pedidoId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = db.collection(COLLECTION_NAME)
                .document(pedidoId)
                .get()
                .get();

        if (document.exists()) {
            Pedidos pedido = document.toObject(Pedidos.class);
            if (pedido != null) {
                pedido.setPedidoId(document.getId());
            }
            return pedido;
        }

        return null;
    }

    /**
     * Obtiene todos los pedidos
     * @return Lista de todos los pedidos
     */
    public List<Pedidos> findAll() throws ExecutionException, InterruptedException {
        List<Pedidos> pedidos = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = query.get();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Pedidos pedido = document.toObject(Pedidos.class);
            if (pedido != null) {
                pedido.setPedidoId(document.getId());
                pedidos.add(pedido);
            }
        }

        return pedidos;
    }

    /**
     * Actualiza el estado de un pedido
     * @param pedidoId ID del pedido
     * @param nuevoEstado Nuevo estado (Pendiente/En proceso/Entregado/Cancelado)
     */
    public void updateEstado(String pedidoId, String nuevoEstado) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION_NAME)
                .document(pedidoId)
                .update("estado", nuevoEstado)
                .get();
    }

    /**
     * Actualiza un pedido completo
     * @param pedido El pedido con los datos actualizados
     */
    public void update(Pedidos pedido) throws ExecutionException, InterruptedException {
        if (pedido.getPedidoId() == null || pedido.getPedidoId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del pedido es obligatorio para actualizar");
        }

        Map<String, Object> pedidoData = new HashMap<>();
        pedidoData.put("productoId", pedido.getProductoId());
        pedidoData.put("cliente", pedido.getCliente());
        pedidoData.put("cantidadSolicitada", pedido.getCantidadSolicitada());
        pedidoData.put("estado", pedido.getEstado());
        pedidoData.put("fecha", pedido.getFecha());

        db.collection(COLLECTION_NAME)
                .document(pedido.getPedidoId())
                .update(pedidoData)
                .get();
    }

    /**
     * Busca pedidos por producto
     * @param productoId ID del producto
     * @return Lista de pedidos del producto
     */
    public List<Pedidos> findByProductoId(String productoId) throws ExecutionException, InterruptedException {
        List<Pedidos> pedidos = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME)
                .whereEqualTo("productoId", productoId)
                .get();

        QuerySnapshot querySnapshot = query.get();
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Pedidos pedido = document.toObject(Pedidos.class);
            if (pedido != null) {
                pedido.setPedidoId(document.getId());
                pedidos.add(pedido);
            }
        }

        return pedidos;
    }

    /**
     * Busca pedidos por estado
     * @param estado Estado del pedido
     * @return Lista de pedidos con ese estado
     */
    public List<Pedidos> findByEstado(String estado) throws ExecutionException, InterruptedException {
        List<Pedidos> pedidos = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME)
                .whereEqualTo("estado", estado)
                .get();

        QuerySnapshot querySnapshot = query.get();
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Pedidos pedido = document.toObject(Pedidos.class);
            if (pedido != null) {
                pedido.setPedidoId(document.getId());
                pedidos.add(pedido);
            }
        }

        return pedidos;
    }

    /**
     * Verifica si existe un pedido con el ID dado
     * @param pedidoId ID del pedido
     * @return true si existe, false en caso contrario
     */
    public boolean existsById(String pedidoId) throws ExecutionException, InterruptedException {
        return findById(pedidoId) != null;
    }

    /**
     * Genera un ID automático para un nuevo pedido
     */
    private String generatePedidoId() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = query.get();

        if (querySnapshot.isEmpty()) {
            return "PED001";
        }

        int maxNumber = 0;
        for (QueryDocumentSnapshot doc : querySnapshot.getDocuments()) {
            String id = doc.getId();
            if (id != null && id.startsWith("PED") && id.length() == 6) {
                try {
                    int number = Integer.parseInt(id.substring(3));
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar IDs que no sigan el formato PED###
                }
            }
        }

        return String.format("PED%03d", maxNumber + 1);
    }
}

