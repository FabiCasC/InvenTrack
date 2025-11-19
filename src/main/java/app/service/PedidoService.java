
package app.service;

import app.model.Pedidos;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import java.text.SimpleDateFormat;
import java.util.List;

public class PedidoService {
    public void mostrarPedidos() {
        try {
            //conexion a firestore
            Firestore db = FirestoreClient.getFirestore();

            // leer la colección Pedidos
            ApiFuture<QuerySnapshot> ped = db.collection("Pedidos").get();
            List<QueryDocumentSnapshot> pedidos = ped.get().getDocuments();

            for (QueryDocumentSnapshot pedidoDoc : pedidos) {

                // transformar documento a objeto Java
                Pedidos p = pedidoDoc.toObject(Pedidos.class);

                // asignar ID del documento
                p.setPedidoId(pedidoDoc.getId());

                // formatear fecha
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String fechaStr = (p.getFecha() != null) ? sdf.format(p.getFecha()) : "Sin fecha";

                // mostrar datos
                System.out.println("PEDIDO ID: " + p.getPedidoId());
                System.out.println("Producto ID: " + p.getProductoId());
                System.out.println("Cantidad Solicitada: " + p.getCantidadSolicitada());
                System.out.println("Estado: " + p.getEstado());
                System.out.println("Fecha: " + fechaStr);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
