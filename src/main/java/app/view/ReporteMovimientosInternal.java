package app.view;

import app.controller.ReporteController;
import app.model.ReporteMovimientos;
import app.utils.ColorConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;

/**
 * RF8.3 - Vista para reporte de movimientos
 */
public class ReporteMovimientosInternal extends javax.swing.JInternalFrame {
    
    private ReporteController reporteController;
    private DefaultTableModel tableModel;
    private JTable tablaReporte;
    private ReporteMovimientos reporteActual;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JComboBox<String> cbTipoFiltro;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    public ReporteMovimientosInternal() {
        setTitle("Reporte de Movimientos (RF8.3)");
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
        
        JLabel title = new JLabel("Reporte de Movimientos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ColorConstants.GRIS_PIZARRA);
        
        JLabel subtitle = new JLabel("RF8.3 - Tabla filtrable y exportable de movimientos");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(subtitle);
        
        // Panel de filtros
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filtros.setOpaque(false);
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        
        JLabel lblFechaInicio = new JLabel("Fecha Inicio:");
        lblFechaInicio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFechaInicio = new JTextField(12);
        txtFechaInicio.setToolTipText("Formato: dd/MM/yyyy");
        txtFechaInicio.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        JLabel lblFechaFin = new JLabel("Fecha Fin:");
        lblFechaFin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFechaFin = new JTextField(12);
        txtFechaFin.setToolTipText("Formato: dd/MM/yyyy");
        txtFechaFin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTipoFiltro = new JComboBox<>(new String[]{"Todos", "Entrada", "Salida"});
        cbTipoFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        filtros.add(lblFechaInicio);
        filtros.add(txtFechaInicio);
        filtros.add(lblFechaFin);
        filtros.add(txtFechaFin);
        filtros.add(lblTipo);
        filtros.add(cbTipoFiltro);
        
        // Botones
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        
        JButton btnGenerar = new JButton("🔍 Generar Reporte");
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
        
        // Tabla
        String[] columnas = {"ID", "Producto", "Tipo", "Cantidad", "Fecha", "Algoritmo", "Usuario"};
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
        
        // Renderer para filas alternadas y por tipo
        tablaReporte.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String tipo = (String) tableModel.getValueAt(row, 2);
                    if (tipo != null && tipo.equalsIgnoreCase("entrada")) {
                        c.setBackground(new Color(235, 255, 238));
                    } else if (tipo != null && tipo.equalsIgnoreCase("salida")) {
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
        contentPanel.add(filtros, BorderLayout.NORTH);
        contentPanel.add(scroll, BorderLayout.CENTER);
        
        main.add(contentPanel, BorderLayout.CENTER);
        
        add(main);
    }
    
    private void generarReporte() {
        try {
            Date fechaInicio = null;
            Date fechaFin = null;
            
            if (!txtFechaInicio.getText().trim().isEmpty()) {
                try {
                    fechaInicio = sdf.parse(txtFechaInicio.getText().trim());
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this, "Fecha de inicio inválida. Use formato dd/MM/yyyy", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (!txtFechaFin.getText().trim().isEmpty()) {
                try {
                    fechaFin = sdf.parse(txtFechaFin.getText().trim());
                    // Ajustar al final del día
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(fechaFin);
                    cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
                    cal.set(java.util.Calendar.MINUTE, 59);
                    cal.set(java.util.Calendar.SECOND, 59);
                    fechaFin = cal.getTime();
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this, "Fecha de fin inválida. Use formato dd/MM/yyyy", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (fechaInicio != null && fechaFin != null && fechaInicio.after(fechaFin)) {
                JOptionPane.showMessageDialog(this, "La fecha de inicio debe ser anterior a la fecha de fin.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String tipoFiltro = (String) cbTipoFiltro.getSelectedItem();
            String tipoFiltroFinal = tipoFiltro.equals("Todos") ? null : tipoFiltro;
            
            reporteActual = reporteController.generarReporteMovimientos(fechaInicio, fechaFin, tipoFiltroFinal);
            
            if (reporteActual == null) {
                JOptionPane.showMessageDialog(this, "Error al generar el reporte", "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Limpiar tabla
            tableModel.setRowCount(0);
            
            // Llenar tabla
            if (reporteActual.getMovimientos() != null) {
                for (ReporteMovimientos.ItemMovimiento item : reporteActual.getMovimientos()) {
                    tableModel.addRow(new Object[]{
                        item.getMovimientoId(),
                        item.getNombreProducto(),
                        item.getTipo(),
                        item.getCantidad(),
                        item.getFecha(),
                        item.getAlgoritmo(),
                        item.getUsuarioId()
                    });
                }
            }
            
            // Mostrar resumen
            if (reporteActual.getResumen() != null) {
                ReporteMovimientos.ResumenMovimientos resumen = reporteActual.getResumen();
                String mensaje = String.format(
                    "Reporte generado:\n" +
                    "Período: %s a %s\n" +
                    "Total movimientos: %d\n" +
                    "Entradas: %d (%d unidades)\n" +
                    "Salidas: %d (%d unidades)",
                    reporteActual.getFechaInicio(),
                    reporteActual.getFechaFin(),
                    resumen.getTotalMovimientos(),
                    resumen.getTotalEntradas(),
                    resumen.getCantidadTotalEntradas(),
                    resumen.getTotalSalidas(),
                    resumen.getCantidadTotalSalidas()
                );
                
                JOptionPane.showMessageDialog(this, mensaje, "Reporte Generado", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportarCSV() {
        if (reporteActual == null || reporteActual.getMovimientos() == null || 
            reporteActual.getMovimientos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar. Genere el reporte primero.", 
                "Sin datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte de movimientos como CSV");
        fileChooser.setSelectedFile(new java.io.File("reporte_movimientos.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                // Encabezados
                writer.append("ID,Producto,Tipo,Cantidad,Fecha,Algoritmo,Usuario\n");
                
                // Datos
                for (ReporteMovimientos.ItemMovimiento item : reporteActual.getMovimientos()) {
                    writer.append(String.format("\"%s\",\"%s\",\"%s\",%d,\"%s\",\"%s\",\"%s\"\n",
                        item.getMovimientoId(),
                        item.getNombreProducto(),
                        item.getTipo(),
                        item.getCantidad(),
                        item.getFecha(),
                        item.getAlgoritmo(),
                        item.getUsuarioId()
                    ));
                }
                
                // Agregar resumen al final
                if (reporteActual.getResumen() != null) {
                    writer.append("\nRESUMEN\n");
                    writer.append(String.format("Total Movimientos,%d\n", reporteActual.getResumen().getTotalMovimientos()));
                    writer.append(String.format("Entradas,%d,%d\n", reporteActual.getResumen().getTotalEntradas(), 
                        reporteActual.getResumen().getCantidadTotalEntradas()));
                    writer.append(String.format("Salidas,%d,%d\n", reporteActual.getResumen().getTotalSalidas(),
                        reporteActual.getResumen().getCantidadTotalSalidas()));
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

