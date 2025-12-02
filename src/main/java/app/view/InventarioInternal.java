package app.view;

import app.controller.ProductoController;
import app.controller.ProveedorController;
import app.model.Productos;
import app.model.Proveedores;
import app.utils.ColorConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventarioInternal extends javax.swing.JInternalFrame {

    public JLabel lblTotalProductos;
    public JLabel lblStockTotal;
    public JLabel lblProductosAlerta;

    public JTable tblInventario;
    public JButton btnAgregarProducto;
    public JButton btnEditarProducto;
    public JButton btnEliminarProducto;

    private DefaultTableModel model;
    private ProductoController productoController;
    private ProveedorController proveedorController;
    private Map<String, String> proveedoresMap; // ID -> Nombre
    private Map<Integer, String> filaToProductoId; // Índice de fila -> ProductoId

    public InventarioInternal() {
        setTitle("Inventario");
        setClosable(false);
        setMaximizable(false);
        setIconifiable(false);
        setResizable(false);
        setBorder(null);
        setSize(970, 650);

        this.productoController = new ProductoController();
        this.proveedorController = new ProveedorController();
        this.proveedoresMap = new HashMap<>();
        this.filaToProductoId = new HashMap<>();

        initComponents();
        cargarProveedores();
        cargarProductos(); // RF2.4 - Listar productos
    }

    private void initComponents() {
        JPanel main = new JPanel();
        main.setBackground(ColorConstants.BLANCO_HUMO);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Título
        JLabel titulo = new JLabel("Inventario");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(ColorConstants.GRIS_PIZARRA);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Gestiona tus productos y su información");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        main.add(titulo);
        main.add(subtitulo);
        main.add(Box.createVerticalStrut(15));

        // Métricas
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        metricsPanel.setOpaque(false);

        lblTotalProductos = new JLabel("0");
        lblStockTotal = new JLabel("0");
        lblProductosAlerta = new JLabel("0");

        metricsPanel.add(createMetricCard("Total de Productos", lblTotalProductos, ColorConstants.AZUL_ACERO));
        metricsPanel.add(createMetricCard("Stock Total", lblStockTotal, ColorConstants.VERDE_EXITO));
        metricsPanel.add(createMetricCard("Productos en Alerta", lblProductosAlerta, ColorConstants.AMARILLO_ADVERTENCIA));

        main.add(metricsPanel);
        main.add(Box.createVerticalStrut(15));

        // Barra de acciones
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        btnAgregarProducto = new JButton("+ Agregar Producto");
        btnAgregarProducto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregarProducto.setBackground(ColorConstants.AZUL_ACERO);
        btnAgregarProducto.setForeground(Color.WHITE);
        btnAgregarProducto.setFocusPainted(false);
        btnAgregarProducto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgregarProducto.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        btnEditarProducto = new JButton("Editar");
        btnEditarProducto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEditarProducto.setBackground(ColorConstants.BOTON_EDITAR);
        btnEditarProducto.setForeground(Color.WHITE);
        btnEditarProducto.setFocusPainted(false);
        btnEditarProducto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditarProducto.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        btnEliminarProducto = new JButton("Eliminar");
        btnEliminarProducto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEliminarProducto.setBackground(ColorConstants.BOTON_ELIMINAR);
        btnEliminarProducto.setForeground(Color.WHITE);
        btnEliminarProducto.setFocusPainted(false);
        btnEliminarProducto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminarProducto.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.add(btnAgregarProducto);
        actionsPanel.add(btnEditarProducto);
        actionsPanel.add(btnEliminarProducto);

        topBar.add(actionsPanel, BorderLayout.EAST);
        main.add(topBar);
        main.add(Box.createVerticalStrut(12));

        // Tabla de Inventario - RF2.4
        tblInventario = new JTable();
        model = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID",
                    "Producto",
                    "Categoría",
                    "Stock Actual",
                    "Stock Mínimo",
                    "Proveedor",
                    "Fecha Ingreso"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblInventario.setModel(model);

        // Estilo de tabla
        tblInventario.setRowHeight(40);
        tblInventario.setBackground(ColorConstants.BLANCO_PURO);
        tblInventario.setForeground(ColorConstants.GRIS_PIZARRA);
        tblInventario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblInventario.setSelectionBackground(new Color(0xE0E7FF));
        tblInventario.setSelectionForeground(ColorConstants.GRIS_PIZARRA);
        tblInventario.setGridColor(ColorConstants.GRIS_CLARO);
        tblInventario.setShowGrid(true);
        
        // Ocultar columna ID
        tblInventario.getColumnModel().getColumn(0).setMinWidth(0);
        tblInventario.getColumnModel().getColumn(0).setMaxWidth(0);
        tblInventario.getColumnModel().getColumn(0).setWidth(0);
        
        // Header de tabla
        JTableHeader header = tblInventario.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(0xF3F4F6));
        header.setForeground(ColorConstants.GRIS_PIZARRA);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        // Renderer para filas alternadas
        tblInventario.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? ColorConstants.BLANCO_PURO : ColorConstants.GRIS_TENUE);
                }
                c.setForeground(ColorConstants.GRIS_PIZARRA);
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tblInventario);
        scroll.setBorder(BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO));
        scroll.setBackground(ColorConstants.BLANCO_PURO);
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());

        main.add(scroll);

        // Eventos de botones
        btnAgregarProducto.addActionListener(e -> abrirFormularioNuevo());
        btnEditarProducto.addActionListener(e -> abrirFormularioEditar());
        btnEliminarProducto.addActionListener(e -> eliminarProducto());
        
        add(main);
    }

    private void cargarProveedores() {
        try {
            List<Proveedores> proveedores = proveedorController.listarProveedores();
            proveedoresMap.clear();
            
            if (proveedores != null) {
                for (Proveedores p : proveedores) {
                    proveedoresMap.put(p.getProveedorId(), p.getNombre());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar proveedores: " + e.getMessage());
        }
    }

    private void cargarProductos() {
        try {
            // RF2.4 - Listar productos desde Firebase
            List<Productos> productos = productoController.listarProductos();
            
            // Limpiar tabla
            model.setRowCount(0);
            filaToProductoId.clear();
            
            int totalProductos = 0;
            int stockTotal = 0;
            int productosAlerta = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            if (productos != null && !productos.isEmpty()) {
                for (Productos producto : productos) {
                    String productoId = producto.getProductoId();
                    String nombre = producto.getNombre();
                    String categoria = producto.getTipo() != null ? producto.getTipo() : "N/A";
                    int stockActual = producto.getStock_actual();
                    int stockMinimo = producto.getStock_minimo();
                    String proveedorNombre = proveedoresMap.getOrDefault(producto.getProveedorId(), "N/A");
                    String fechaIngreso = producto.getFechaIngreso() != null ? 
                        sdf.format(producto.getFechaIngreso()) : "N/A";
                    
                    // Agregar a la tabla
                    int rowIndex = model.getRowCount();
                    model.addRow(new Object[]{
                        productoId, // ID oculto
                        nombre,
                        categoria,
                        stockActual,
                        stockMinimo,
                        proveedorNombre,
                        fechaIngreso
                    });
                    
                    // Guardar mapeo
                    filaToProductoId.put(rowIndex, productoId);
                    
                    // Actualizar métricas
                    totalProductos++;
                    stockTotal += stockActual;
                    if (stockActual <= stockMinimo) {
                        productosAlerta++;
                    }
                }
            }
            
            // Actualizar métricas
            lblTotalProductos.setText(String.valueOf(totalProductos));
            lblStockTotal.setText(String.valueOf(stockTotal));
            lblProductosAlerta.setText(String.valueOf(productosAlerta));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar productos: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirFormularioNuevo() {
        ProductoFormInternal form = new ProductoFormInternal("nuevo", model);
        form.setSize(600, 650);
        form.setLocation(
            (getDesktopPane().getWidth() - form.getWidth()) / 2,
            (getDesktopPane().getHeight() - form.getHeight()) / 2
        );
        getDesktopPane().add(form);
        form.setVisible(true);
        
        // Agregar listener para recargar después de guardar
        form.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                cargarProductos(); // RF2.4 - Recargar lista
                cargarProveedores(); // Recargar proveedores por si hay nuevos
            }
        });
    }

    private void abrirFormularioEditar() {
        // RF2.2 - Editar producto
        int selectedRow = tblInventario.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, selecciona un producto para editar.", 
                "Sin selección", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String productoId = filaToProductoId.get(selectedRow);
        if (productoId == null) {
            JOptionPane.showMessageDialog(this, 
                "Error: No se pudo obtener el ID del producto.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Productos producto = productoController.obtenerProducto(productoId);
            if (producto == null) {
                JOptionPane.showMessageDialog(this, 
                    "Producto no encontrado.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            ProductoFormInternal form = new ProductoFormInternal("editar", model);
            form.cargarDatosProducto(producto);
            form.setSize(600, 650);
            form.setLocation(
                (getDesktopPane().getWidth() - form.getWidth()) / 2,
                (getDesktopPane().getHeight() - form.getHeight()) / 2
            );
            getDesktopPane().add(form);
            form.setVisible(true);
            
            // Recargar después de editar
            form.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                    cargarProductos(); // RF2.4 - Recargar lista
                }
            });
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar producto: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarProducto() {
        // RF2.3 - Eliminar producto
        int selectedRow = tblInventario.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, selecciona un producto para eliminar.", 
                "Sin selección", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String productoId = filaToProductoId.get(selectedRow);
        if (productoId == null) {
            JOptionPane.showMessageDialog(this, 
                "Error: No se pudo obtener el ID del producto.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // RF2.3 - Confirmar eliminación
        int opcion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea eliminar este producto de Firebase?\n\n" +
            "Esta acción no se puede deshacer.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = productoController.eliminarProducto(productoId);
                if (exito) {
                    cargarProductos(); // RF2.4 - Recargar lista
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar producto: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createMetricCard(String title, JLabel dynamic, Color accentColor) {
        JPanel card = new JPanel();
        card.setBackground(ColorConstants.BLANCO_PURO);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, accentColor),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            )
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        dynamic.setFont(new Font("Segoe UI", Font.BOLD, 28));
        dynamic.setForeground(accentColor);
        dynamic.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(5));
        card.add(dynamic);

        return card;
    }

}
