package app.controller;

import app.model.Proveedores;
import app.service.ProveedorService;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Controller para gestionar operaciones de Proveedores
 * Sigue el patrón MVC: View → Controller → Service → Repository
 */
public class ProveedorController {
    private final ProveedorService proveedorService;

    public ProveedorController() {
        this.proveedorService = new ProveedorService();
    }

    /**
     * Registra un nuevo proveedor
     */
    public boolean registrarProveedor(String nombre, String telefono, String email) {
        try {
            Proveedores proveedor = new Proveedores();
            proveedor.setNombre(nombre);
            proveedor.setTelefono(telefono);
            proveedor.setEmail(email);

            String proveedorId = proveedorService.registrarProveedor(proveedor);
            mostrarExito("Proveedor registrado exitosamente.\nID: " + proveedorId);
            return true;

        } catch (Exception e) {
            mostrarError(e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza un proveedor existente
     */
    public boolean actualizarProveedor(String proveedorId, String nombre, String telefono, String email) {
        try {
            Proveedores proveedor = proveedorService.obtenerProveedor(proveedorId);
            if (proveedor == null) {
                mostrarError("Proveedor no encontrado");
                return false;
            }

            proveedor.setNombre(nombre);
            proveedor.setTelefono(telefono);
            proveedor.setEmail(email);

            proveedorService.actualizarProveedor(proveedor);
            mostrarExito("Proveedor actualizado exitosamente");
            return true;

        } catch (Exception e) {
            mostrarError(e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un proveedor
     */
    public boolean eliminarProveedor(String proveedorId) {
        try {
            int respuesta = JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro que desea eliminar este proveedor?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                proveedorService.eliminarProveedor(proveedorId);
                mostrarExito("Proveedor eliminado exitosamente");
                return true;
            }
            return false;

        } catch (Exception e) {
            mostrarError(e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene un proveedor por su ID
     */
    public Proveedores obtenerProveedor(String proveedorId) {
        try {
            return proveedorService.obtenerProveedor(proveedorId);
        } catch (Exception e) {
            mostrarError("Error al obtener proveedor: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista todos los proveedores
     */
    public List<Proveedores> listarProveedores() {
        try {
            return proveedorService.listarProveedores();
        } catch (Exception e) {
            mostrarError("Error al listar proveedores: " + e.getMessage());
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

