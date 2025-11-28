
package app.service;

import app.model.Pedidos;
import app.model.Productos;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;



public class PedidoService {
    private final Firestore db;
    private final String COLLECTION_PEDIDOS = "Pedidos";
    private final String COLLECTION_PRODUCTOS = "Productos";
    
    public PedidoService() {
        this.db = FirestoreClient.getFirestore();
    }
    
    //registrar pedido adaptado para el formulario
    public Pedidos registrarPedido(Pedidos pedido) throws ExecutionException, InterruptedException {
        //valida datos del pedido
        validarPedido(pedido);
        
        //verificar que el producto exista 
        Productos producto = obtenerProducto(pedido.getProductoId());
        
        //validar el stock para que sea sufiente
        if (producto.getStock_actual() < pedido.getCantidadSolicitada()) {
            throw new IllegalArgumentException(
                "Stock insuficiente. Disponible: " + producto.getStock_actual() + 
                ", Solicitado: " + pedido.getCantidadSolicitada()
            );
        }
        
        //genera un id del pedido automatico
        if (pedido.getPedidoId() == null || pedido.getPedidoId().trim().isEmpty()) {
            pedido.setPedidoId(generarPedidoId());
        }
        
        //asigna la fecha actual
        if (pedido.getFecha() == null) {
            pedido.setFecha(new Date());
        }
        
        //asigna como estado incial pendiente 
        if (pedido.getEstado() == null || pedido.getEstado().trim().isEmpty()) {
            pedido.setEstado("pendiente");
        }
        
        //guarda el pedido en la bd
        db.collection(COLLECTION_PEDIDOS)
                .document(pedido.getPedidoId())
                .set(pedido)
                .get();

        //guarda lo que necesito no me me añade el documento id repitiendo el id del pedido
        Map<String, Object> pedidoData = new HashMap<>();
        pedidoData.put("cliente", pedido.getCliente());
        pedidoData.put("productoId", pedido.getProductoId());
        pedidoData.put("cantidadSolicitada", pedido.getCantidadSolicitada());
        pedidoData.put("estado", pedido.getEstado());
        pedidoData.put("fecha", pedido.getFecha());
        //guarda los datos en firebase
        db.collection(COLLECTION_PEDIDOS)
                .document(pedido.getPedidoId())
                .set(pedidoData) //guarda el map donde estan los datos que se necesitan mostrar
                .get();
        
        return pedido;
    }
    
    
    private void validarPedido(Pedidos pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        
        if (pedido.getProductoId() == null || pedido.getProductoId().trim().isEmpty()) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }
        
        if (pedido.getCantidadSolicitada() <= 0) {
            throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a 0");
        }
    }
    //obtener productos por el id
    private Productos obtenerProducto(String productoId) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db.collection(COLLECTION_PRODUCTOS)
                .document(productoId)
                .get()
                .get();
        
        if (!doc.exists()) {
            throw new IllegalArgumentException("El producto no existe: " + productoId);
        }
        
        return doc.toObject(Productos.class);
    }
    //generar automaticamente un id por el pedido
    private String generarPedidoId() throws ExecutionException, InterruptedException {
        int total = (int) db.collection(COLLECTION_PEDIDOS).get().get().size();
        String nuevoId;
        
        do {
            total++;
            nuevoId = String.format("PED%03d", total);
        } while (existePedido(nuevoId));
        
        return nuevoId;
    }
    //verificar que el pedido exista
    private boolean existePedido(String pedidoId) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db.collection(COLLECTION_PEDIDOS)
                .document(pedidoId)
                .get()
                .get();
        return doc.exists();
    }
    
}
