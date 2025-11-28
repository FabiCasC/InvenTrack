package app.service;

import app.model.Lotes;
import app.model.Productos;
import com.google.cloud.firestore.DocumentSnapshot;
import java.util.concurrent.ExecutionException;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import java.util.Calendar;
import java.util.Date;

public class ProductoService {
    private final Firestore db;
    private final String COLLECTION_PRODUCTOS = "Productos";
    private final String COLLECTION_LOTES = "Lotes";
    private final String COLLECTION_PROVEEDORES = "Proveedores";

    public ProductoService() {
        this.db = FirestoreClient.getFirestore();
    }
    
    //registro con validaciones
    public Productos registrarProducto(Productos producto, Lotes loteInicial) throws ExecutionException, InterruptedException {
        validarProducto(producto);
        if (!existeProveedor(producto.getProveedorId())) { //verificar que el proveedor exista en bd
            throw new IllegalArgumentException("El proveedor con ID " + producto.getProveedorId() + " no existe. Debe registrarlo primero.");
        }
        //generar un id para el producto
        if (producto.getProductoId() == null || producto.getProductoId().trim().isEmpty()) {
            producto.setProductoId(generarProductoId());
        } else {
            //verificar que el id no se repita
            if (existeProducto(producto.getProductoId())) {
                throw new IllegalArgumentException("Ya existe un producto con el ID: " + producto.getProductoId());
            }
        }
        //guardar datos en firebase
        db.collection(COLLECTION_PRODUCTOS)
                .document(producto.getProductoId())
                .set(producto)
                .get();

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

    private boolean existeProducto(String productoId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = db.collection(COLLECTION_PRODUCTOS)
                .document(productoId)
                .get()
                .get();
        return document.exists();
    }

    private boolean existeProveedor(String proveedorId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = db.collection(COLLECTION_PROVEEDORES)
                .document(proveedorId)
                .get()
                .get();
        return document.exists();
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
