
package app.service;

import app.model.Usuarios;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import java.util.List;


public class UsuarioService {
    
    //metodo para mostrar usuarios de firestore
    public void mostrarUsuarios() {
        try {
            Firestore bd = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> f = bd.collection("Usuarios").get();
            List<QueryDocumentSnapshot> docs = f.get().getDocuments();

            for (QueryDocumentSnapshot d : docs) {

                //transforma el documento a la clase para usar los atributos
                Usuarios u = d.toObject(Usuarios.class);

                // actualizo el id de firestore para mostrarlo en java
                u.setUsuarioId(d.getId());

                // Ahora sí usas tus atributos del POJO
                System.out.println("ID: " + u.getUsuarioId());
                System.out.println("Nombre: " + u.getNombre());
                System.out.println("Email: " + u.getEmail());
                System.out.println("Rol: " + u.getRol());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
