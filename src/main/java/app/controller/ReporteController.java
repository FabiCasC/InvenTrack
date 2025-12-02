package app.controller;

import app.model.ReporteInventarioCritico;
import app.model.ReporteMovimientos;
import app.model.ReportePrediccion;
import app.service.ReporteService;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 * Controller para gestionar reportes (RF8)
 */
public class ReporteController {
    private final ReporteService reporteService;
    
    public ReporteController() {
        this.reporteService = new ReporteService();
    }
    
    /**
     * RF8.1 - Genera reporte de inventario crítico
     */
    public ReporteInventarioCritico generarReporteInventarioCritico() {
        try {
            return reporteService.generarReporteInventarioCritico();
        } catch (Exception e) {
            mostrarError("Error al generar reporte de inventario crítico: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * RF8.2 - Genera reporte de predicción
     */
    public ReportePrediccion generarReportePrediccion(int diasProyeccion) {
        try {
            return reporteService.generarReportePrediccion(diasProyeccion);
        } catch (Exception e) {
            mostrarError("Error al generar reporte de predicción: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * RF8.3 - Genera reporte de movimientos
     */
    public ReporteMovimientos generarReporteMovimientos(Date fechaInicio, Date fechaFin, String tipoFiltro) {
        try {
            return reporteService.generarReporteMovimientos(fechaInicio, fechaFin, tipoFiltro);
        } catch (Exception e) {
            mostrarError("Error al generar reporte de movimientos: " + e.getMessage());
            return null;
        }
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

