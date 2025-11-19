
package app.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import java.util.List;


public class UsuarioService {
    
    //metodo para mostrar usuarios de firestore
    public void mostrarUsuarios(){
        try {
            Firestore bd = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> f = bd.collection("Usuarios").get();
            List<QueryDocumentSnapshot> docs = f.get().getDocuments();

            for (QueryDocumentSnapshot d : docs) {
                System.out.println("ID: " + d.getId());
                System.out.println("Datos: " + d.getData());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
}
