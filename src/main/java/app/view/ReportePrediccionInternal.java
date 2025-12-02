package app.view;

import app.controller.ReporteController;
import app.model.ReportePrediccion;
import app.utils.ColorConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;

/**
 * RF8.2 - Vista para reporte de predicción
 */
public class ReportePrediccionInternal extends javax.swing.JInternalFrame {
    
    private ReporteController reporteController;
    private DefaultTableModel tableModel;
    private JTable tablaReporte;
    private ReportePrediccion reporteActual;
    private JSpinner spinnerDias;
    
    public ReportePrediccionInternal() {
        setTitle("Reporte de Predicción (RF8.2)");
        setClosable(true);
        setResizable(false);
        setSize(970, 650);
        setBorder(null);
        
        this.reporteController = new ReporteController();
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(ColorConstants.BLANCO_HUMO);
        main.setBorder(new EmptyBorder(15, 20, 20, 20));
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel title = new JLabel("Reporte de Predicción");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ColorConstants.GRIS_PIZARRA);
        
        JLabel subtitle = new JLabel("RF8.2 - Predicción de demanda basada en árbol de decisión");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(subtitle);
        
        // Panel de controles
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controls.setOpaque(false);
        controls.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel lblDias = new JLabel("Días de proyección:");
        lblDias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinnerDias = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        spinnerDias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinnerDias.setPreferredSize(new Dimension(80, 30));
        
        controls.add(lblDias);
        controls.add(spinnerDias);
        
        // Botones
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        
        JButton btnGenerar = new JButton("🔮 Generar Predicción");
        btnGenerar.setBackground(ColorConstants.AZUL_ACERO);
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGenerar.setFocusPainted(false);
        btnGenerar.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnGenerar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGenerar.addActionListener(e -> generarReporte());
        
        JButton btnExportar = new JButton("📥 Exportar CSV");
        btnExportar.setBackground(ColorConstants.VERDE_EXITO);
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExportar.setFocusPainted(false);
        btnExportar.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnExportar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportar.addActionListener(e -> exportarCSV());
        
        buttons.add(btnGenerar);
        buttons.add(btnExportar);
        
        header.add(titleBox, BorderLayout.WEST);
        header.add(buttons, BorderLayout.EAST);
        
        main.add(header, BorderLayout.NORTH);
        main.add(controls, BorderLayout.CENTER);
        
        // Tabla
        String[] columnas = {"ID", "Nombre", "Stock Actual", "Stock Proyectado", 
                            "Predicción", "Riesgo", "Confianza", "Razón"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaReporte = new JTable(tableModel);
        tablaReporte.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaReporte.setRowHeight(25);
        tablaReporte.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaReporte.getTableHeader().setBackground(ColorConstants.GRIS_PIZARRA);
        tablaReporte.getTableHeader().setForeground(Color.WHITE);
        
        // Renderer para filas con colores según riesgo
        tablaReporte.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String riesgo = (String) tableModel.getValueAt(row, 5);
                    if (riesgo != null && riesgo.equals("ALTO")) {
                        c.setBackground(new Color(255, 235, 238));
                    } else if (riesgo != null && riesgo.equals("MEDIO")) {
                        c.setBackground(new Color(255, 248, 225));
                    } else {
                        c.setBackground(row % 2 == 0 ? ColorConstants.BLANCO_PURO : ColorConstants.GRIS_TENUE);
                    }
                }
                
                c.setForeground(ColorConstants.GRIS_PIZARRA);
                return c;
            }
        });
        
        JScrollPane scroll = new JScrollPane(tablaReporte);
        scroll.setBorder(BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO));
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(controls, BorderLayout.NORTH);
        contentPanel.add(scroll, BorderLayout.CENTER);
        
        main.add(header, BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);
        
        add(main);
    }
    
    private void generarReporte() {
        try {
            int dias = (Integer) spinnerDias.getValue();
            reporteActual = reporteController.generarReportePrediccion(dias);
            
            if (reporteActual == null) {
                JOptionPane.showMessageDialog(this, "Error al generar el reporte", "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Limpiar tabla
            tableModel.setRowCount(0);
            
            // Llenar tabla
            if (reporteActual.getPredicciones() != null) {
                for (ReportePrediccion.PrediccionProducto pred : reporteActual.getPredicciones()) {
                    tableModel.addRow(new Object[]{
                        pred.getProductoId(),
                        pred.getNombre(),
                        pred.getStockActual(),
                        pred.getStockProyectado(),
                        pred.getPrediccion(),
                        pred.getRiesgo(),
                        String.format("%.0f%%", pred.getConfianza() * 100),
                        pred.getRazon() != null ? pred.getRazon() : "N/A"
                    });
                }
            }
            
            // Mostrar resumen
            String mensaje = String.format(
                "Reporte de predicción generado:\n" +
                "Fecha: %s\n" +
                "Productos analizados: %d\n" +
                "Días de proyección: %d",
                reporteActual.getFechaGeneracion(),
                reporteActual.getTotalProductosAnalizados(),
                dias
            );
            
            JOptionPane.showMessageDialog(this, mensaje, "Predicción Generada", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar predicción: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportarCSV() {
        if (reporteActual == null || reporteActual.getPredicciones() == null || 
            reporteActual.getPredicciones().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar. Genere el reporte primero.", 
                "Sin datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte de predicción como CSV");
        fileChooser.setSelectedFile(new java.io.File("reporte_prediccion.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                // Encabezados
                writer.append("ID,Nombre,Stock Actual,Stock Proyectado,Predicción,Riesgo,Confianza,Razón\n");
                
                // Datos
                for (ReportePrediccion.PrediccionProducto pred : reporteActual.getPredicciones()) {
                    writer.append(String.format("\"%s\",\"%s\",%d,%d,\"%s\",\"%s\",%.2f,\"%s\"\n",
                        pred.getProductoId(),
                        pred.getNombre(),
                        pred.getStockActual(),
                        pred.getStockProyectado(),
                        pred.getPrediccion(),
                        pred.getRiesgo(),
                        pred.getConfianza(),
                        pred.getRazon() != null ? pred.getRazon().replace("\"", "\"\"") : "N/A"
                    ));
                }
                
                JOptionPane.showMessageDialog(this, "Reporte exportado exitosamente.", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

