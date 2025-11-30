/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package app.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class PedidosInternal extends javax.swing.JInternalFrame {

    public JButton btnNuevoPedido;

    public JPanel panelPendientes;
    public JPanel panelHistorial;

    public JLabel lblTotalPedidos;
    public JLabel lblPendientes;
    public JLabel lblCompletados;

    public PedidosInternal() {

        setTitle("Pedidos");
        setBorder(null);
        setClosable(true);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(248, 248, 248));
        setSize(900, 650);

        // PANEL PRINCIPAL
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 248, 248));
        add(mainPanel, BorderLayout.CENTER);

        // ============================
        // HEADER SUPERIOR
        // ============================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 248, 248));
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(248, 248, 248));
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Pedidos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitleLabel = new JLabel("Gestiona pedidos a tiendas y clientes");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        btnNuevoPedido = new JButton(" +  Crear Pedido");
        btnNuevoPedido.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNuevoPedido.setForeground(Color.WHITE);
        btnNuevoPedido.setBackground(new Color(50, 50, 50));
        btnNuevoPedido.setBorder(null);
        btnNuevoPedido.setFocusPainted(false);
        btnNuevoPedido.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevoPedido.setPreferredSize(new Dimension(140, 40));
        
        // EVENTO: CREAR PEDIDO
        btnNuevoPedido.addActionListener(e -> {
            ProductoFormInternal form = new ProductoFormInternal("nuevo");
            getDesktopPane().add(form);
            form.setVisible(true);
        });

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnNuevoPedido, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ============================
        // CONTENT PANEL
        // ============================
        JPanel content = new JPanel();
        content.setBackground(new Color(248, 248, 248));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 25, 25, 25));

        mainPanel.add(content, BorderLayout.CENTER);

        // ============================
        // ESTADISTICAS
        // ============================
        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setBackground(new Color(248, 248, 248));

        JPanel card1 = createStatCard("Total Pedidos");
        lblTotalPedidos = (JLabel) card1.getComponent(2);

        JPanel card2 = createStatCard("Pendientes");
        lblPendientes = (JLabel) card2.getComponent(2);

        JPanel card3 = createStatCard("Completados");
        lblCompletados = (JLabel) card3.getComponent(2);

        stats.add(card1);
        stats.add(card2);
        stats.add(card3);

        content.add(stats);
        content.add(Box.createVerticalStrut(20));

        // ============================================================
        // *** PEDIDOS PENDIENTES ***
        // ============================================================
        JPanel cardPendientes = new JPanel(new BorderLayout());
        cardPendientes.setBackground(Color.WHITE);
        cardPendientes.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblPend = new JLabel("Pedidos Pendientes");
        lblPend.setFont(new Font("Segoe UI", Font.BOLD, 16));

        cardPendientes.add(lblPend, BorderLayout.NORTH);

        panelPendientes = new JPanel();
        panelPendientes.setBackground(Color.WHITE);
        panelPendientes.setLayout(new BoxLayout(panelPendientes, BoxLayout.Y_AXIS));

        JScrollPane scrollPend = new JScrollPane(panelPendientes);
        scrollPend.setBorder(null);

        cardPendientes.add(scrollPend, BorderLayout.CENTER);

        content.add(cardPendientes);
        content.add(Box.createVerticalStrut(25));

        // ============================================================
        // *** HISTORIAL DE PEDIDOS ***
        // ============================================================
        JPanel cardHistorial = new JPanel(new BorderLayout());
        cardHistorial.setBackground(Color.WHITE);
        cardHistorial.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblHist = new JLabel("Historial de Pedidos");
        lblHist.setFont(new Font("Segoe UI", Font.BOLD, 16));

        cardHistorial.add(lblHist, BorderLayout.NORTH);

        panelHistorial = new JPanel();
        panelHistorial.setBackground(Color.WHITE);
        panelHistorial.setLayout(new BoxLayout(panelHistorial, BoxLayout.Y_AXIS));

        JScrollPane scrollHist = new JScrollPane(panelHistorial);
        scrollHist.setBorder(null);

        cardHistorial.add(scrollHist, BorderLayout.CENTER);

        content.add(cardHistorial);
    }

    // =========================================================
    // TARJETA DE ESTADÍSTICAS
    // =========================================================
    private JPanel createStatCard(String title) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);

        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValue = new JLabel("—");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(5));
        card.add(lblValue);

        return card;
    }

    // =========================================================
    // MÉTODO PARA AGREGAR PEDIDOS PENDIENTES
    // =========================================================
    public JPanel addPedidoPendiente(String codigo, String cliente, String fecha, String detalle, int total) {

        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Color.WHITE);
        item.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // ——— TOP
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel lblCod = new JLabel(codigo);
        lblCod.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel chip = new JLabel("Pendiente");
        chip.setOpaque(true);
        chip.setBackground(new Color(240, 240, 240));
        chip.setBorder(new EmptyBorder(3, 8, 3, 8));

        left.add(lblCod);
        left.add(Box.createVerticalStrut(4));
        left.add(chip);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton btnCompletar = new JButton("Completar");
        btnCompletar.setBackground(new Color(30, 30, 30));
        btnCompletar.setForeground(Color.WHITE);
        btnCompletar.setBorder(new EmptyBorder(5, 10, 5, 10));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(240, 240, 240));
        btnCancelar.setBorder(new EmptyBorder(5, 10, 5, 10));

        right.add(btnCompletar);
        right.add(btnCancelar);

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        // ——— MID
        JPanel mid = new JPanel();
        mid.setOpaque(false);
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));

        mid.add(new JLabel("Cliente: " + cliente));
        mid.add(new JLabel("Fecha: " + fecha));
        mid.add(Box.createVerticalStrut(5));

        JTextArea txt = new JTextArea(detalle);
        txt.setOpaque(false);
        txt.setEditable(false);

        mid.add(txt);
        mid.add(Box.createVerticalStrut(8));
        mid.add(new JLabel("Total: " + total + " unidades"));

        item.add(top, BorderLayout.NORTH);
        item.add(mid, BorderLayout.CENTER);

        panelPendientes.add(item);
        panelPendientes.add(Box.createVerticalStrut(15));
        panelPendientes.revalidate();

        return item;
    }

    // =========================================================
    // MÉTODO PARA AGREGAR AL HISTORIAL
    // =========================================================
    public void addPedidoHistorial(String codigo, String cliente, int total, String estado) {

        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Color.WHITE);
        item.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel icon = new JLabel("⏲");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel lblCod = new JLabel(codigo);
        lblCod.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblCli = new JLabel(cliente);
        lblCli.setFont(new Font("SansSerif", Font.PLAIN, 12));

        info.add(lblCod);
        info.add(lblCli);

        left.add(icon);
        left.add(info);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel lblTot = new JLabel(total + " unidades");

        JLabel chip = new JLabel(estado);
        chip.setOpaque(true);
        chip.setBorder(new EmptyBorder(4, 10, 4, 10));
        chip.setBackground(new Color(230, 230, 230));

        right.add(lblTot);
        right.add(chip);

        item.add(left, BorderLayout.WEST);
        item.add(right, BorderLayout.EAST);

        panelHistorial.add(item);
        panelHistorial.add(Box.createVerticalStrut(10));
        panelHistorial.revalidate();
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
