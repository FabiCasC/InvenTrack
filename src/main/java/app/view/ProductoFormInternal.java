/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package app.view;

import javax.swing.*;
import java.awt.*;

public class ProductoFormInternal extends javax.swing.JInternalFrame {

    public JTextField txtNombreProducto;
    public JComboBox<String> cbTipoProducto;
    public JTextField txtCantidadStock;
    public JTextField txtFechaVencimiento;
    public JTextField txtNumeroLote;
    public JComboBox<String> cbProveedor;

    public JButton btnCancelar;
    public JButton btnGuardar;

    private String modo; // "nuevo" o "editar"

    public ProductoFormInternal(String modo) {
        this.modo = modo;

        setTitle(modo.equals("nuevo") ? "Nuevo Producto" : "Editar Producto");
        setSize(450, 600);
        setClosable(true);
        setIconifiable(true);
        setResizable(false);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //--------------------------------------------
        // TÍTULO
        //--------------------------------------------
        JLabel titulo = new JLabel(
                modo.equals("nuevo") ? "Nuevo Producto" : "Editar Producto"
        );
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Completa la información del producto");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(90, 90, 90));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        main.add(titulo);
        main.add(subtitulo);
        main.add(Box.createVerticalStrut(20));

        //--------------------------------------------
        // CAMPOS DEL FORMULARIO
        //--------------------------------------------
        main.add(createLabel("Nombre del Producto"));
        txtNombreProducto = new JTextField();
        txtNombreProducto.setPreferredSize(new Dimension(300, 35));
        txtNombreProducto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtNombreProducto.setText("");
        txtNombreProducto.putClientProperty("placeholderText", "Ej: Leche Fresca Entera");
        main.add(txtNombreProducto);
        main.add(Box.createVerticalStrut(15));

        main.add(createLabel("Tipo de Producto"));
        cbTipoProducto = new JComboBox<>();
        cbTipoProducto.addItem("Selecciona un tipo");
        cbTipoProducto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        // AQUÍ tu compañera cargará los tipos desde Firestore
        main.add(cbTipoProducto);
        main.add(Box.createVerticalStrut(15));

        main.add(createLabel("Cantidad en Stock"));
        txtCantidadStock = new JTextField("0");
        txtCantidadStock.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        main.add(txtCantidadStock);
        main.add(Box.createVerticalStrut(15));

        main.add(createLabel("Fecha de Vencimiento"));
        txtFechaVencimiento = new JTextField();
        txtFechaVencimiento.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        main.add(txtFechaVencimiento);
        main.add(Box.createVerticalStrut(15));

        main.add(createLabel("Número de Lote"));
        txtNumeroLote = new JTextField();
        txtNumeroLote.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtNumeroLote.putClientProperty("placeholderText", "Ej: LF-2025-001");
        main.add(txtNumeroLote);
        main.add(Box.createVerticalStrut(15));

        main.add(createLabel("Proveedor"));
        cbProveedor = new JComboBox<>();
        cbProveedor.addItem("Selecciona un proveedor");
        cbProveedor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        // AQUÍ tu compañera cargará los proveedores desde Firestore
        main.add(cbProveedor);
        main.add(Box.createVerticalStrut(20));

        //--------------------------------------------
        // BOTONES
        //--------------------------------------------
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(200, 200, 200));
        btnCancelar.setFocusPainted(false);

        btnGuardar = new JButton(
                modo.equals("nuevo") ? "Agregar Producto" : "Guardar Cambios"
        );
        btnGuardar.setBackground(new Color(70, 120, 240));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);

        buttons.add(btnCancelar);
        buttons.add(btnGuardar);

        main.add(buttons);

        add(main);
    }

    //--------------------------------------------
    // LABEL BONITO
    //--------------------------------------------
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
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
