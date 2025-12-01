package app.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InventarioInternal extends javax.swing.JInternalFrame {

    public JLabel lblTotalProductos;
    public JLabel lblStockTotal;
    public JLabel lblProductosAlerta;

    public JTable tblInventario;
    public JButton btnAgregarProducto;

    public InventarioInternal() {

        setTitle("Inventario");
        setClosable(false);
        setMaximizable(false);
        setIconifiable(false);
        setResizable(false);
        setBorder(null);
        setSize(900, 650);

        JPanel main = new JPanel();
        main.setBackground(new Color(245, 245, 245));
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Inventario");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(new Color(50, 50, 50));

        JLabel subtitulo = new JLabel("Gestiona tus productos y su información");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitulo.setForeground(new Color(90, 90, 90));

        main.add(titulo);
        main.add(subtitulo);
        main.add(Box.createVerticalStrut(20));

        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        metricsPanel.setOpaque(false);

        lblTotalProductos = new JLabel("—");
        lblStockTotal = new JLabel("—");
        lblProductosAlerta = new JLabel("—");

        metricsPanel.add(createMetricCard("Total de Productos", lblTotalProductos));
        metricsPanel.add(createMetricCard("Stock Total", lblStockTotal));
        metricsPanel.add(createMetricCard("Productos en Alerta", lblProductosAlerta));

        main.add(metricsPanel);
        main.add(Box.createVerticalStrut(20));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        btnAgregarProducto = new JButton("Agregar Producto");
        btnAgregarProducto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregarProducto.setBackground(new Color(70, 120, 240));
        btnAgregarProducto.setForeground(Color.WHITE);
        
        JButton btnEditarProducto = new JButton("Editar");
        JButton btnEliminarProducto = new JButton("Eliminar");

        btnEditarProducto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEliminarProducto.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnEditarProducto.setBackground(new Color(255, 185, 0));
        btnEliminarProducto.setBackground(new Color(255, 70, 70));

        btnEditarProducto.setForeground(Color.WHITE);
        btnEliminarProducto.setForeground(Color.WHITE);

        btnEditarProducto.setFocusPainted(false);
        btnEliminarProducto.setFocusPainted(false);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        actionsPanel.add(btnAgregarProducto);
        actionsPanel.add(btnEditarProducto);
        actionsPanel.add(btnEliminarProducto);
        
        btnAgregarProducto.setFocusPainted(false);

        topBar.add(btnAgregarProducto, BorderLayout.EAST);

        main.add(topBar);
        main.add(Box.createVerticalStrut(15));

        tblInventario = new JTable();

        tblInventario.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Producto",
                    "Tipo",
                    "Stock",
                    "Vencimiento",
                    "Lote",
                    "Proveedor",
                    "Acciones"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        tblInventario.setRowHeight(35);
        tblInventario.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(tblInventario);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        main.add(scroll);

        DefaultTableModel model = (DefaultTableModel) tblInventario.getModel();

        btnAgregarProducto.addActionListener(e -> {
            ProductoFormInternal form = new ProductoFormInternal("nuevo", model);
            getDesktopPane().add(form);
            form.setVisible(true);
        });

        btnEditarProducto.addActionListener(e -> {
            ProductoFormInternal form = new ProductoFormInternal("editar", model); // No necesita el modelo para editar, pero lo mantenemos para consistencia
            getDesktopPane().add(form);
            form.setVisible(true);
        });

        btnEliminarProducto.addActionListener(e -> {
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Seguro que deseas eliminar este producto?",
                    "Confirmación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (opcion == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
            }
        });
        
        add(main);
    }

    private JPanel createMetricCard(String title, JLabel dynamic) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(new Color(100, 100, 100));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        dynamic.setFont(new Font("Segoe UI", Font.BOLD, 28));
        dynamic.setForeground(new Color(40, 40, 40));
        dynamic.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lbl);
        card.add(dynamic);

        return card;
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
