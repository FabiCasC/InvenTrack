package app.service;

import app.model.Productos;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import java.util.List;

public class ProductoService {
    public void mostrarProductos() {
        try {
            Firestore bd = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> f = bd.collection("Productos").get();
            List<QueryDocumentSnapshot> docs = f.get().getDocuments();

            for (QueryDocumentSnapshot d : docs) {

                //transforma el documento a la clase para usar los atributos
                Productos p = d.toObject(Productos.class);

                // actualizo el id de firestore para mostrarlo en java
                p.setProductoId(d.getId());

                // para usar atributos de la clase
                System.out.println("ID_producto: " + p.getProductoId());
                System.out.println("Nombre: " + p.getNombre());
                System.out.println("Tipo: " + p.getTipo());
                System.out.println("Rotacion: " + p.getMetodo_rotacion());
                System.out.println("Stock_actual: " + p.getStock_actual());
                System.out.println("Stock_minimo: " + p.getStock_minimo());
                System.out.println("Stock_maximo: " + p.getStock_maximo());
                System.out.println("ProveedorId: " + p.getProveedorId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
