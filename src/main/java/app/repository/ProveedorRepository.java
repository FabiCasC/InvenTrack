package app.repository;

import app.config.FirebaseConfig;
import app.model.Proveedores;
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
 * Repository para operaciones CRUD de Proveedores en Firestore
 */
public class ProveedorRepository {
    private final Firestore db;
    private static final String COLLECTION_NAME = "Proveedores";

    public ProveedorRepository() {
        this.db = FirebaseConfig.getFirestore();
    }

    /**
     * Guarda un proveedor en Firestore
     * @param proveedor El proveedor a guardar
     * @return El ID del proveedor guardado
     */
    public String save(Proveedores proveedor) throws ExecutionException, InterruptedException {
        if (proveedor.getProveedorId() == null || proveedor.getProveedorId().trim().isEmpty()) {
            proveedor.setProveedorId(generateProveedorId());
        }

        Map<String, Object> proveedorData = new HashMap<>();
        proveedorData.put("proveedorId", proveedor.getProveedorId());
        proveedorData.put("nombre", proveedor.getNombre());
        proveedorData.put("telefono", proveedor.getTelefono());
        proveedorData.put("email", proveedor.getEmail());

        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(proveedor.getProveedorId())
                .set(proveedorData);

        future.get();
        return proveedor.getProveedorId();
    }

    /**
     * Busca un proveedor por su ID
     * @param proveedorId ID del proveedor
     * @return El proveedor encontrado o null si no existe
     */
    public Proveedores findById(String proveedorId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = db.collection(COLLECTION_NAME)
                .document(proveedorId)
                .get()
                .get();

        if (document.exists()) {
            Proveedores proveedor = document.toObject(Proveedores.class);
            if (proveedor != null) {
                proveedor.setProveedorId(document.getId());
            }
            return proveedor;
        }

        return null;
    }

    /**
     * Obtiene todos los proveedores
     * @return Lista de todos los proveedores
     */
    public List<Proveedores> findAll() throws ExecutionException, InterruptedException {
        List<Proveedores> proveedores = new ArrayList<>();
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = query.get();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            Proveedores proveedor = document.toObject(Proveedores.class);
            if (proveedor != null) {
                proveedor.setProveedorId(document.getId());
                proveedores.add(proveedor);
            }
        }

        return proveedores;
    }

    /**
     * Actualiza un proveedor existente
     * @param proveedor El proveedor con los datos actualizados
     */
    public void update(Proveedores proveedor) throws ExecutionException, InterruptedException {
        if (proveedor.getProveedorId() == null || proveedor.getProveedorId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del proveedor es obligatorio para actualizar");
        }

        Map<String, Object> proveedorData = new HashMap<>();
        proveedorData.put("nombre", proveedor.getNombre());
        proveedorData.put("telefono", proveedor.getTelefono());
        proveedorData.put("email", proveedor.getEmail());

        db.collection(COLLECTION_NAME)
                .document(proveedor.getProveedorId())
                .update(proveedorData)
                .get();
    }

    /**
     * Elimina un proveedor por su ID
     * @param proveedorId ID del proveedor a eliminar
     */
    public void delete(String proveedorId) throws ExecutionException, InterruptedException {
        db.collection(COLLECTION_NAME)
                .document(proveedorId)
                .delete()
                .get();
    }

    /**
     * Verifica si existe un proveedor con el ID dado
     * @param proveedorId ID del proveedor
     * @return true si existe, false en caso contrario
     */
    public boolean existsById(String proveedorId) throws ExecutionException, InterruptedException {
        return findById(proveedorId) != null;
    }

    /**
     * Busca un proveedor por email
     * @param email Email del proveedor
     * @return El proveedor encontrado o null si no existe
     */
    public Proveedores findByEmail(String email) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .get();

        QuerySnapshot querySnapshot = query.get();

        if (!querySnapshot.isEmpty()) {
            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
            Proveedores proveedor = document.toObject(Proveedores.class);
            if (proveedor != null) {
                proveedor.setProveedorId(document.getId());
            }
            return proveedor;
        }

        return null;
    }

    /**
     * Genera un ID automático para un nuevo proveedor
     */
    private String generateProveedorId() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = query.get();

        if (querySnapshot.isEmpty()) {
            return "PROV001";
        }

        int maxNumber = 0;
        for (QueryDocumentSnapshot doc : querySnapshot.getDocuments()) {
            String id = doc.getId();
            if (id != null && id.startsWith("PROV") && id.length() == 7) {
                try {
                    int number = Integer.parseInt(id.substring(4));
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar IDs que no sigan el formato PROV###
                }
            }
        }

        return String.format("PROV%03d", maxNumber + 1);
    }
}

