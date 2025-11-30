/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package app.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class ProveedoresInternal extends javax.swing.JInternalFrame {

    public JButton btnNuevoProveedor;
    public JLabel lblTotalProv;
    public JLabel lblActivos;
    public JLabel lblAlertas;
    public JPanel panelProveedores;

    public ProveedoresInternal() {

        // -------- CONFIG GENERAL --------
        setTitle("Proveedores");
        setBorder(null);
        setClosable(true);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 248, 248));
        setSize(1000, 650);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(248, 248, 248));
        add(main, BorderLayout.CENTER);

        // =============================
        // HEADER
        // =============================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(248, 248, 248));
        header.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel titleBox = new JPanel();
        titleBox.setBackground(new Color(248, 248, 248));
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Proveedores");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitle = new JLabel("Gestión de proveedores de productos lácteos");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(subtitle);

        header.add(titleBox, BorderLayout.WEST);
        
        main.add(header, BorderLayout.NORTH);

        // =============================
        // CONTENT
        // =============================
        JPanel content = new JPanel();
        content.setBackground(new Color(248, 248, 248));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 25, 25, 25));
        main.add(content);

        // =============================
        // STATS
        // =============================
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 20, 0));
        statsRow.setBackground(new Color(248, 248, 248));

        lblTotalProv = new JLabel("—");
        lblActivos = new JLabel("—");
        lblAlertas = new JLabel("—");

        statsRow.add(createStatCard("Total Proveedores", lblTotalProv));
        statsRow.add(createStatCard("Proveedores Activos", lblActivos));
        statsRow.add(createStatCard("En Alerta", lblAlertas));
        statsRow.add(createStatCard("Entregas Este Mes", new JLabel("—")));

        content.add(statsRow);
        content.add(Box.createVerticalStrut(20));

        // =============================
        // LISTA DE PROVEEDORES
        // =============================
        panelProveedores = new JPanel();
        panelProveedores.setBackground(new Color(248, 248, 248));
        panelProveedores.setLayout(new BoxLayout(panelProveedores, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(panelProveedores);
        scroll.setBorder(null);

        content.add(scroll);
    }

    // ----------------------------------------
    // TARJETA ESTADÍSTICA
    // ----------------------------------------
    private JPanel createStatCard(String title, JLabel valueLabel) {

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);

        return card;
    }

    // ===============================================================
    // MÉTODO PARA AÑADIR UN PROVEEDOR — SOLO VISTA
    // ===============================================================
    public JPanel addProveedorCard(
            String nombreProveedor,
            String contacto,
            String telefono,
            String correo,
            String direccion,
            List<String> productos,
            String ultimaEntrega,
            int entregasTotales,
            int confiabilidad,
            String estado // “Activo” o “Alerta”
    ) {

        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // =================== IZQUIERDA ===================
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(Color.WHITE);

        JLabel title = new JLabel(nombreProveedor);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel lblContacto = new JLabel("Contacto: " + contacto);
        JLabel lblTel = new JLabel("📞 " + telefono);
        JLabel lblCorreo = new JLabel("✉ " + correo);
        JLabel lblDir = new JLabel("📍 " + direccion);

        lblContacto.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblCorreo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDir.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // CHIPS DE PRODUCTOS
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        chips.setBackground(Color.WHITE);

        for (String p : productos) {
            JLabel chip = new JLabel(p);
            chip.setOpaque(true);
            chip.setBackground(new Color(245, 245, 245));
            chip.setBorder(new EmptyBorder(4, 8, 4, 8));
            chips.add(chip);
        }

        left.add(title);
        left.add(Box.createVerticalStrut(10));
        left.add(lblContacto);
        left.add(lblTel);
        left.add(lblCorreo);
        left.add(lblDir);
        left.add(Box.createVerticalStrut(10));
        left.add(chips);

        // =================== DERECHA ===================
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(Color.WHITE);
        right.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblEntrega = new JLabel("Última Entrega: " + ultimaEntrega);
        JLabel lblTotal = new JLabel("Entregas Totales: " + entregasTotales);
        JLabel lblConf = new JLabel("Confiabilidad: " + confiabilidad + "%");

        lblEntrega.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTotal.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblConf.setFont(new Font("SansSerif", Font.BOLD, 13));

        JLabel chipEstado = new JLabel(estado);
        chipEstado.setOpaque(true);
        chipEstado.setBorder(new EmptyBorder(5, 10, 5, 10));

        if (estado.equalsIgnoreCase("Activo")) {
            chipEstado.setBackground(new Color(37, 213, 98));
            chipEstado.setForeground(Color.WHITE);
        } else {
            chipEstado.setBackground(new Color(255, 204, 153));
        }

        right.add(lblEntrega);
        right.add(lblTotal);
        right.add(lblConf);
        right.add(Box.createVerticalStrut(10));
        right.add(chipEstado);

        // ARMADO
        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);

        panelProveedores.add(card);
        panelProveedores.add(Box.createVerticalStrut(20));
        panelProveedores.revalidate();

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
