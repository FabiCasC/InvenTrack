package app.service;

import app.model.Productos;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import java.util.List;
import java.util.Scanner;

public class ProductoService {
    public void mostrarProductos() {
        try {
            Firestore bd = FirestoreClient.getFirestore();

            // obtengo toda la colección de productos
            ApiFuture<QuerySnapshot> f = bd.collection("Productos").get();
            List<QueryDocumentSnapshot> docs = f.get().getDocuments();

            for (QueryDocumentSnapshot d : docs) {

                // transformo el documento a la clase para usar sus atributos
                Productos p = d.toObject(Productos.class);

                // actualizo el id de firestore al objeto (el id no viene dentro del doc)
                p.setProductoId(d.getId());

             
                // se valida que el stock_actual no sea negativo
                if (p.getStock_actual() == -1) {
                    System.out.println("el producto " + p.getNombre() + " tiene stock -1 actualizar");
                    Scanner sc = new Scanner(System.in);
                    int nuevoStock;

                    // pedir hasta que ingrese un valor valido
                    do {
                        System.out.print("ingresa un nuevo stock mayor a 10: ");
                        nuevoStock = sc.nextInt();
                        if (nuevoStock < 10) {
                            System.out.println("valor no valido ingresa otro");
                        }
                    } while (nuevoStock < 10);
                    
                    // actualizar en firebase
                    bd.collection("Productos")
                            .document(p.getProductoId())
                            .update("stock_actual", nuevoStock);

                    // actualizar en el objeto
                    p.setStock_actual(nuevoStock);

                    System.out.println("stock actualizado");
                }

                // MOSTRAR DATOS DEL PRODUCTO
                System.out.println("ID_producto: " + p.getProductoId());
                System.out.println("Nombre: " + p.getNombre());
                System.out.println("Tipo: " + p.getTipo());
                System.out.println("Rotacion: " + p.getMetodo_rotacion());
                System.out.println("Stock_actual: " + p.getStock_actual());
                System.out.println("Stock_minimo: " + p.getStock_minimo());
                System.out.println("Stock_maximo: " + p.getStock_maximo());
                System.out.println("ProveedorId: " + p.getProveedorId());
                System.out.println("-------------------------------------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
