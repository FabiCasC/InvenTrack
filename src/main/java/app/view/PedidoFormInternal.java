/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package app.view;

import app.controller.PedidoController;
import app.controller.ProductoController;
import app.model.Productos;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidoFormInternal extends javax.swing.JInternalFrame {
    
    private PedidoController pedidoController;
    private ProductoController productoController;
    private JTextField txtDestino;
    private JComboBox<String> comboProducto;
    private JTextField txtCantidad;
    private JTable table;
    private DefaultTableModel tableModel;
    private Map<String, String> productosMap; // nombreCompleto -> productoId
    private Map<String, Productos> productosData; // productoId -> Productos
    private List<Map<String, Object>> productosAgregados; // Lista de productos agregados al pedido

    public PedidoFormInternal() {
        this.pedidoController = new PedidoController();
        this.productoController = new ProductoController();
        this.productosMap = new HashMap<>();
        this.productosData = new HashMap<>();
        this.productosAgregados = new ArrayList<>();

        setTitle("Nuevo Pedido");
        setClosable(true);
        setIconifiable(true);
        setResizable(true);
        setSize(600, 550);

        // ================================
        // PANEL PRINCIPAL
        // ================================
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(248, 248, 248)); // #F8F8F8
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // ================================
        // HEADER
        // ================================
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(248, 248, 248));

        // Títulos
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(248, 248, 248));

        JLabel titleLabel = new JLabel("Nuevo Pedido");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Selecciona productos y cantidades para crear el pedido");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        // Botón Crear Pedido
        JButton btnCrear = new JButton("Crear Pedido");
        btnCrear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCrear.setForeground(Color.WHITE);
        btnCrear.setBackground(new Color(50, 50, 50));
        btnCrear.setBorder(null);
        btnCrear.setFocusPainted(false);
        btnCrear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCrear.setPreferredSize(new Dimension(130, 35));

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnCrear, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ================================
        // CONTENIDO CENTRAL
        // ================================
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(248, 248, 248));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 30, 30, 30));

        // ==== TARJETA CLIENTE DESTINO ====
        JPanel cardDestino = new JPanel();
        cardDestino.setBackground(Color.WHITE);
        cardDestino.setLayout(new BoxLayout(cardDestino, BoxLayout.Y_AXIS));
        cardDestino.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblDestino = new JLabel("Cliente o Destino");
        lblDestino.setFont(new Font("SansSerif", Font.PLAIN, 14));

        txtDestino = new JTextField();
        txtDestino.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        cardDestino.add(lblDestino);
        cardDestino.add(Box.createVerticalStrut(10));
        cardDestino.add(txtDestino);

        contentPanel.add(cardDestino);
        contentPanel.add(Box.createVerticalStrut(20));

        // ==== TARJETA AGREGAR PRODUCTOS ====
        JPanel cardProducto = new JPanel();
        cardProducto.setBackground(Color.WHITE);
        cardProducto.setLayout(new BoxLayout(cardProducto, BoxLayout.Y_AXIS));
        cardProducto.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblProducto = new JLabel("Agregar Productos");
        lblProducto.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel productRow = new JPanel();
        productRow.setBackground(Color.WHITE);
        productRow.setLayout(new BoxLayout(productRow, BoxLayout.X_AXIS));

        comboProducto = new JComboBox<>();
        comboProducto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cargarProductos();

        txtCantidad = new JTextField();
        txtCantidad.setPreferredSize(new Dimension(80, 35));
        txtCantidad.setMaximumSize(new Dimension(80, 35));
        txtCantidad.setHorizontalAlignment(JTextField.CENTER);

        JButton btnAdd = new JButton("+");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBackground(new Color(50, 50, 50));
        btnAdd.setBorder(null);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.setPreferredSize(new Dimension(40, 35));
        btnAdd.setMaximumSize(new Dimension(40, 35));
        
        // Evento para agregar producto a la tabla
        btnAdd.addActionListener(e -> agregarProductoATabla());

        productRow.add(comboProducto);
        productRow.add(Box.createHorizontalStrut(10));
        productRow.add(txtCantidad);
        productRow.add(Box.createHorizontalStrut(10));
        productRow.add(btnAdd);

        cardProducto.add(lblProducto);
        cardProducto.add(Box.createVerticalStrut(10));
        cardProducto.add(productRow);

        contentPanel.add(cardProducto);
        contentPanel.add(Box.createVerticalStrut(20));

        // ==== TABLA DE PRODUCTOS AGREGADOS ====

        String[] columnNames = { "Producto", "Cantidad", "Acción" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Solo la columna de acción es editable
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(30);

        // Cabecera gris
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(230, 230, 230));
        header.setFont(new Font("SansSerif", Font.BOLD, 13));

        // Alternado de filas
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                          boolean isSelected, boolean hasFocus,
                                                          int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                return c;
            }
        });

        // Agregar botón de eliminar en cada fila
        TableCellRenderer buttonRenderer = new ButtonRenderer();
        TableCellEditor buttonEditor = new ButtonEditor(new JCheckBox());
        table.getColumn("Acción").setCellRenderer(buttonRenderer);
        table.getColumn("Acción").setCellEditor(buttonEditor);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1, true));

        contentPanel.add(scrollPane);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Evento para crear pedido
        btnCrear.addActionListener(e -> crearPedido());
    }
    
    private void cargarProductos() {
        try {
            List<Productos> productos = productoController.listarProductos();
            comboProducto.removeAllItems();
            comboProducto.addItem("Selecciona un producto");
            productosMap.clear();
            productosData.clear();
            
            if (productos != null && !productos.isEmpty()) {
                for (Productos p : productos) {
                    String nombreCompleto = p.getNombre() + " (Stock: " + p.getStock_actual() + ")";
                    comboProducto.addItem(nombreCompleto);
                    productosMap.put(nombreCompleto, p.getProductoId());
                    productosData.put(p.getProductoId(), p);
                }
            } else {
                comboProducto.addItem("No hay productos registrados");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar productos: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            comboProducto.addItem("Error al cargar");
        }
    }
    
    private void agregarProductoATabla() {
        String productoSeleccionado = (String) comboProducto.getSelectedItem();
        
        if (productoSeleccionado == null || productoSeleccionado.equals("Selecciona un producto") || 
            productoSeleccionado.equals("No hay productos registrados") || 
            productoSeleccionado.equals("Error al cargar")) {
            JOptionPane.showMessageDialog(this, 
                "Por favor selecciona un producto válido", 
                "Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String cantidadStr = txtCantidad.getText().trim();
        if (cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingresa una cantidad", 
                "Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "La cantidad debe ser mayor a 0", 
                    "Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String productoId = productosMap.get(productoSeleccionado);
            Productos producto = productosData.get(productoId);
            
            if (producto.getStock_actual() < cantidad) {
                JOptionPane.showMessageDialog(this, 
                    "Stock insuficiente. Disponible: " + producto.getStock_actual(), 
                    "Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Agregar a la tabla
            String nombreProducto = producto.getNombre();
            tableModel.addRow(new Object[]{nombreProducto, cantidad, "Eliminar"});
            
            // Guardar en la lista de productos agregados
            Map<String, Object> item = new HashMap<>();
            item.put("productoId", productoId);
            item.put("nombre", nombreProducto);
            item.put("cantidad", cantidad);
            productosAgregados.add(item);
            
            // Limpiar campos
            txtCantidad.setText("");
            comboProducto.setSelectedIndex(0);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "La cantidad debe ser un número válido", 
                "Error", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void crearPedido() {
        // Validar cliente
        String cliente = txtDestino.getText().trim();
        if (cliente.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingresa el nombre del cliente", 
                "Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar que haya productos agregados
        if (productosAgregados.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor agrega al menos un producto al pedido", 
                "Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Crear un pedido por cada producto (o podrías modificar para crear un solo pedido con múltiples productos)
        // Por ahora, crearemos un pedido por cada producto como está diseñado
        boolean todosExitosos = true;
        int exitosos = 0;
        
        for (Map<String, Object> item : productosAgregados) {
            String productoId = (String) item.get("productoId");
            int cantidad = (Integer) item.get("cantidad");
            
            boolean exito = pedidoController.registrarPedido(productoId, cliente, cantidad);
            if (exito) {
                exitosos++;
            } else {
                todosExitosos = false;
            }
        }
        
        if (todosExitosos) {
            JOptionPane.showMessageDialog(this, 
                "Pedido(s) creado(s) exitosamente. Total: " + exitosos, 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Cerrar el formulario
        } else {
            JOptionPane.showMessageDialog(this, 
                "Se crearon " + exitosos + " de " + productosAgregados.size() + " pedidos. " +
                "Revisa los errores anteriores.", 
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // Clases auxiliares para el botón de eliminar en la tabla
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setText("Eliminar");
            setBackground(new Color(220, 53, 69));
            setForeground(Color.WHITE);
            setBorder(null);
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "Eliminar" : value.toString();
            button.setText(label);
            button.setBackground(new Color(220, 53, 69));
            button.setForeground(Color.WHITE);
            isPushed = true;
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Eliminar la fila
                int confirm = JOptionPane.showConfirmDialog(
                    PedidoFormInternal.this,
                    "¿Deseas eliminar este producto del pedido?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    tableModel.removeRow(row);
                    if (row < productosAgregados.size()) {
                        productosAgregados.remove(row);
                    }
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 394, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
