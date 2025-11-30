/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package app.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class PedidoFormInternal extends javax.swing.JInternalFrame {

    public PedidoFormInternal() {

        setTitle("Nuevo Pedido");
        setClosable(true);
        setIconifiable(true);
        setResizable(true);
        setSize(450, 600);
        setTitle("Nuevo Pedido");

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

        JTextField txtDestino = new JTextField();
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

        JTextField txtProducto = new JTextField();
        txtProducto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JTextField txtCantidad = new JTextField();
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

        productRow.add(txtProducto);
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

        String[] columnNames = { "Producto", "Cantidad" };
        Object[][] data = {}; // vacío, solo UI

        JTable table = new JTable(data, columnNames);
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1, true));

        contentPanel.add(scrollPane);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
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
