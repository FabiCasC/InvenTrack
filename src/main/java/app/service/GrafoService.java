package app.service;

import app.model.Productos;
import app.model.Proveedores;
import app.model.algoritmos.Grafo;
import app.repository.ProductoRepository;
import app.repository.ProveedorRepository;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Service para gestionar el grafo de relaciones Productos-Proveedores
 * RF4.5 - Construcción y análisis del grafo
 */
public class GrafoService {
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;

    public GrafoService() {
        this.productoRepository = new ProductoRepository();
        this.proveedorRepository = new ProveedorRepository();
    }

    /**
     * Construye el grafo completo de relaciones productos-proveedores
     * RF4.5 - nodos = productos y proveedores, aristas = relaciones de abastecimiento
     */
    public Grafo construirGrafoCompleto() throws ExecutionException, InterruptedException {
        Grafo grafo = new Grafo();
        
        // Obtener todos los productos y proveedores
        List<Productos> productos = productoRepository.findAll();
        List<Proveedores> proveedores = proveedorRepository.findAll();
        
        // Agregar nodos de productos
        for (Productos producto : productos) {
            grafo.agregarNodoProducto(producto);
        }
        
        // Agregar nodos de proveedores
        for (Proveedores proveedor : proveedores) {
            grafo.agregarNodoProveedor(proveedor);
        }
        
        // Agregar aristas (relaciones de abastecimiento)
        for (Productos producto : productos) {
            if (producto.getProveedorId() != null && !producto.getProveedorId().trim().isEmpty()) {
                grafo.agregarArista(producto.getProveedorId(), producto.getProductoId());
            }
        }
        
        return grafo;
    }

    /**
     * Detecta el proveedor crítico (mayor grado)
     * RF4.5 - Detectar proveedor crítico (grado mayor)
     */
    public String detectarProveedorCritico() throws ExecutionException, InterruptedException {
        Grafo grafo = construirGrafoCompleto();
        return grafo.detectarProveedorCritico();
    }

    /**
     * Obtiene todos los productos de un proveedor
     * RF4.5 - Mostrar rutas desde un proveedor hacia varios productos
     */
    public List<String> obtenerProductosDeProveedor(String proveedorId) throws ExecutionException, InterruptedException {
        Grafo grafo = construirGrafoCompleto();
        return grafo.obtenerProductosDeProveedor(proveedorId);
    }

    /**
     * Detecta dependencias para reabastecimiento
     * RF4.5 - Detectar dependencias para reabastecimiento
     */
    public Map<String, List<String>> detectarDependencias() throws ExecutionException, InterruptedException {
        Grafo grafo = construirGrafoCompleto();
        return grafo.detectarDependencias();
    }

    /**
     * Obtiene información del grafo
     */
    public String obtenerInformacionGrafo() throws ExecutionException, InterruptedException {
        Grafo grafo = construirGrafoCompleto();
        return grafo.obtenerInformacionGrafo();
    }
}

