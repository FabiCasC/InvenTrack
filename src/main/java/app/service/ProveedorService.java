package app.service;

import app.model.Proveedores;
import app.repository.ProveedorRepository;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Service para gestionar operaciones de negocio relacionadas con Proveedores
 * Sigue el patrón MVC: View → Controller → Service → Repository
 */
public class ProveedorService {
    private final ProveedorRepository proveedorRepository;

    public ProveedorService() {
        this.proveedorRepository = new ProveedorRepository();
    }

    /**
     * Registra un nuevo proveedor
     */
    public String registrarProveedor(Proveedores proveedor) throws ExecutionException, InterruptedException {
        validarProveedor(proveedor);
        return proveedorRepository.save(proveedor);
    }

    /**
     * Actualiza un proveedor existente
     */
    public void actualizarProveedor(Proveedores proveedor) throws ExecutionException, InterruptedException {
        validarProveedor(proveedor);
        
        if (!proveedorRepository.existsById(proveedor.getProveedorId())) {
            throw new IllegalArgumentException("El proveedor no existe: " + proveedor.getProveedorId());
        }
        
        proveedorRepository.update(proveedor);
    }

    /**
     * Elimina un proveedor
     */
    public void eliminarProveedor(String proveedorId) throws ExecutionException, InterruptedException {
        if (!proveedorRepository.existsById(proveedorId)) {
            throw new IllegalArgumentException("El proveedor no existe: " + proveedorId);
        }
        
        proveedorRepository.delete(proveedorId);
    }

    /**
     * Obtiene un proveedor por su ID
     */
    public Proveedores obtenerProveedor(String proveedorId) throws ExecutionException, InterruptedException {
        return proveedorRepository.findById(proveedorId);
    }

    /**
     * Lista todos los proveedores
     */
    public List<Proveedores> listarProveedores() throws ExecutionException, InterruptedException {
        return proveedorRepository.findAll();
    }

    /**
     * Valida los datos de un proveedor
     */
    private void validarProveedor(Proveedores proveedor) {
        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio");
        }
        
        if (proveedor.getEmail() == null || proveedor.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email del proveedor es obligatorio");
        }
    }
}

