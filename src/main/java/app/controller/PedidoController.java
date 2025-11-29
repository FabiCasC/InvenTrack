package app.controller;

import app.model.Pedidos;
import app.service.PedidoService;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Controller para gestionar operaciones de Pedidos
 * Sigue el patrón MVC: View → Controller → Service → Repository
 */
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController() {
        this.pedidoService = new PedidoService();
    }

    /**
     * Registra un nuevo pedido
     */
    public boolean registrarPedido(String productoId, String cliente, int cantidadSolicitada) {
        try {
            Pedidos pedido = new Pedidos();
            pedido.setProductoId(productoId);
            pedido.setCliente(cliente);
            pedido.setCantidadSolicitada(cantidadSolicitada);
            pedido.setFecha(new Date());
            pedido.setEstado("Pendiente");

            Pedidos pedidoRegistrado = pedidoService.registrarPedido(pedido);
            mostrarExito("Pedido registrado exitosamente.\nID: " + pedidoRegistrado.getPedidoId());
            return true;

        } catch (Exception e) {
            mostrarError(e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el estado de un pedido
     */
    public boolean actualizarEstadoPedido(String pedidoId, String nuevoEstado) {
        try {
            if (!esEstadoValido(nuevoEstado)) {
                mostrarError("Estado inválido. Estados válidos: Pendiente, En proceso, Entregado, Cancelado");
                return false;
            }

            pedidoService.actualizarEstado(pedidoId, nuevoEstado);
            mostrarExito("Estado del pedido actualizado exitosamente");
            return true;

        } catch (Exception e) {
            mostrarError(e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene un pedido por su ID
     */
    public Pedidos obtenerPedido(String pedidoId) {
        try {
            return pedidoService.obtenerPedido(pedidoId);
        } catch (Exception e) {
            mostrarError("Error al obtener pedido: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista todos los pedidos
     */
    public List<Pedidos> listarPedidos() {
        try {
            return pedidoService.listarPedidos();
        } catch (Exception e) {
            mostrarError("Error al listar pedidos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista pedidos por producto
     */
    public List<Pedidos> listarPedidosPorProducto(String productoId) {
        try {
            return pedidoService.listarPedidosPorProducto(productoId);
        } catch (Exception e) {
            mostrarError("Error al listar pedidos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista pedidos por estado
     */
    public List<Pedidos> listarPedidosPorEstado(String estado) {
        try {
            return pedidoService.listarPedidosPorEstado(estado);
        } catch (Exception e) {
            mostrarError("Error al listar pedidos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si un estado es válido
     */
    private boolean esEstadoValido(String estado) {
        return estado != null && (
            estado.equalsIgnoreCase("Pendiente") ||
            estado.equalsIgnoreCase("En proceso") ||
            estado.equalsIgnoreCase("Entregado") ||
            estado.equalsIgnoreCase("Cancelado")
        );
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}

