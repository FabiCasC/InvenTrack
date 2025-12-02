package app.view;

import app.controller.ReporteController;
import app.model.ReporteInventarioCritico;
import app.utils.ColorConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;

/**
 * RF8.1 - Vista para reporte de inventario crítico
 */
public class ReporteInventarioCriticoInternal extends javax.swing.JInternalFrame {
    
    private ReporteController reporteController;
    private DefaultTableModel tableModel;
    private JTable tablaReporte;
    private ReporteInventarioCritico reporteActual;
    
    public ReporteInventarioCriticoInternal() {
        setTitle("Reporte de Inventario Crítico (RF8.1)");
        setClosable(true);
        setResizable(false);
        setSize(970, 650);
        setBorder(null);
        
        this.reporteController = new ReporteController();
        
        initComponents();
        generarReporte();
    }
    
    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(ColorConstants.BLANCO_HUMO);
        main.setBorder(new EmptyBorder(15, 20, 20, 20));
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel title = new JLabel("Reporte de Inventario Crítico");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ColorConstants.GRIS_PIZARRA);
        
        JLabel subtitle = new JLabel("RF8.1 - Productos con stock bajo o próximos a vencer");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(subtitle);
        
        // Botones
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        
        JButton btnGenerar = new JButton("🔄 Generar");
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
        main.add(Box.createVerticalStrut(15), BorderLayout.CENTER);
        
        // Tabla
        String[] columnas = {"ID", "Nombre", "Tipo", "Stock Actual", "Stock Mínimo", 
                            "Proveedor", "Nivel Crítico", "Motivo", "Días Vencimiento"};
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
        tablaReporte.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Renderer para filas alternadas
        tablaReporte.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? ColorConstants.BLANCO_PURO : ColorConstants.GRIS_TENUE);
                }
                
                // Colorear según nivel crítico
                String nivelCritico = (String) tableModel.getValueAt(row, 6);
                if (nivelCritico != null && nivelCritico.equals("CRÍTICO")) {
                    c.setBackground(new Color(255, 235, 238));
                } else if (nivelCritico != null && nivelCritico.equals("ALERTA")) {
                    c.setBackground(new Color(255, 248, 225));
                }
                
                c.setForeground(ColorConstants.GRIS_PIZARRA);
                return c;
            }
        });
        
        JScrollPane scroll = new JScrollPane(tablaReporte);
        scroll.setBorder(BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO));
        
        main.add(scroll, BorderLayout.CENTER);
        
        add(main);
    }
    
    private void generarReporte() {
        try {
            reporteActual = reporteController.generarReporteInventarioCritico();
            
            if (reporteActual == null) {
                JOptionPane.showMessageDialog(this, "Error al generar el reporte", "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Limpiar tabla
            tableModel.setRowCount(0);
            
            // Llenar tabla
            if (reporteActual.getProductosCriticos() != null) {
                for (ReporteInventarioCritico.ItemInventarioCritico item : reporteActual.getProductosCriticos()) {
                    tableModel.addRow(new Object[]{
                        item.getProductoId(),
                        item.getNombre(),
                        item.getTipo(),
                        item.getStockActual(),
                        item.getStockMinimo(),
                        item.getProveedor(),
                        item.getNivelCritico(),
                        item.getMotivo(),
                        item.getDiasParaVencimiento() > 0 ? item.getDiasParaVencimiento() : "N/A"
                    });
                }
            }
            
            // Mostrar resumen
            String mensaje = String.format(
                "Reporte generado:\n" +
                "Total de productos: %d\n" +
                "Productos críticos: %d\n" +
                "- Con stock bajo: %d\n" +
                "- Próximos a vencer: %d",
                reporteActual.getTotalProductos(),
                reporteActual.getProductosCriticos().size(),
                reporteActual.getProductosStockBajo(),
                reporteActual.getProductosPorVencer()
            );
            
            JOptionPane.showMessageDialog(this, mensaje, "Reporte Generado", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportarCSV() {
        if (reporteActual == null || reporteActual.getProductosCriticos() == null || 
            reporteActual.getProductosCriticos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar. Genere el reporte primero.", 
                "Sin datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte como CSV");
        fileChooser.setSelectedFile(new java.io.File("reporte_inventario_critico.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                // Encabezados
                writer.append("ID,Nombre,Tipo,Stock Actual,Stock Mínimo,Stock Máximo," +
                             "Proveedor,Nivel Crítico,Motivo,Días Vencimiento\n");
                
                // Datos
                for (ReporteInventarioCritico.ItemInventarioCritico item : reporteActual.getProductosCriticos()) {
                    writer.append(String.format("\"%s\",\"%s\",\"%s\",%d,%d,%d,\"%s\",\"%s\",\"%s\",%d\n",
                        item.getProductoId(),
                        item.getNombre(),
                        item.getTipo(),
                        item.getStockActual(),
                        item.getStockMinimo(),
                        item.getStockMaximo(),
                        item.getProveedor(),
                        item.getNivelCritico(),
                        item.getMotivo(),
                        item.getDiasParaVencimiento()
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

