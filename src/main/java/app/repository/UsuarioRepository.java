
package app.repository;

import app.config.FirebaseConfig;
import app.model.Usuarios;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class UsuarioRepository {
    private final Firestore db;
    private static final String COLLECTION_NAME = "Usuarios";

    public UsuarioRepository() {
        this.db = FirebaseConfig.getFirestore();
    }

    /**
     * Busca un usuario por email
     */
    public Usuarios findByEmail(String email) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .get();

        QuerySnapshot querySnapshot = query.get();
        
        if (!querySnapshot.isEmpty()) {
            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
            Usuarios usuario = document.toObject(Usuarios.class);
            usuario.setUsuarioId(document.getId());
            return usuario;
        }
        
        return null;
    }

    /**
     * Guarda un nuevo usuario en Firestore
     */
    public String save(Usuarios usuario) throws ExecutionException, InterruptedException {
        // Si no tiene ID, generarlo
        if (usuario.getUsuarioId()== null || usuario.getUsuarioId().isEmpty()) {
            usuario.setUsuarioId(generateUserId());
        }

        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("nombre", usuario.getNombre());
        usuarioData.put("email", usuario.getEmail());
        usuarioData.put("contraseña", usuario.getContraseña());

        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                .document(usuario.getUsuarioId())
                .set(usuarioData);

        future.get();
        return usuario.getUsuarioId();
    }

    /**
     * Verifica si existe un usuario con el email dado
     */
    public boolean existsByEmail(String email) throws ExecutionException, InterruptedException {
        return findByEmail(email) != null;
    }

    
    //genera un id para el nuevo usuario a registrar
    private String generateUserId() throws ExecutionException, InterruptedException {
        // Obtener todos los usuarios (sin orderBy para evitar necesidad de índice)
        ApiFuture<QuerySnapshot> query = db.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = query.get();

        if (querySnapshot.isEmpty()) {
            return "U001"; 
        }

        int maxNumber = 0;
        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
            String id = doc.getId();
            if (id != null && id.startsWith("U") && id.length() == 4) {
                try {
                    int number = Integer.parseInt(id.substring(1));
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar IDs que no sigan el formato U###
                }
            }
        }

        return String.format("U%03d", maxNumber + 1);
    }
}
