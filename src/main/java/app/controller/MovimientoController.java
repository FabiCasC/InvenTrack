package app.controller;

import app.model.Movimientos;
import app.service.MovimientoService;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Controller para gestionar operaciones de Movimientos
 * Sigue el patrón MVC: View → Controller → Service → Repository
 */
public class MovimientoController {
    private final MovimientoService movimientoService;

    public MovimientoController() {
        this.movimientoService = new MovimientoService();
    }

    /**
     * Registra una entrada de stock
     */
    public boolean registrarEntrada(String productoId, int cantidad, String usuarioId,
                                   String algoritmo, String loteId, Date fechaVencimiento) {
        try {
            Movimientos movimiento = new Movimientos();
            movimiento.setProductoId(productoId);
            movimiento.setTipo_movimiento("entrada");
            movimiento.setCantidad(cantidad);
            movimiento.setFecha(new Date());
            movimiento.setUsuarioId(usuarioId);
            movimiento.setAlgoritmo(algoritmo);

            Movimientos movimientoRegistrado = movimientoService.registrarMovimiento(movimiento, loteId, fechaVencimiento);
            mostrarExito("Entrada registrada exitosamente.\nID: " + movimientoRegistrado.getMovimientoId());
            return true;

        } catch (Exception e) {
            mostrarError(e.getMessage());
            return false;
        }
    }

    /**
     * Registra una salida de stock
     */
    public boolean registrarSalida(String productoId, int cantidad, String usuarioId,
                                  String algoritmo) {
        try {
            Movimientos movimiento = new Movimientos();
            movimiento.setProductoId(productoId);
            movimiento.setTipo_movimiento("salida");
            movimiento.setCantidad(cantidad);
            movimiento.setFecha(new Date());
            movimiento.setUsuarioId(usuarioId);
            movimiento.setAlgoritmo(algoritmo);

            Movimientos movimientoRegistrado = movimientoService.registrarMovimiento(movimiento, null, null);
            mostrarExito("Salida registrada exitosamente.\nID: " + movimientoRegistrado.getMovimientoId());
            return true;

        } catch (Exception e) {
            mostrarError(e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene un movimiento por su ID
     */
    public Movimientos obtenerMovimiento(String movimientoId) {
        try {
            return movimientoService.obtenerMovimiento(movimientoId);
        } catch (Exception e) {
            mostrarError("Error al obtener movimiento: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista todos los movimientos
     */
    public List<Movimientos> listarMovimientos() {
        try {
            return movimientoService.listarMovimientos();
        } catch (Exception e) {
            mostrarError("Error al listar movimientos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista movimientos por producto
     */
    public List<Movimientos> listarMovimientosPorProducto(String productoId) {
        try {
            return movimientoService.listarMovimientosPorProducto(productoId);
        } catch (Exception e) {
            mostrarError("Error al listar movimientos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista movimientos por rango de fechas
     */
    public List<Movimientos> listarMovimientosPorFechas(Date fechaInicio, Date fechaFin) {
        try {
            return movimientoService.listarMovimientosPorFechas(fechaInicio, fechaFin);
        } catch (Exception e) {
            mostrarError("Error al listar movimientos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista movimientos por tipo
     */
    public List<Movimientos> listarMovimientosPorTipo(String tipoMovimiento) {
        try {
            return movimientoService.listarMovimientosPorTipo(tipoMovimiento);
        } catch (Exception e) {
            mostrarError("Error al listar movimientos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Elimina movimientos de productos que no existen
     * @param mostrarMensajes Si es true, muestra mensajes de éxito/error
     * @return Número de movimientos eliminados
     */
    public int eliminarMovimientosProductosDesconocidos(boolean mostrarMensajes) {
        try {
            int eliminados = movimientoService.eliminarMovimientosProductosDesconocidos();
            if (mostrarMensajes) {
                if (eliminados > 0) {
                    mostrarExito("Se eliminaron " + eliminados + " movimiento(s) de productos desconocidos.");
                } else {
                    mostrarExito("No se encontraron movimientos de productos desconocidos.");
                }
            }
            return eliminados;
        } catch (Exception e) {
            if (mostrarMensajes) {
                mostrarError("Error al eliminar movimientos: " + e.getMessage());
            }
            return 0;
        }
    }
    
    /**
     * Elimina movimientos de productos que no existen (sin mostrar mensajes)
     * @return Número de movimientos eliminados
     */
    public int eliminarMovimientosProductosDesconocidos() {
        return eliminarMovimientosProductosDesconocidos(false);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}

