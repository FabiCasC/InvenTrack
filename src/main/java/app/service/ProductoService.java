package app.service;

import app.model.Lotes;
import app.model.Productos;
import app.repository.ProductoRepository;
import app.repository.ProveedorRepository;
import com.google.cloud.firestore.DocumentSnapshot;
import java.util.List;
import java.util.concurrent.ExecutionException;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import java.util.Calendar;
import java.util.Date;

public class ProductoService {
    private final Firestore db;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final String COLLECTION_PRODUCTOS = "Productos";
    private final String COLLECTION_LOTES = "Lotes";

    public ProductoService() {
        this.db = FirestoreClient.getFirestore();
        this.productoRepository = new ProductoRepository();
        this.proveedorRepository = new ProveedorRepository();
    }
    
    //registro con validaciones
    public Productos registrarProducto(Productos producto, Lotes loteInicial) throws ExecutionException, InterruptedException {
        validarProducto(producto);
        if (!proveedorRepository.existsById(producto.getProveedorId())) {
            throw new IllegalArgumentException("El proveedor con ID " + producto.getProveedorId() + " no existe. Debe registrarlo primero.");
        }
        
        // Guardar usando repository
        productoRepository.save(producto);

        //validar si tienen algun tipo de metodo de rotacion
        if (tieneMetodoRotacion(producto.getMetodo_rotacion()) && loteInicial != null) {
            //generar el id del lote
            if (loteInicial.getLoteId() == null || loteInicial.getLoteId().trim().isEmpty()) {
                loteInicial.setLoteId(generarLoteId(producto.getProductoId()));
            }
            registrarLoteInicial(producto.getProductoId(), loteInicial);
        }

        return producto;
    }

    //meetodos de ayuda
    private void validarProducto(Productos producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }

        if (producto.getStock_actual() < 0) {
            throw new IllegalArgumentException("El stock actual no puede ser negativo");
        }

        if (producto.getStock_minimo() < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
        }

        if (producto.getStock_maximo() <= producto.getStock_minimo()) {
            throw new IllegalArgumentException("El stock máximo debe ser mayor al stock mínimo");
        }

        if (producto.getProveedorId() == null || producto.getProveedorId().trim().isEmpty()) {
            throw new IllegalArgumentException("El proveedor es obligatorio");
        }
    }

    /**
     * Obtiene un producto por su ID
     */
    public Productos obtenerProducto(String productoId) throws ExecutionException, InterruptedException {
        Productos producto = productoRepository.findById(productoId);
        if (producto == null) {
            throw new IllegalArgumentException("El producto no existe: " + productoId);
        }
        return producto;
    }

    /**
     * Lista todos los productos
     */
    public List<Productos> listarProductos() throws ExecutionException, InterruptedException {
        return productoRepository.findAll();
    }

    /**
     * Lista productos por proveedor
     */
    public List<Productos> listarProductosPorProveedor(String proveedorId) throws ExecutionException, InterruptedException {
        return productoRepository.findByProveedorId(proveedorId);
    }

    /**
     * Actualiza un producto
     */
    public void actualizarProducto(Productos producto) throws ExecutionException, InterruptedException {
        validarProducto(producto);
        
        if (!productoRepository.existsById(producto.getProductoId())) {
            throw new IllegalArgumentException("El producto no existe: " + producto.getProductoId());
        }
        
        productoRepository.update(producto);
    }

    /**
     * Elimina un producto
     */
    public void eliminarProducto(String productoId) throws ExecutionException, InterruptedException {
        if (!productoRepository.existsById(productoId)) {
            throw new IllegalArgumentException("El producto no existe: " + productoId);
        }
        
        productoRepository.delete(productoId);
    }

    private boolean tieneMetodoRotacion(String metodoRotacion) {
        return metodoRotacion != null
                && (metodoRotacion.equalsIgnoreCase("FIFO")
                || metodoRotacion.equalsIgnoreCase("LIFO")
                || metodoRotacion.equalsIgnoreCase("DRIFO"));
    }

    private void registrarLoteInicial(String productoId, Lotes lote) throws ExecutionException, InterruptedException {
        if (lote.getLoteId() == null || lote.getLoteId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del lote es obligatorio");
        }

        db.collection(COLLECTION_PRODUCTOS)
                .document(productoId)
                .collection(COLLECTION_LOTES)
                .document(lote.getLoteId())
                .set(lote)
                .get();
    }

    //id automatico para los productos a registrar
    private String generarProductoId() throws ExecutionException, InterruptedException {
        int totalProductos = (int) db.collection(COLLECTION_PRODUCTOS).get().get().size();
        String nuevoId;

        do {
            totalProductos++;
            nuevoId = String.format("PROD%03d", totalProductos);
        } while (existeProducto(nuevoId));

        return nuevoId;
    }

    //id para lotes generado
    private String generarLoteId(String productoId) throws ExecutionException, InterruptedException {
        int totalLotes = (int) db.collection(COLLECTION_PRODUCTOS)
                .document(productoId)
                .collection(COLLECTION_LOTES)
                .get()
                .get()
                .size();

        String nuevoId;
        do {
            totalLotes++;
            nuevoId = String.format("LOT-%s-%03d", productoId, totalLotes);
        } while (existeLote(productoId, nuevoId));

        return nuevoId;
    }

    //verificar la existencia de un lote no se si sea necesario para el registro
    private boolean existeLote(String productoId, String loteId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = db.collection(COLLECTION_PRODUCTOS)
                .document(productoId)
                .collection(COLLECTION_LOTES)
                .document(loteId)
                .get()
                .get();
        return document.exists();
    }
    
    
}
