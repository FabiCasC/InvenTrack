package app.repository;

import app.config.FirebaseConfig;
import app.model.Productos;
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
 * Repository para operaciones CRUD de Productos en Firestore
 * Implementa el patrón Repository para separar la lógica de acceso a datos
 */
public class ProductoRepository {
    private final Firestore db;
    private static final String COLLECTION_NAME = "productos";

    public ProductoRepository() {
        this.db = FirebaseConfig.getFirestore();
    }

    /**
     * Guarda un producto en Firestore
     * @param producto El producto a guardar
     * @return El ID del producto guardado
     */
    public String save(Productos producto) throws ExecutionException, InterruptedException {
        if (producto.getProductoId() == null || producto.getProductoId().trim().isEmpty()) {
            producto.setProductoId(generateProductoId());
        }

        Map<String, Object> productoData = new HashMap<>();
        productoData.put("productoId", producto.getProductoId());
        productoData.put("nombre", producto.getNombre());
        productoData.put("tipo", producto.getTipo());
        productoData.put("metodo_rotacion", producto.getMetodo_rotacion());
        productoData.put("stock_actual", producto.getStock_actual());
        productoData.put("stock_minimo", producto.getStock_minimo());
        productoData.put("stock_maximo", producto.getStock_maximo());
        productoData.put("proveedorId", producto.getProveedorId());
        if (producto.getFechaIngreso() != null) {
            productoData.put("fechaIngreso", producto.getFechaIngreso());
        }

        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(producto.getProductoId())
                .set(productoData);

        future.get();
        return producto.getProductoId();
    }

    /**
     * Busca un producto por su ID
     * @param productoId ID del producto
     * @return El producto encontrado o null si no existe
     */
    public Productos findById(String productoId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = db.collection(COLLECTION_NAME)
                .document(productoId)
                .get()
                .get();

        if (document.exists()) {
            Productos producto = document.toObject(Productos.class);
            if (producto != null) {
                producto.setProductoId(document.getId());
            }
            return producto;
        }

        return null;
    }

    /**
     * Obtiene todos los productos
     * @return Lista de todos los productos
     */
    public List<Productos> findAll() throws ExecutionException, InterruptedException {
        List<Productos> productos = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = query.get();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Productos producto = document.toObject(Productos.class);
            if (producto != null) {
                producto.setProductoId(document.getId());
                productos.add(producto);
            }
        }

        return productos;
    }

    /**
     * Actualiza un producto existente
     * @param producto El producto con los datos actualizados
     */
    public void update(Productos producto) throws ExecutionException, InterruptedException {
        if (producto.getProductoId() == null || producto.getProductoId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del producto es obligatorio para actualizar");
        }

        Map<String, Object> productoData = new HashMap<>();
        productoData.put("nombre", producto.getNombre());
        productoData.put("tipo", producto.getTipo());
        productoData.put("metodo_rotacion", producto.getMetodo_rotacion());
        productoData.put("stock_actual", producto.getStock_actual());
        productoData.put("stock_minimo", producto.getStock_minimo());
        productoData.put("stock_maximo", producto.getStock_maximo());
        productoData.put("proveedorId", producto.getProveedorId());
        if (producto.getFechaIngreso() != null) {
            productoData.put("fechaIngreso", producto.getFechaIngreso());
        }

        db.collection(COLLECTION_NAME)
                .document(producto.getProductoId())
                .update(productoData)
                .get();
    }

    /**
     * Actualiza solo el stock de un producto
     * @param productoId ID del producto
     * @param nuevoStock Nuevo valor de stock
     */
    public void updateStock(String productoId, int nuevoStock) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION_NAME)
                .document(productoId)
                .update("stock_actual", nuevoStock)
                .get();
    }

    /**
     * Elimina un producto por su ID
     * @param productoId ID del producto a eliminar
     */
    public void delete(String productoId) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION_NAME)
                .document(productoId)
                .delete()
                .get();
    }

    /**
     * Verifica si existe un producto con el ID dado
     * @param productoId ID del producto
     * @return true si existe, false en caso contrario
     */
    public boolean existsById(String productoId) throws ExecutionException, InterruptedException {
        return findById(productoId) != null;
    }

    /**
     * Busca productos por proveedor
     * @param proveedorId ID del proveedor
     * @return Lista de productos del proveedor
     */
    public List<Productos> findByProveedorId(String proveedorId) throws ExecutionException, InterruptedException {
        List<Productos> productos = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME)
                .whereEqualTo("proveedorId", proveedorId)
                .get();

        QuerySnapshot querySnapshot = query.get();
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Productos producto = document.toObject(Productos.class);
            if (producto != null) {
                producto.setProductoId(document.getId());
                productos.add(producto);
            }
        }

        return productos;
    }

    /**
     * Genera un ID automático para un nuevo producto
     */
    private String generateProductoId() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = query.get();

        if (querySnapshot.isEmpty()) {
            return "PROD001";
        }

        int maxNumber = 0;
        for (QueryDocumentSnapshot doc : querySnapshot.getDocuments()) {
            String id = doc.getId();
            if (id != null && id.startsWith("PROD") && id.length() == 7) {
                try {
                    int number = Integer.parseInt(id.substring(4));
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar IDs que no sigan el formato PROD###
                }
            }
        }

        return String.format("PROD%03d", maxNumber + 1);
    }
}
