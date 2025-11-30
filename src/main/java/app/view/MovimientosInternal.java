package app.view;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class MovimientosInternal extends javax.swing.JInternalFrame {

    public JButton btnRegistrarMovimiento;

    public JButton btnFiltroTodos;
    public JButton btnFiltroEntradas;
    public JButton btnFiltroSalidas;

    public JLabel lblTotalMovimientos;
    public JLabel lblTotalEntradas;
    public JLabel lblTotalSalidas;

    public JPanel panelListaMovimientos;

    public MovimientosInternal() {

        setTitle("Movimientos");
        setClosable(false);
        setResizable(false);
        setBorder(null);
        setSize(900, 650);



        // FONDO GRIS (igual que Dashboard)
        JPanel root = new JPanel();
        root.setBackground(new Color(245, 245, 245));
        root.setLayout(new BorderLayout());
        add(root);

        // CONTENIDO SCROLLEABLE
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(245, 245, 245));
        scroll.getVerticalScrollBar().setUnitIncrement(25);

        root.add(scroll, BorderLayout.CENTER);

        // ---------------------------------------------------
        // TITULO + SUBTITULO + BOTÓN
        // ---------------------------------------------------
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // sin padding

        // TÍTULO
        JLabel lblTitulo = new JLabel("Movimientos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(60, 60, 60));

        // SUBTÍTULO
        JLabel lblDesc = new JLabel("Historial de entradas y salidas de inventario");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblDesc.setForeground(new Color(90, 90, 90));

        // CAJA IZQUIERDA (TÍTULO + SUBTÍTULO)
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(lblTitulo);
        titleBox.add(lblDesc);

        // BOTÓN (CON ALTURA REDUCIDA)
        btnRegistrarMovimiento = new JButton("Registrar");
        btnRegistrarMovimiento.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegistrarMovimiento.setBackground(new Color(50, 50, 50));
        btnRegistrarMovimiento.setForeground(Color.WHITE);
        btnRegistrarMovimiento.setFocusPainted(false);
        btnRegistrarMovimiento.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrarMovimiento.setPreferredSize(new Dimension(120, 30));

        // EVENTO: REGISTRAR MOVIMIENTO
        btnRegistrarMovimiento.addActionListener(e -> {
            MovimientoFormInternal form = new MovimientoFormInternal();
            getDesktopPane().add(form);
            form.setVisible(true);
        });
        
        // ARMAR HEADER
        header.add(titleBox, BorderLayout.WEST);

        // PANEL CONTENEDOR PARA CONTROLAR LA ALTURA DEL BOTÓN
        JPanel btnWrapper = new JPanel();
        btnWrapper.setOpaque(false);
        btnWrapper.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        btnWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // sin margen
        
        // Tamaño REAL del botón (tú puedes ajustar esto)
        btnRegistrarMovimiento.setPreferredSize(new Dimension(120, 32));
        btnRegistrarMovimiento.setMaximumSize(new Dimension(120, 32));
        btnRegistrarMovimiento.setMinimumSize(new Dimension(120, 32));

        btnWrapper.add(btnRegistrarMovimiento);
        header.add(btnWrapper, BorderLayout.EAST);
        
        content.add(header);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // ---------------------------------------------------
        // CARDS DE ESTADISTICAS (IGUAL A DASHBOARD)
        // ---------------------------------------------------
        JPanel cards = new JPanel(new GridLayout(1, 3, 15, 0));
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        cards.setOpaque(false);

        lblTotalMovimientos = new JLabel("—");
        lblTotalEntradas = new JLabel("—");
        lblTotalSalidas = new JLabel("—");

        cards.add(createCard("Total Movimientos", lblTotalMovimientos));
        cards.add(createCard("Entradas", lblTotalEntradas));
        cards.add(createCard("Salidas", lblTotalSalidas));

        content.add(cards);

        // ---------------------------------------------------
        // FILTROS (chips estilo limpio)
        // ---------------------------------------------------
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFiltros.setOpaque(false);

        btnFiltroTodos = createChip("Todos", true);
        btnFiltroEntradas = createChip("Entradas", false);
        btnFiltroSalidas = createChip("Salidas", false);

        panelFiltros.add(btnFiltroTodos);
        panelFiltros.add(btnFiltroEntradas);
        panelFiltros.add(btnFiltroSalidas);

        content.add(panelFiltros);
        content.add(Box.createVerticalStrut(20));

        // ---------------------------------------------------
        // LISTA DE MOVIMIENTOS
        // ---------------------------------------------------
        JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setBackground(Color.WHITE);
        listContainer.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        panelListaMovimientos = new JPanel();
        panelListaMovimientos.setBackground(Color.WHITE);
        panelListaMovimientos.setLayout(new BoxLayout(panelListaMovimientos, BoxLayout.Y_AXIS));

        listContainer.add(panelListaMovimientos, BorderLayout.NORTH);

        content.add(listContainer);
    }

    // =====================================================
    // CARD estilo Dashboard
    // =====================================================
    private JPanel createCard(String title, JLabel label) {

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel l1 = new JLabel(title);
        l1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l1.setForeground(new Color(90, 90, 90));
        l1.setAlignmentX(Component.CENTER_ALIGNMENT);

        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(new Color(40, 40, 40));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(10));
        card.add(l1);
        card.add(label);
        card.add(Box.createVerticalStrut(10));

        return card;
    }

    // =====================================================
    // CHIP FILTRO
    // =====================================================
    private JButton createChip(String text, boolean active) {

        JButton chip = new JButton(text);
        chip.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chip.setFocusPainted(false);
        chip.setCursor(new Cursor(Cursor.HAND_CURSOR));

        chip.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        if (active) {
            chip.setBackground(Color.BLACK);
            chip.setForeground(Color.WHITE);
        } else {
            chip.setBackground(new Color(230, 230, 230));
            chip.setForeground(Color.BLACK);
        }

        return chip;
    }

    // =====================================================
    // TARJETA DE MOVIMIENTO (vista pura)
    // =====================================================
    public JPanel crearTarjetaMovimiento(String tipo, String producto, String lote,
            String proveedor, String fecha, String cantidad) {

        Color color = tipo.equalsIgnoreCase("Entrada")
                ? new Color(90, 180, 100)
                : new Color(80, 110, 240);

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // ICONO
        JPanel icon = new JPanel();
        icon.setPreferredSize(new Dimension(16, 16));
        icon.setBackground(color);
        icon.setBorder(BorderFactory.createLineBorder(color, 8, true));

        // INFO
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel lblProducto = new JLabel(producto);
        lblProducto.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel lblDetalle = new JLabel("Lote " + lote + "  •  " + proveedor);
        lblDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetalle.setForeground(new Color(100, 100, 100));

        JLabel lblFecha = new JLabel(fecha);
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFecha.setForeground(new Color(120, 120, 120));

        info.add(lblProducto);
        info.add(lblDetalle);
        info.add(lblFecha);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(icon);
        left.add(info);

        // CANTIDAD
        JLabel lblCantidad = new JLabel(cantidad);
        lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblCantidad.setForeground(color);

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.add(lblCantidad, BorderLayout.CENTER);

        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);

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
