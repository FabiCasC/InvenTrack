package app.controller;

import app.model.Lotes;
import app.model.Productos;
import app.service.ProductoService;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Controller para gestionar operaciones de Productos
 * Sigue el patrón MVC: View → Controller → Service → Repository
 */
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController() {
        this.productoService = new ProductoService();
    }

    /**
     * Registra un nuevo producto
     */
    public boolean registrarProducto(String nombre, String tipo, String metodoRotacion,
                                    int stockActual, int stockMinimo, int stockMaximo,
                                    String proveedorId, Date fechaIngreso, Lotes loteInicial) {
        try {
            Productos producto = new Productos();
            producto.setNombre(nombre);
            producto.setTipo(tipo);
            producto.setMetodo_rotacion(metodoRotacion);
            producto.setStock_actual(stockActual);
            producto.setStock_minimo(stockMinimo);
            producto.setStock_maximo(stockMaximo);
            producto.setProveedorId(proveedorId);
            producto.setFechaIngreso(fechaIngreso);

            Productos productoRegistrado = productoService.registrarProducto(producto, loteInicial);
            mostrarExito("Producto registrado exitosamente.\nID: " + productoRegistrado.getProductoId());
            return true;

        } catch (Exception e) {
            mostrarError(e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza un producto existente
     */
    public boolean actualizarProducto(String productoId, String nombre, String tipo,
                                     String metodoRotacion, int stockActual,
                                     int stockMinimo, int stockMaximo, String proveedorId) {
        try {
            Productos producto = productoService.obtenerProducto(productoId);
            if (producto == null) {
                mostrarError("Producto no encontrado");
                return false;
            }

            producto.setNombre(nombre);
            producto.setTipo(tipo);
            producto.setMetodo_rotacion(metodoRotacion);
            producto.setStock_actual(stockActual);
            producto.setStock_minimo(stockMinimo);
            producto.setStock_maximo(stockMaximo);
            producto.setProveedorId(proveedorId);

            productoService.actualizarProducto(producto);
            mostrarExito("Producto actualizado exitosamente");
            return true;

        } catch (Exception e) {
            mostrarError(e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un producto
     */
    public boolean eliminarProducto(String productoId) {
        try {
            int respuesta = JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro que desea eliminar este producto?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                productoService.eliminarProducto(productoId);
                mostrarExito("Producto eliminado exitosamente");
                return true;
            }
            return false;

        } catch (Exception e) {
            mostrarError(e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene un producto por su ID
     */
    public Productos obtenerProducto(String productoId) {
        try {
            return productoService.obtenerProducto(productoId);
        } catch (Exception e) {
            mostrarError("Error al obtener producto: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene todos los productos
     */
    public List<Productos> listarProductos() {
        try {
            return productoService.listarProductos();
        } catch (Exception e) {
            mostrarError("Error al listar productos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista productos por proveedor
     */
    public List<Productos> listarProductosPorProveedor(String proveedorId) {
        try {
            return productoService.listarProductosPorProveedor(proveedorId);
        } catch (Exception e) {
            mostrarError("Error al listar productos: " + e.getMessage());
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

