package app.view;

import app.utils.ColorConstants;
import javax.swing.*;
import java.awt.*;

public class DashboardInternal extends javax.swing.JInternalFrame {

    // Componentes ACCESIBLES para el controlador
    public JLabel lblTotalProductos;
    public JLabel lblAlertas;
    public JLabel lblStockBajo;
    public JLabel lblPedidosPendientes;

    public JTextArea txtLotes;
    public JTextArea txtStockBajo;
    public JTextArea txtAltaDemanda;

    public DashboardInternal() {
        setTitle("Dashboard");
        setClosable(false);
        setMaximizable(false);
        setIconifiable(false);
        setResizable(false);
        setBorder(null);
        setSize(970, 650);

        JPanel main = new JPanel();
        main.setBackground(ColorConstants.BLANCO_HUMO);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //-------------------------------------
        // TÍTULO
        //-------------------------------------
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ColorConstants.GRIS_PIZARRA);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        main.add(title);
        main.add(Box.createVerticalStrut(15));

        //-------------------------------------
        // TARJETAS SUPERIORES
        //-------------------------------------
        JPanel cards = new JPanel(new GridLayout(1, 4, 15, 0));
        cards.setOpaque(false);

        lblTotalProductos = new JLabel("—");
        lblAlertas = new JLabel("—");
        lblStockBajo = new JLabel("—");
        lblPedidosPendientes = new JLabel("—");

        cards.add(createCard("Total Productos", lblTotalProductos, ColorConstants.AZUL_ACERO));
        cards.add(createCard("Alertas de Vencimiento", lblAlertas, ColorConstants.AMARILLO_ADVERTENCIA));
        cards.add(createCard("Stock Bajo", lblStockBajo, ColorConstants.ROJO_ALERTA));
        cards.add(createCard("Pedidos Pendientes", lblPedidosPendientes, ColorConstants.VERDE_EXITO));

        main.add(cards);
        main.add(Box.createVerticalStrut(15));

        //-------------------------------------
        // SECCIONES
        //-------------------------------------
        txtLotes = new JTextArea();
        txtStockBajo = new JTextArea();
        txtAltaDemanda = new JTextArea();

        main.add(createSection("Lotes Próximos a Vencer", txtLotes, ColorConstants.AMARILLO_ADVERTENCIA));
        main.add(Box.createVerticalStrut(12));

        main.add(createSection("Stock Bajo", txtStockBajo, ColorConstants.ROJO_ALERTA));
        main.add(Box.createVerticalStrut(12));

        main.add(createSection("Alta Demanda", txtAltaDemanda, ColorConstants.VERDE_ESMERALDA));

        add(main);
    }

    // TARJETA con color de acento
    private JPanel createCard(String title, JLabel dynamicLabel, Color accentColor) {
        JPanel card = new JPanel();
        card.setBackground(ColorConstants.BLANCO_PURO);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, accentColor),
            BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(140, 95));

        JLabel l1 = new JLabel(title);
        l1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l1.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);

        dynamicLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        dynamicLabel.setForeground(accentColor);

        l1.setAlignmentX(Component.CENTER_ALIGNMENT);
        dynamicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(10));
        card.add(l1);
        card.add(dynamicLabel);
        card.add(Box.createVerticalStrut(10));

        return card;
    }

    // SECCIÓN con color de acento
    private JPanel createSection(String title, JTextArea area, Color accentColor) {
        JPanel section = new JPanel();
        section.setBackground(ColorConstants.BLANCO_PURO);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, accentColor),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            )
        ));
        section.setLayout(new BorderLayout());

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 18));
        t.setForeground(ColorConstants.GRIS_PIZARRA);

        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setEditable(false);
        area.setBackground(ColorConstants.BLANCO_PURO);
        area.setForeground(ColorConstants.GRIS_PIZARRA);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        section.add(t, BorderLayout.NORTH);
        section.add(new JScrollPane(area), BorderLayout.CENTER);

        return section;
    }

    @SuppressWarnings("unchecked")
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
    }
}
