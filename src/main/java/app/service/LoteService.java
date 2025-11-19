
package app.service;

import app.model.Lotes;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import java.text.SimpleDateFormat;
import java.util.List;



public class LoteService {
    
    public void mostrarLotes() {

        try {
            Firestore db = FirestoreClient.getFirestore();

            // recorre los productos
            ApiFuture<QuerySnapshot> productosFuture = db.collection("Productos").get();
            List<QueryDocumentSnapshot> productos = productosFuture.get().getDocuments();

            for (QueryDocumentSnapshot p : productos) {

                System.out.println("Producto ID: " + p.getId());

                // leer subcoleccion en los productos
                ApiFuture<QuerySnapshot> lotesFuture = p.getReference().collection("lotes").get();
                List<QueryDocumentSnapshot> lotes = lotesFuture.get().getDocuments();

                for (QueryDocumentSnapshot loteDoc : lotes) {

                    // transformar documento a objeto Java
                    Lotes l = loteDoc.toObject(Lotes.class);

                    // asignar id manualmente
                    l.setLoteId(loteDoc.getId());

                    // formatear fecha
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    // convertir a string
                    String fechaEntradaStr = sdf.format(l.getFecha_Entrada());
                    String fechaVencStr = sdf.format(l.getFecha_Vencimiento());

                    // mostrar datos de subcoleccion lote
                    System.out.println("Lote ID: " + l.getLoteId());
                    System.out.println("Cantidad: " + l.getCantidad());
                    System.out.println("Fecha Entrada: " + fechaEntradaStr);
                    System.out.println("Fecha Vencimiento: " + fechaVencStr);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
