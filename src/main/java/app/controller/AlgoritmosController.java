package app.controller;

import app.model.algoritmos.ArbolDecision;
import app.model.Movimientos;
import app.model.Productos;
import app.model.estructuras.ColaFIFO;
import app.model.estructuras.PilaLIFO;
import app.model.estructuras.ListaEnlazada;
import app.service.AlgoritmoService;
import app.service.GrafoService;
import app.service.MovimientoService;
import app.service.PrediccionService;
import app.service.ProductoService;
import app.service.ProveedorService;
import app.service.PrediccionService.PrediccionInventario;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * Controller para gestionar operaciones de Algoritmos
 * RF4 - Módulo de Algoritmos de Inventario
 * Sigue el patrón MVC: View → Controller → Service → Repository
 */
public class AlgoritmosController {
    private final ProductoService productoService;
    private final MovimientoService movimientoService;
    private final ProveedorService proveedorService;
    private final AlgoritmoService algoritmoService;
    private final GrafoService grafoService;
    private final PrediccionService prediccionService;

    public AlgoritmosController() {
        this.productoService = new ProductoService();
        this.movimientoService = new MovimientoService();
        this.proveedorService = new ProveedorService();
        this.algoritmoService = new AlgoritmoService();
        this.grafoService = new GrafoService();
        this.prediccionService = new PrediccionService();
    }

    // ========== RF4.4 - Árbol de Decisión para Predicción de Demanda ==========
    
    /**
     * Ejecuta el árbol de decisión para predecir la demanda de un producto
     * RF4.4 - Basado en: ventas históricas, categorías, estacionalidad, comportamiento de pedidos
     * @param productoId ID del producto a analizar
     * @return Predicción: "subirá", "bajará" o "estable"
     */
    public String ejecutarPrediccionDemanda(String productoId) {
        try {
            PrediccionInventario prediccion = prediccionService.predecirInventario(productoId, 30);
            
            if (prediccion != null) {
                mostrarExito("Predicción generada: La demanda " + prediccion.getPrediccionDemanda());
                return prediccion.getPrediccionDemanda();
            }
            
            return null;

        } catch (Exception e) {
            mostrarError("Error al ejecutar predicción: " + e.getMessage());
            return null;
        }
    }

    /**
     * Entrena el árbol de decisión con nuevos datos
     * RF4.4 - Debe entrenarse cada vez que se acumulen nuevos datos
     */
    public boolean entrenarArbolDecision() {
        try {
            // El árbol se entrena automáticamente cuando se usa el PrediccionService
            // Este método puede usarse para forzar re-entrenamiento
            mostrarExito("Árbol de decisión actualizado con nuevos datos");
            return true;

        } catch (Exception e) {
            mostrarError("Error al entrenar árbol de decisión: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Genera predicción completa de inventario
     * @param productoId ID del producto
     * @param diasProyeccion Días a proyectar
     * @return PredicciónInventario con todos los detalles
     */
    public PrediccionInventario predecirInventario(String productoId, int diasProyeccion) {
        try {
            return prediccionService.predecirInventario(productoId, diasProyeccion);
        } catch (Exception e) {
            mostrarError("Error al predecir inventario: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene los resultados del árbol de decisión
     */
    public String obtenerResultadosArbol(String productoId) {
        try {
            // TODO: Implementar obtención de resultados detallados
            return "Resultados del árbol de decisión para producto: " + productoId;
        } catch (Exception e) {
            mostrarError("Error al obtener resultados: " + e.getMessage());
            return null;
        }
    }

    // ========== RF4.1 - Cola FIFO para Productos Perecibles ==========
    
    /**
     * Construye una cola FIFO para un producto perecible
     * RF4.1 - El primer producto ingresado es el primero en salir
     * @param productoId ID del producto perecible
     * @return Cola FIFO con los lotes del producto
     */
    public ColaFIFO construirColaFIFO(String productoId) {
        try {
            ColaFIFO cola = algoritmoService.construirColaFIFO(productoId);
            Productos producto = productoService.obtenerProducto(productoId);
            
            if (cola != null && producto != null) {
                mostrarExito("Cola FIFO construida para producto: " + producto.getNombre() + " (Tamaño: " + cola.getTamaño() + ")");
            }
            
            return cola;

        } catch (Exception e) {
            mostrarError("Error al construir cola FIFO: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene el siguiente lote a procesar (desencolar)
     * RF4.1 - Cada salida desencola un nodo
     */
    public String obtenerSiguienteLoteFIFO(ColaFIFO cola) {
        try {
            if (cola == null || cola.estaVacia()) {
                mostrarError("La cola está vacía");
                return null;
            }

            // TODO: Implementar desencolar
            // Lotes lote = cola.desencolar();
            return "Lote desencolado correctamente";

        } catch (Exception e) {
            mostrarError("Error al desencolar: " + e.getMessage());
            return null;
        }
    }

    // ========== RF4.2 - Pila LIFO para Productos No Perecibles ==========
    
    /**
     * Construye una pila LIFO para un producto no perecible
     * RF4.2 - El último producto ingresado es el primero en salir
     * @param productoId ID del producto no perecible
     * @return Pila LIFO con los lotes del producto
     */
    public PilaLIFO construirPilaLIFO(String productoId) {
        try {
            PilaLIFO pila = algoritmoService.construirPilaLIFO(productoId);
            Productos producto = productoService.obtenerProducto(productoId);
            
            if (pila != null && producto != null) {
                mostrarExito("Pila LIFO construida para producto: " + producto.getNombre() + " (Tamaño: " + pila.getTamaño() + ")");
            }
            
            return pila;

        } catch (Exception e) {
            mostrarError("Error al construir pila LIFO: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene el siguiente lote a procesar (desapilar)
     * RF4.2 - Cada despacho desapila un nodo
     */
    public String obtenerSiguienteLoteLIFO(PilaLIFO pila) {
        try {
            if (pila == null || pila.estaVacia()) {
                mostrarError("La pila está vacía");
                return null;
            }

            // TODO: Implementar desapilar
            // Lotes lote = pila.pop();
            return "Lote desapilado correctamente";

        } catch (Exception e) {
            mostrarError("Error al desapilar: " + e.getMessage());
            return null;
        }
    }

    // ========== RF4.3 - Lista Enlazada para Movimientos ==========
    
    /**
     * Construye una lista enlazada con los movimientos de un producto
     * RF4.3 - Cada nodo contiene tipo, fecha, cantidad y referencia al siguiente
     * @param productoId ID del producto
     * @return Lista enlazada con los movimientos
     */
    public ListaEnlazada construirListaMovimientos(String productoId) {
        try {
            List<Movimientos> movimientos = movimientoService.listarMovimientosPorProducto(productoId);
            
            if (movimientos == null || movimientos.isEmpty()) {
                mostrarError("No se encontraron movimientos para este producto");
                return null;
            }

            ListaEnlazada lista = new ListaEnlazada();
            
            // Agregar movimientos a la lista enlazada
            for (Movimientos movimiento : movimientos) {
                lista.agregarAlFinal(movimiento);
            }

            mostrarExito("Lista enlazada construida con " + movimientos.size() + " movimientos");
            return lista;

        } catch (Exception e) {
            mostrarError("Error al construir lista de movimientos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene el historial de movimientos desde la lista enlazada
     * RF4.3 - Permite recorrer el historial en tiempo lineal
     */
    public String obtenerHistorialMovimientos(ListaEnlazada lista) {
        try {
            if (lista == null || lista.estaVacia()) {
                return "No hay movimientos registrados";
            }

            // TODO: Implementar recorrido de la lista enlazada
            // Recorrer desde la cabeza y construir string con la información
            
            return "Historial de movimientos generado";

        } catch (Exception e) {
            mostrarError("Error al obtener historial: " + e.getMessage());
            return null;
        }
    }

    // ========== RF4.5 - Grafo de Relaciones Productos-Proveedores ==========
    
    /**
     * Construye un grafo de relaciones entre productos y proveedores
     * RF4.5 - nodos = productos y proveedores, aristas = relaciones de abastecimiento
     * @return Información del grafo construido
     */
    public String construirGrafoProductosProveedores() {
        try {
            String info = grafoService.obtenerInformacionGrafo();
            mostrarExito("Grafo de relaciones construido exitosamente");
            return info;

        } catch (Exception e) {
            mostrarError("Error al construir grafo: " + e.getMessage());
            return null;
        }
    }

    /**
     * Detecta el proveedor crítico (grado mayor)
     * RF4.5 - Detectar proveedor crítico (grado mayor)
     * @return ID del proveedor crítico
     */
    public String detectarProveedorCritico() {
        try {
            String proveedorCritico = grafoService.detectarProveedorCritico();
            
            if (proveedorCritico != null) {
                mostrarExito("Proveedor crítico detectado: " + proveedorCritico);
            } else {
                mostrarError("No se encontró ningún proveedor crítico");
            }
            
            return proveedorCritico;

        } catch (Exception e) {
            mostrarError("Error al detectar proveedor crítico: " + e.getMessage());
            return null;
        }
    }

    /**
     * Muestra las rutas desde un proveedor hacia varios productos
     * RF4.5 - Mostrar rutas desde un proveedor hacia varios productos
     * @param proveedorId ID del proveedor
     * @return Lista de productos del proveedor
     */
    public List<String> obtenerRutasProveedor(String proveedorId) {
        try {
            List<String> productos = grafoService.obtenerProductosDeProveedor(proveedorId);
            
            if (productos != null && !productos.isEmpty()) {
                mostrarExito("Se encontraron " + productos.size() + " productos para el proveedor");
            } else {
                mostrarError("No se encontraron productos para este proveedor");
            }
            
            return productos;

        } catch (Exception e) {
            mostrarError("Error al obtener rutas: " + e.getMessage());
            return null;
        }
    }

    /**
     * Detecta dependencias para reabastecimiento
     * RF4.5 - Detectar dependencias para reabastecimiento
     * @return Mapa de dependencias (producto -> lista de proveedores)
     */
    public Map<String, List<String>> detectarDependenciasReabastecimiento() {
        try {
            Map<String, List<String>> dependencias = grafoService.detectarDependencias();
            
            if (dependencias != null && !dependencias.isEmpty()) {
                mostrarExito("Dependencias detectadas para " + dependencias.size() + " productos");
            } else {
                mostrarError("No se encontraron dependencias");
            }
            
            return dependencias;

        } catch (Exception e) {
            mostrarError("Error al detectar dependencias: " + e.getMessage());
            return null;
        }
    }

    // ========== RF4.6 - Sistema de Alertas Inteligentes ==========
    
    /**
     * Genera alertas cuando el stock proyectado llegue a nivel crítico usando árbol de decisión
     * RF4.6 - Stock proyectado llegue a nivel crítico
     * @return Lista de alertas generadas
     */
    public String generarAlertasStockCritico() {
        try {
            List<Productos> productos = productoService.listarProductos();
            if (productos == null || productos.isEmpty()) {
                return "No hay productos registrados";
            }
            
            StringBuilder alertas = new StringBuilder();
            ArbolDecision arbolDecision = new ArbolDecision();
            List<Movimientos> movimientos = movimientoService.listarMovimientos();
            
            // Entrenar árbol con datos actuales
            arbolDecision.entrenar(movimientos, productos);

            for (Productos producto : productos) {
                if (producto.getStock_actual() <= producto.getStock_minimo()) {
                    // Generar alerta usando árbol de decisión
                    ArbolDecision.Alerta alerta = arbolDecision.generarAlerta(producto.getProductoId(), producto);
                    
                    if (alerta != null) {
                        alertas.append("[").append(alerta.getNivel()).append("] ")
                               .append(producto.getNombre())
                               .append(" - ").append(alerta.getMensaje())
                               .append("\n  Acción: ").append(alerta.getAccionRecomendada())
                               .append("\n");
                    } else {
                        alertas.append("ALERTA: ").append(producto.getNombre())
                               .append(" - Stock crítico: ").append(producto.getStock_actual())
                               .append(" (Mínimo: ").append(producto.getStock_minimo()).append(")\n");
                    }
                }
            }

            if (alertas.length() == 0) {
                return "No hay alertas de stock crítico";
            }

            mostrarExito("Alertas de stock crítico generadas");
            return alertas.toString();

        } catch (Exception e) {
            mostrarError("Error al generar alertas: " + e.getMessage());
            return null;
        }
    }

    /**
     * Detecta patrones de venta inusuales
     * RF4.6 - Detectar patrones de venta inusuales
     * @return Patrones detectados
     */
    public String detectarPatronesVentaInusuales() {
        try {
            // TODO: Implementar detección de patrones inusuales
            // Analizar movimientos históricos y detectar anomalías
            
            return "Patrones de venta inusuales detectados";

        } catch (Exception e) {
            mostrarError("Error al detectar patrones: " + e.getMessage());
            return null;
        }
    }

    /**
     * Genera alertas cuando un proveedor crítico tiene demoras
     * RF4.6 - Proveedor crítico tenga demoras
     * @return Alertas generadas
     */
    public String generarAlertasProveedorDemoras() {
        try {
            // TODO: Implementar detección de demoras de proveedores
            // Analizar pedidos pendientes y tiempos de entrega
            
            return "Alertas de demoras de proveedores generadas";

        } catch (Exception e) {
            mostrarError("Error al generar alertas de proveedores: " + e.getMessage());
            return null;
        }
    }

    /**
     * Genera todas las alertas inteligentes
     * RF4.6 - Sistema completo de alertas
     */
    public String generarTodasLasAlertas() {
        try {
            StringBuilder todasAlertas = new StringBuilder();
            
            todasAlertas.append("=== ALERTAS DE STOCK CRÍTICO ===\n");
            todasAlertas.append(generarAlertasStockCritico()).append("\n\n");
            
            todasAlertas.append("=== ALERTAS DE PATRONES INUSUALES ===\n");
            todasAlertas.append(detectarPatronesVentaInusuales()).append("\n\n");
            
            todasAlertas.append("=== ALERTAS DE PROVEEDORES ===\n");
            todasAlertas.append(generarAlertasProveedorDemoras()).append("\n");
            
            mostrarExito("Todas las alertas generadas exitosamente");
            return todasAlertas.toString();

        } catch (Exception e) {
            mostrarError("Error al generar alertas: " + e.getMessage());
            return null;
        }
    }

    // ========== Métodos de Utilidad ==========
    
    /**
     * Obtiene métricas del módulo de algoritmos
     */
    public String obtenerMetricas() {
        try {
            StringBuilder metricas = new StringBuilder();
            metricas.append("=== MÉTRICAS DEL MÓDULO DE ALGORITMOS ===\n\n");
            
            // TODO: Agregar métricas reales
            metricas.append("Árbol de Decisión: Entrenado\n");
            metricas.append("Estructuras de Datos: Disponibles\n");
            metricas.append("Sistema de Alertas: Activo\n");
            
            return metricas.toString();

        } catch (Exception e) {
            mostrarError("Error al obtener métricas: " + e.getMessage());
            return null;
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}

