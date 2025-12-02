/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package app.view;

import app.controller.PedidoController;
import app.controller.ProductoController;
import app.model.Pedidos;
import app.model.Productos;
import app.utils.ColorConstants;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidosInternal extends javax.swing.JInternalFrame {
    
    private PedidoController pedidoController;
    private ProductoController productoController;
    private Map<String, String> productosMap; // ID -> Nombre

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
        getContentPane().setBackground(ColorConstants.BLANCO_HUMO);
        setSize(970, 650);
        
        this.pedidoController = new PedidoController();
        this.productoController = new ProductoController();
        this.productosMap = new HashMap<>();

        // PANEL PRINCIPAL
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ColorConstants.BLANCO_HUMO);
        add(mainPanel, BorderLayout.CENTER);

        // ============================
        // HEADER SUPERIOR
        // ============================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorConstants.BLANCO_HUMO);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ColorConstants.BLANCO_HUMO);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Pedidos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(ColorConstants.GRIS_PIZARRA);

        JLabel subtitleLabel = new JLabel("Gestiona pedidos a tiendas y clientes");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        btnNuevoPedido = new JButton(" +  Crear Pedido");
        btnNuevoPedido.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNuevoPedido.setForeground(Color.WHITE);
        btnNuevoPedido.setBackground(ColorConstants.AZUL_ACERO);
        btnNuevoPedido.setBorder(null);
        btnNuevoPedido.setFocusPainted(false);
        btnNuevoPedido.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevoPedido.setPreferredSize(new Dimension(140, 40));
        
        // EVENTO: CREAR PEDIDO
        btnNuevoPedido.addActionListener(e -> {
            PedidoFormInternal form = new PedidoFormInternal();
            form.setSize(600, 550);
            form.setLocation(
                (getDesktopPane().getWidth() - form.getWidth()) / 2,
                (getDesktopPane().getHeight() - form.getHeight()) / 2
            );
            getDesktopPane().add(form);
            form.setVisible(true);
            
            // Recargar después de crear pedido
            form.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                    cargarPedidos();
                }
            });
        });

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnNuevoPedido, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ============================
        // CONTENT PANEL
        // ============================
        JPanel content = new JPanel();
        content.setBackground(ColorConstants.BLANCO_HUMO);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 25, 25, 25));

        mainPanel.add(content, BorderLayout.CENTER);

        // ============================
        // ESTADISTICAS
        // ============================
        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setBackground(ColorConstants.BLANCO_HUMO);

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
        cardPendientes.setBackground(ColorConstants.BLANCO_PURO);
        cardPendientes.setBorder(new CompoundBorder(
                new LineBorder(ColorConstants.GRIS_CLARO, 1, true),
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
        cardHistorial.setBackground(ColorConstants.BLANCO_PURO);
        cardHistorial.setBorder(new CompoundBorder(
                new LineBorder(ColorConstants.GRIS_CLARO, 1, true),
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
        
        // Cargar productos para mapear IDs a nombres
        cargarProductos();
        
        // Cargar pedidos al inicializar
        cargarPedidos();
    }
    
    private void cargarProductos() {
        try {
            List<Productos> productos = productoController.listarProductos();
            productosMap.clear();
            if (productos != null) {
                for (Productos p : productos) {
                    productosMap.put(p.getProductoId(), p.getNombre());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
        }
    }
    
    private void cargarPedidos() {
        try {
            // Limpiar paneles
            panelPendientes.removeAll();
            panelHistorial.removeAll();
            
            // Cargar pedidos desde Firebase
            List<Pedidos> pedidos = pedidoController.listarPedidos();
            
            if (pedidos == null || pedidos.isEmpty()) {
                JLabel lblVacioPendientes = new JLabel("No hay pedidos pendientes");
                lblVacioPendientes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                lblVacioPendientes.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
                lblVacioPendientes.setAlignmentX(Component.CENTER_ALIGNMENT);
                panelPendientes.add(Box.createVerticalGlue());
                panelPendientes.add(lblVacioPendientes);
                panelPendientes.add(Box.createVerticalGlue());
                
                JLabel lblVacioHistorial = new JLabel("No hay historial de pedidos");
                lblVacioHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                lblVacioHistorial.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
                lblVacioHistorial.setAlignmentX(Component.CENTER_ALIGNMENT);
                panelHistorial.add(Box.createVerticalGlue());
                panelHistorial.add(lblVacioHistorial);
                panelHistorial.add(Box.createVerticalGlue());
                
                lblTotalPedidos.setText("0");
                lblPendientes.setText("0");
                lblCompletados.setText("0");
            } else {
                int total = pedidos.size();
                int pendientes = 0;
                int completados = 0;
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                
                for (Pedidos pedido : pedidos) {
                    String estado = pedido.getEstado() != null ? pedido.getEstado().toLowerCase() : "";
                    String productoNombre = productosMap.getOrDefault(pedido.getProductoId(), "Producto desconocido");
                    String cliente = pedido.getCliente() != null ? pedido.getCliente() : "Sin cliente";
                    String fecha = pedido.getFecha() != null ? sdf.format(pedido.getFecha()) : "Fecha no disponible";
                    String codigo = pedido.getPedidoId() != null ? pedido.getPedidoId() : "Sin código";
                    int cantidad = pedido.getCantidadSolicitada();
                    
                    // Clasificar pedidos
                    if (estado.equals("pendiente") || estado.equals("en proceso")) {
                        pendientes++;
                        // Agregar a pendientes
                        addPedidoPendiente(
                            codigo,
                            cliente,
                            fecha,
                            productoNombre + " x" + cantidad,
                            cantidad
                        );
                    } else {
                        if (estado.equals("entregado") || estado.equals("completado")) {
                            completados++;
                        }
                        // Agregar al historial
                        addPedidoHistorial(
                            codigo,
                            cliente,
                            cantidad,
                            estado.substring(0, 1).toUpperCase() + estado.substring(1)
                        );
                    }
                }
                
                // Actualizar estadísticas
                lblTotalPedidos.setText(String.valueOf(total));
                lblPendientes.setText(String.valueOf(pendientes));
                lblCompletados.setText(String.valueOf(completados));
            }
            
            panelPendientes.revalidate();
            panelPendientes.repaint();
            panelHistorial.revalidate();
            panelHistorial.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar pedidos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // TARJETA DE ESTADÍSTICAS
    // =========================================================
    private JPanel createStatCard(String title) {
        Color accentColor = ColorConstants.AZUL_ACERO;
        if (title.contains("Pendientes")) accentColor = ColorConstants.AMARILLO_ADVERTENCIA;
        if (title.contains("Completados")) accentColor = ColorConstants.VERDE_EXITO;

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(ColorConstants.BLANCO_PURO);

        card.setBorder(new CompoundBorder(
                new MatteBorder(2, 0, 0, 0, accentColor),
                new CompoundBorder(
                    new LineBorder(ColorConstants.GRIS_CLARO, 1, true),
                new EmptyBorder(15, 15, 15, 15)
                )
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValue = new JLabel("—");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(accentColor);
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
        final String pedidoId = codigo; // Guardar para usar en los listeners de los botones

        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(ColorConstants.BLANCO_PURO);
        item.setBorder(new CompoundBorder(
                new LineBorder(ColorConstants.GRIS_CLARO, 1, true),
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
            chip.setBackground(ColorConstants.GRIS_CLARO);
        chip.setBorder(new EmptyBorder(3, 8, 3, 8));

        left.add(lblCod);
        left.add(Box.createVerticalStrut(4));
        left.add(chip);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton btnCompletar = new JButton("Completar");
        btnCompletar.setBackground(ColorConstants.VERDE_EXITO);
        btnCompletar.setForeground(Color.WHITE);
        btnCompletar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCompletar.setBorder(new EmptyBorder(5, 12, 5, 12));
        btnCompletar.setFocusPainted(false);
        btnCompletar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCompletar.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea completar este pedido?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
            );
            if (respuesta == JOptionPane.YES_OPTION) {
                pedidoController.actualizarEstadoPedido(pedidoId, "Entregado");
                cargarPedidos();
            }
        });

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(ColorConstants.GRIS_NEUTRO);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancelar.setBorder(new EmptyBorder(5, 12, 5, 12));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cancelar este pedido?",
                "Confirmar cancelación",
                JOptionPane.YES_NO_OPTION
            );
            if (respuesta == JOptionPane.YES_OPTION) {
                pedidoController.actualizarEstadoPedido(pedidoId, "Cancelado");
                cargarPedidos();
            }
        });

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

}
