
package app.service;
import app.model.Movimientos;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import java.text.SimpleDateFormat;
import java.util.List;

public class MovimientoService {
    public void mostrarMovimientos() {
        try {
            Firestore db = FirestoreClient.getFirestore();

            // leer la colección Movimientos
            ApiFuture<QuerySnapshot> mov = db.collection("Movimientos").get();
            List<QueryDocumentSnapshot> movimientos = mov.get().getDocuments();

            for (QueryDocumentSnapshot movDoc : movimientos) {

                // transformar documento a objeto Java
                Movimientos m = movDoc.toObject(Movimientos.class);

                // asignar ID del documento
                m.setMovimientoId(movDoc.getId());

                // formatear fecha
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String fechaStr = (m.getFecha() != null) ? sdf.format(m.getFecha()) : "Sin fecha";

                // mostrar datos
                System.out.println("Movimiento ID: " + m.getMovimientoId());
                System.out.println("Producto ID: " + m.getProductoId());
                System.out.println("Tipo movimiento: " + m.getTipo_movimiento());
                System.out.println("Cantidad: " + m.getCantidad());
                System.out.println("Fecha: " + fechaStr);
                System.out.println("Algoritmo: " + m.getAlgoritmo());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
