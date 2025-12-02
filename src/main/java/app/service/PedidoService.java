
package app.service;

import app.model.Movimientos;
import app.model.Pedidos;
import app.model.Productos;
import app.utils.SessionManager;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;



public class PedidoService {
    private final Firestore db;
    private final String COLLECTION_PEDIDOS = "Pedidos";
    private final String COLLECTION_PRODUCTOS = "productos"; // Corregido: debe ser minúscula como en otros servicios
    private final MovimientoService movimientoService;
    
    public PedidoService() {
        this.db = FirestoreClient.getFirestore();
        this.movimientoService = new MovimientoService();
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
        
        //asigna como estado inicial pendiente (normalizado a minúscula para consistencia)
        if (pedido.getEstado() == null || pedido.getEstado().trim().isEmpty()) {
            pedido.setEstado("pendiente");
        } else {
            // Normalizar estado a minúscula para consistencia
            pedido.setEstado(pedido.getEstado().toLowerCase());
        }
        
        // Guardar el pedido en la base de datos
        Map<String, Object> pedidoData = new HashMap<>();
        pedidoData.put("cliente", pedido.getCliente());
        pedidoData.put("productoId", pedido.getProductoId());
        pedidoData.put("cantidadSolicitada", pedido.getCantidadSolicitada());
        pedidoData.put("estado", pedido.getEstado());
        pedidoData.put("fecha", pedido.getFecha());
        
        db.collection(COLLECTION_PEDIDOS)
                .document(pedido.getPedidoId())
                .set(pedidoData)
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
    
    /**
     * Actualiza el estado de un pedido
     * Si el nuevo estado es "Entregado", crea automáticamente un movimiento de salida
     * que reduce el stock usando FIFO/LIFO según el método de rotación del producto
     */
    public void actualizarEstado(String pedidoId, String nuevoEstado) throws ExecutionException, InterruptedException {
        if (!existePedido(pedidoId)) {
            throw new IllegalArgumentException("El pedido no existe: " + pedidoId);
        }
        
        // Obtener el pedido actual para verificar el estado anterior
        Pedidos pedidoActual = obtenerPedido(pedidoId);
        String estadoAnterior = pedidoActual.getEstado();
        
        // Normalizar el nuevo estado a minúscula para consistencia
        String nuevoEstadoNormalizado = nuevoEstado != null ? nuevoEstado.toLowerCase() : nuevoEstado;
        
        // Actualizar el estado en la base de datos
        db.collection(COLLECTION_PEDIDOS)
                .document(pedidoId)
                .update("estado", nuevoEstadoNormalizado)
                .get();
        
        // Si el nuevo estado es "entregado" y el estado anterior NO era "entregado",
        // crear automáticamente un movimiento de salida
        if ("entregado".equalsIgnoreCase(nuevoEstadoNormalizado) && 
            !"entregado".equalsIgnoreCase(estadoAnterior != null ? estadoAnterior.toLowerCase() : "")) {
            
            try {
                crearMovimientoSalidaDesdePedido(pedidoActual);
                System.out.println("Movimiento de salida creado automáticamente para el pedido: " + pedidoId);
            } catch (Exception e) {
                // Si falla la creación del movimiento, revertir el estado del pedido
                db.collection(COLLECTION_PEDIDOS)
                        .document(pedidoId)
                        .update("estado", estadoAnterior)
                        .get();
                throw new IllegalStateException(
                    "Error al crear movimiento de salida: " + e.getMessage() + 
                    ". El estado del pedido se ha revertido.", e
                );
            }
        }
    }
    
    /**
     * Crea un movimiento de salida automáticamente cuando un pedido se marca como "Entregado"
     * Este movimiento reduce el stock usando FIFO/LIFO según el método de rotación del producto
     */
    private void crearMovimientoSalidaDesdePedido(Pedidos pedido) throws ExecutionException, InterruptedException {
        // Obtener el producto para validar stock y método de rotación
        Productos producto = obtenerProducto(pedido.getProductoId());
        
        // Validar que hay stock suficiente
        if (producto.getStock_actual() < pedido.getCantidadSolicitada()) {
            throw new IllegalArgumentException(
                "No se puede entregar el pedido. Stock insuficiente. " +
                "Disponible: " + producto.getStock_actual() + 
                ", Solicitado: " + pedido.getCantidadSolicitada()
            );
        }
        
        // Crear el movimiento de salida
        Movimientos movimiento = new Movimientos();
        movimiento.setProductoId(pedido.getProductoId());
        movimiento.setTipo_movimiento("salida");
        movimiento.setCantidad(pedido.getCantidadSolicitada());
        movimiento.setFecha(new Date());
        
        // Obtener el usuario actual de la sesión
        String usuarioId = SessionManager.getInstance().getUsuarioId();
        if (usuarioId != null) {
            movimiento.setUsuarioId(usuarioId);
        }
        
        // El algoritmo se asignará automáticamente según el método de rotación del producto
        // en MovimientoService.registrarMovimiento()
        
        // Registrar el movimiento (esto procesará la salida con FIFO/LIFO y actualizará lotes)
        // No se necesita loteId ni fechaVencimiento para movimientos de salida
        movimientoService.registrarMovimiento(movimiento, null, null);
        
        System.out.println("Movimiento de salida creado: " + movimiento.getMovimientoId() + 
                         " para el pedido: " + pedido.getPedidoId() +
                         ". Cantidad: " + pedido.getCantidadSolicitada() +
                         ". Algoritmo usado: " + movimiento.getAlgoritmo());
    }
    
    /**
     * Obtiene un pedido por su ID
     */
    public Pedidos obtenerPedido(String pedidoId) throws ExecutionException, InterruptedException {
        DocumentSnapshot doc = db.collection(COLLECTION_PEDIDOS)
                .document(pedidoId)
                .get()
                .get();
        
        if (!doc.exists()) {
            throw new IllegalArgumentException("El pedido no existe: " + pedidoId);
        }
        
        Pedidos pedido = doc.toObject(Pedidos.class);
        if (pedido != null) {
            pedido.setPedidoId(doc.getId());
        }
        return pedido;
    }
    
    /**
     * Lista todos los pedidos
     */
    public List<Pedidos> listarPedidos() throws ExecutionException, InterruptedException {
        List<Pedidos> pedidos = new ArrayList<>();
        QuerySnapshot snapshot = db.collection(COLLECTION_PEDIDOS).get().get();
        
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Pedidos pedido = doc.toObject(Pedidos.class);
            if (pedido != null) {
                pedido.setPedidoId(doc.getId());
                pedidos.add(pedido);
            }
        }
        
        return pedidos;
    }
    
    /**
     * Lista pedidos por producto
     */
    public List<Pedidos> listarPedidosPorProducto(String productoId) throws ExecutionException, InterruptedException {
        List<Pedidos> pedidos = new ArrayList<>();
        QuerySnapshot snapshot = db.collection(COLLECTION_PEDIDOS)
                .whereEqualTo("productoId", productoId)
                .get()
                .get();
        
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Pedidos pedido = doc.toObject(Pedidos.class);
            if (pedido != null) {
                pedido.setPedidoId(doc.getId());
                pedidos.add(pedido);
            }
        }
        
        return pedidos;
    }
    
    /**
     * Lista pedidos por estado
     */
    public List<Pedidos> listarPedidosPorEstado(String estado) throws ExecutionException, InterruptedException {
        List<Pedidos> pedidos = new ArrayList<>();
        QuerySnapshot snapshot = db.collection(COLLECTION_PEDIDOS)
                .whereEqualTo("estado", estado)
                .get()
                .get();
        
        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            Pedidos pedido = doc.toObject(Pedidos.class);
            if (pedido != null) {
                pedido.setPedidoId(doc.getId());
                pedidos.add(pedido);
            }
        }
        
        return pedidos;
    }
    
}
