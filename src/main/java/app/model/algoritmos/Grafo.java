package app.model.algoritmos;

import app.model.Productos;
import app.model.Proveedores;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Grafo para modelar relaciones entre Productos y Proveedores
 * RF4.5 - nodos = productos y proveedores, aristas = relaciones de abastecimiento
 */
public class Grafo {
    // Mapa de nodos: clave = ID, valor = NodoGrafo
    private Map<String, NodoGrafo> nodos;
    
    // Lista de aristas (relaciones)
    private List<Arista> aristas;
    
    public Grafo() {
        this.nodos = new HashMap<>();
        this.aristas = new ArrayList<>();
    }
    
    /**
     * Agrega un nodo de producto al grafo
     */
    public void agregarNodoProducto(Productos producto) {
        if (producto != null && producto.getProductoId() != null) {
            NodoGrafo nodo = new NodoGrafo(producto.getProductoId(), "PRODUCTO", producto.getNombre());
            nodos.put(producto.getProductoId(), nodo);
        }
    }
    
    /**
     * Agrega un nodo de proveedor al grafo
     */
    public void agregarNodoProveedor(Proveedores proveedor) {
        if (proveedor != null && proveedor.getProveedorId() != null) {
            NodoGrafo nodo = new NodoGrafo(proveedor.getProveedorId(), "PROVEEDOR", proveedor.getNombre());
            nodos.put(proveedor.getProveedorId(), nodo);
        }
    }
    
    /**
     * Agrega una arista (relación de abastecimiento) entre proveedor y producto
     */
    public void agregarArista(String proveedorId, String productoId) {
        if (nodos.containsKey(proveedorId) && nodos.containsKey(productoId)) {
            Arista arista = new Arista(proveedorId, productoId);
            aristas.add(arista);
            
            // Agregar referencia en los nodos
            nodos.get(proveedorId).agregarVecino(productoId);
            nodos.get(productoId).agregarVecino(proveedorId);
        }
    }
    
    /**
     * Obtiene el grado de un nodo (número de conexiones)
     */
    public int obtenerGrado(String nodoId) {
        if (nodos.containsKey(nodoId)) {
            return nodos.get(nodoId).getVecinos().size();
        }
        return 0;
    }
    
    /**
     * Detecta el proveedor crítico (mayor grado)
     * RF4.5 - Detectar proveedor crítico (grado mayor)
     */
    public String detectarProveedorCritico() {
        String proveedorCritico = null;
        int maxGrado = 0;
        
        for (Map.Entry<String, NodoGrafo> entry : nodos.entrySet()) {
            NodoGrafo nodo = entry.getValue();
            if ("PROVEEDOR".equals(nodo.getTipo())) {
                int grado = nodo.getVecinos().size();
                if (grado > maxGrado) {
                    maxGrado = grado;
                    proveedorCritico = entry.getKey();
                }
            }
        }
        
        return proveedorCritico;
    }
    
    /**
     * Obtiene todos los productos abastecidos por un proveedor
     * RF4.5 - Mostrar rutas desde un proveedor hacia varios productos
     */
    public List<String> obtenerProductosDeProveedor(String proveedorId) {
        List<String> productos = new ArrayList<>();
        
        if (nodos.containsKey(proveedorId)) {
            NodoGrafo proveedor = nodos.get(proveedorId);
            for (String vecinoId : proveedor.getVecinos()) {
                NodoGrafo vecino = nodos.get(vecinoId);
                if (vecino != null && "PRODUCTO".equals(vecino.getTipo())) {
                    productos.add(vecinoId);
                }
            }
        }
        
        return productos;
    }
    
    /**
     * Detecta dependencias para reabastecimiento
     * RF4.5 - Detectar dependencias para reabastecimiento
     */
    public Map<String, List<String>> detectarDependencias() {
        Map<String, List<String>> dependencias = new HashMap<>();
        
        for (Map.Entry<String, NodoGrafo> entry : nodos.entrySet()) {
            NodoGrafo nodo = entry.getValue();
            if ("PRODUCTO".equals(nodo.getTipo())) {
                List<String> proveedores = new ArrayList<>();
                for (String vecinoId : nodo.getVecinos()) {
                    NodoGrafo vecino = nodos.get(vecinoId);
                    if (vecino != null && "PROVEEDOR".equals(vecino.getTipo())) {
                        proveedores.add(vecinoId);
                    }
                }
                if (!proveedores.isEmpty()) {
                    dependencias.put(entry.getKey(), proveedores);
                }
            }
        }
        
        return dependencias;
    }
    
    /**
     * Obtiene información del grafo
     */
    public String obtenerInformacionGrafo() {
        StringBuilder info = new StringBuilder();
        info.append("=== INFORMACIÓN DEL GRAFO ===\n");
        info.append("Total de nodos: ").append(nodos.size()).append("\n");
        info.append("Total de aristas: ").append(aristas.size()).append("\n");
        
        int productos = 0;
        int proveedores = 0;
        for (NodoGrafo nodo : nodos.values()) {
            if ("PRODUCTO".equals(nodo.getTipo())) productos++;
            else if ("PROVEEDOR".equals(nodo.getTipo())) proveedores++;
        }
        
        info.append("Productos: ").append(productos).append("\n");
        info.append("Proveedores: ").append(proveedores).append("\n");
        
        return info.toString();
    }
    
    // Clase interna para representar un nodo del grafo
    public static class NodoGrafo {
        private String id;
        private String tipo; // PRODUCTO o PROVEEDOR
        private String nombre;
        private Set<String> vecinos;
        
        public NodoGrafo(String id, String tipo, String nombre) {
            this.id = id;
            this.tipo = tipo;
            this.nombre = nombre;
            this.vecinos = new HashSet<>();
        }
        
        public void agregarVecino(String vecinoId) {
            vecinos.add(vecinoId);
        }
        
        // Getters
        public String getId() { return id; }
        public String getTipo() { return tipo; }
        public String getNombre() { return nombre; }
        public Set<String> getVecinos() { return vecinos; }
    }
    
    // Clase interna para representar una arista (relación)
    public static class Arista {
        private String proveedorId;
        private String productoId;
        
        public Arista(String proveedorId, String productoId) {
            this.proveedorId = proveedorId;
            this.productoId = productoId;
        }
        
        // Getters
        public String getProveedorId() { return proveedorId; }
        public String getProductoId() { return productoId; }
    }
}

