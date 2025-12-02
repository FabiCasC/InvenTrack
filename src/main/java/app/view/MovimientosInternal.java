package app.view;

import app.controller.MovimientoController;
import app.controller.ProductoController;
import app.model.Movimientos;
import app.model.Productos;
import app.utils.ColorConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovimientosInternal extends javax.swing.JInternalFrame {

    public JButton btnRegistrarMovimiento;
    public JButton btnFiltroTodos;
    public JButton btnFiltroEntradas;
    public JButton btnFiltroSalidas;
    public JLabel lblTotalMovimientos;
    public JLabel lblTotalEntradas;
    public JLabel lblTotalSalidas;
    public JPanel panelListaMovimientos;
    
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JButton btnFiltrarFechas;
    private JButton btnLimpiarFiltro;

    private MovimientoController movimientoController;
    private ProductoController productoController;
    private Map<String, String> productosMap; // ID -> Nombre
    private Map<String, String> usuariosMap; // ID -> Nombre (por ahora solo IDs)
    private String filtroActual = "todos"; // todos, entrada, salida

    public MovimientosInternal() {
        setTitle("Movimientos");
        setClosable(false);
        setResizable(false);
        setBorder(null);
        setSize(970, 650);

        this.movimientoController = new MovimientoController();
        this.productoController = new ProductoController();
        this.productosMap = new HashMap<>();
        this.usuariosMap = new HashMap<>();

        initComponents();
        cargarProductos();
        cargarUsuarios();
        cargarMovimientos(); // RF3.3 - Cargar historial
    }

    private void initComponents() {
        // Panel raíz
        JPanel root = new JPanel();
        root.setBackground(ColorConstants.BLANCO_HUMO);
        root.setLayout(new BorderLayout());
        add(root);

        // Contenido scrolleable
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(ColorConstants.BLANCO_HUMO);
        scroll.getVerticalScrollBar().setUnitIncrement(25);

        root.add(scroll, BorderLayout.CENTER);

        // Título + Subtítulo + Botón
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel lblTitulo = new JLabel("Movimientos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(ColorConstants.GRIS_PIZARRA);

        JLabel lblDesc = new JLabel("Historial de entradas y salidas de inventario");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesc.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(lblTitulo);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(lblDesc);

        btnRegistrarMovimiento = new JButton("+ Registrar Movimiento");
        btnRegistrarMovimiento.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegistrarMovimiento.setBackground(ColorConstants.AZUL_ACERO);
        btnRegistrarMovimiento.setForeground(Color.WHITE);
        btnRegistrarMovimiento.setFocusPainted(false);
        btnRegistrarMovimiento.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrarMovimiento.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRegistrarMovimiento.setPreferredSize(new Dimension(170, 35));

        btnRegistrarMovimiento.addActionListener(e -> {
            MovimientoFormInternal form = new MovimientoFormInternal();
            form.setSize(600, 550);
            form.setLocation(
                (getDesktopPane().getWidth() - form.getWidth()) / 2,
                (getDesktopPane().getHeight() - form.getHeight()) / 2
            );
            getDesktopPane().add(form);
            form.setVisible(true);
        
            // Recargar después de registrar
            form.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
                @Override
                public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                    cargarMovimientos();
                }
            });
        });

        header.add(titleBox, BorderLayout.WEST);
        header.add(btnRegistrarMovimiento, BorderLayout.EAST);
        content.add(header);
        content.add(Box.createVerticalStrut(15));

        // Cards de estadísticas
        JPanel cards = new JPanel(new GridLayout(1, 3, 15, 0));
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        cards.setOpaque(false);

        lblTotalMovimientos = new JLabel("0");
        lblTotalEntradas = new JLabel("0");
        lblTotalSalidas = new JLabel("0");

        cards.add(createCard("Total Movimientos", lblTotalMovimientos, ColorConstants.AZUL_ACERO));
        cards.add(createCard("Entradas", lblTotalEntradas, ColorConstants.VERDE_ESMERALDA));
        cards.add(createCard("Salidas", lblTotalSalidas, ColorConstants.NARANJA_AMBAR));

        content.add(cards);
        content.add(Box.createVerticalStrut(15));

        // RF3.3 - Filtro por rango de fechas
        JPanel panelFiltroFechas = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFiltroFechas.setOpaque(false);
        panelFiltroFechas.setBorder(BorderFactory.createTitledBorder("Filtro por Fechas (RF3.3)"));
        
        JLabel lblFechaInicio = new JLabel("Desde:");
        lblFechaInicio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFechaInicio = new JTextField(10);
        txtFechaInicio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFechaInicio.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtFechaInicio.setToolTipText("Formato: dd/MM/yyyy");
        
        JLabel lblFechaFin = new JLabel("Hasta:");
        lblFechaFin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFechaFin = new JTextField(10);
        txtFechaFin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtFechaFin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtFechaFin.setToolTipText("Formato: dd/MM/yyyy");
        
        btnFiltrarFechas = new JButton("Filtrar");
        btnFiltrarFechas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFiltrarFechas.setBackground(ColorConstants.AZUL_ACERO);
        btnFiltrarFechas.setForeground(Color.WHITE);
        btnFiltrarFechas.setFocusPainted(false);
        btnFiltrarFechas.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnFiltrarFechas.addActionListener(e -> filtrarPorFechas());
        
        btnLimpiarFiltro = new JButton("Limpiar");
        btnLimpiarFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLimpiarFiltro.setBackground(ColorConstants.GRIS_NEUTRO);
        btnLimpiarFiltro.setForeground(Color.WHITE);
        btnLimpiarFiltro.setFocusPainted(false);
        btnLimpiarFiltro.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnLimpiarFiltro.addActionListener(e -> {
            txtFechaInicio.setText("");
            txtFechaFin.setText("");
            filtroActual = "todos";
            cargarMovimientos();
        });
        
        panelFiltroFechas.add(lblFechaInicio);
        panelFiltroFechas.add(txtFechaInicio);
        panelFiltroFechas.add(lblFechaFin);
        panelFiltroFechas.add(txtFechaFin);
        panelFiltroFechas.add(btnFiltrarFechas);
        panelFiltroFechas.add(btnLimpiarFiltro);
        
        content.add(panelFiltroFechas);
        content.add(Box.createVerticalStrut(10));

        // Filtros por tipo
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFiltros.setOpaque(false);

        btnFiltroTodos = createChip("Todos", true);
        btnFiltroEntradas = createChip("Entradas", false);
        btnFiltroSalidas = createChip("Salidas", false);

        // Eventos de filtros
        btnFiltroTodos.addActionListener(e -> {
            filtroActual = "todos";
            actualizarFiltros();
            cargarMovimientos();
        });
        btnFiltroEntradas.addActionListener(e -> {
            filtroActual = "entrada";
            actualizarFiltros();
            cargarMovimientos();
        });
        btnFiltroSalidas.addActionListener(e -> {
            filtroActual = "salida";
            actualizarFiltros();
            cargarMovimientos();
        });

        panelFiltros.add(btnFiltroTodos);
        panelFiltros.add(btnFiltroEntradas);
        panelFiltros.add(btnFiltroSalidas);

        content.add(panelFiltros);
        content.add(Box.createVerticalStrut(15));

        // Lista de movimientos
        JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setBackground(ColorConstants.BLANCO_PURO);
        listContainer.setBorder(BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO));

        panelListaMovimientos = new JPanel();
        panelListaMovimientos.setBackground(ColorConstants.BLANCO_PURO);
        panelListaMovimientos.setLayout(new BoxLayout(panelListaMovimientos, BoxLayout.Y_AXIS));

        JScrollPane listScroll = new JScrollPane(panelListaMovimientos);
        listScroll.setBorder(null);
        listScroll.setViewportBorder(BorderFactory.createEmptyBorder());

        listContainer.add(listScroll, BorderLayout.CENTER);
        content.add(listContainer);
    }

    private void cargarProductos() {
        try {
            List<Productos> productos = productoController.listarProductos();
            productosMap.clear();
            if (productos != null) {
                for (Productos p : productos) {
                    if (p.getProductoId() != null && p.getNombre() != null) {
                        productosMap.put(p.getProductoId(), p.getNombre());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene el nombre del producto, intentando desde el mapa primero y luego desde Firebase
     */
    private String obtenerNombreProducto(String productoId) {
        if (productoId == null || productoId.trim().isEmpty()) {
            return "Producto desconocido";
        }
        
        // Primero intentar del mapa
        String nombre = productosMap.get(productoId);
        if (nombre != null && !nombre.trim().isEmpty()) {
            return nombre;
        }
        
        // Si no está en el mapa, intentar buscarlo directamente desde Firebase
        try {
            Productos producto = productoController.obtenerProducto(productoId);
            if (producto != null && producto.getNombre() != null) {
                // Actualizar el mapa para futuras consultas
                productosMap.put(productoId, producto.getNombre());
                return producto.getNombre();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener producto " + productoId + ": " + e.getMessage());
        }
        
        // Si todo falla, mostrar el ID como última opción
        return productoId + " (ID)";
    }

    private void cargarUsuarios() {
        // Los usuarios se mostrarán por ID, se puede mejorar después cargando nombres
        usuariosMap.clear();
    }

    private void cargarMovimientos() {
        try {
            // Primero limpiar movimientos de productos desconocidos automáticamente
            movimientoController.eliminarMovimientosProductosDesconocidos();
            
            // Recargar productos después de la limpieza para asegurar que el mapa esté actualizado
            cargarProductos();
            
            // RF3.3 - Cargar movimientos desde Firebase
            List<Movimientos> movimientos = movimientoController.listarMovimientos();
            
            // Filtrar según el tipo seleccionado
            if (movimientos != null) {
                if (filtroActual.equals("entrada")) {
                    movimientos = movimientoController.listarMovimientosPorTipo("entrada");
                } else if (filtroActual.equals("salida")) {
                    movimientos = movimientoController.listarMovimientosPorTipo("salida");
                }
            }
            
            mostrarMovimientos(movimientos);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar movimientos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarPorFechas() {
        // RF3.3 - Filtrar por rango de fechas
        String fechaInicioTxt = txtFechaInicio.getText().trim();
        String fechaFinTxt = txtFechaFin.getText().trim();

        if (fechaInicioTxt.isEmpty() || fechaFinTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, ingrese ambas fechas para filtrar.",
                "Fechas requeridas",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date fechaInicio = sdf.parse(fechaInicioTxt);
            Date fechaFin = sdf.parse(fechaFinTxt);

            if (fechaInicio.after(fechaFin)) {
                JOptionPane.showMessageDialog(this,
                    "La fecha de inicio debe ser anterior a la fecha de fin.",
                    "Fechas inválidas",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Ajustar fecha fin al final del día
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaFin);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            fechaFin = cal.getTime();

            List<Movimientos> movimientos = movimientoController.listarMovimientosPorFechas(fechaInicio, fechaFin);
            
            // Aplicar filtro de tipo si está activo
            if (filtroActual.equals("entrada")) {
                movimientos.removeIf(m -> !m.getTipo_movimiento().equalsIgnoreCase("entrada"));
            } else if (filtroActual.equals("salida")) {
                movimientos.removeIf(m -> !m.getTipo_movimiento().equalsIgnoreCase("salida"));
            }
            
            mostrarMovimientos(movimientos);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al filtrar por fechas: " + e.getMessage() + "\nUse el formato dd/MM/yyyy",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarMovimientos(List<Movimientos> movimientos) {
        // Limpiar lista actual
        panelListaMovimientos.removeAll();

        if (movimientos == null || movimientos.isEmpty()) {
            JLabel lblVacio = new JLabel("No hay movimientos para mostrar");
            lblVacio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lblVacio.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
            lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelListaMovimientos.add(Box.createVerticalGlue());
            panelListaMovimientos.add(lblVacio);
            panelListaMovimientos.add(Box.createVerticalGlue());
        } else {
            // Actualizar métricas
            int total = movimientos.size();
            int entradas = 0;
            int salidas = 0;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (Movimientos movimiento : movimientos) {
                // RF3.3 - Mostrar fecha, tipo, cantidad y usuario
                String tipo = movimiento.getTipo_movimiento();
                if (tipo.equalsIgnoreCase("entrada")) {
                    entradas++;
                } else {
                    salidas++;
                }

                // Obtener nombre del producto
                String productoNombre = obtenerNombreProducto(movimiento.getProductoId());
                String usuarioNombre = movimiento.getUsuarioId() != null ? 
                    "Usuario: " + movimiento.getUsuarioId() : "Usuario no especificado";
                String fechaStr = movimiento.getFecha() != null ? 
                    sdf.format(movimiento.getFecha()) : "Fecha no disponible";
                String cantidadStr = String.valueOf(movimiento.getCantidad());

                JPanel card = crearTarjetaMovimiento(
                    tipo,
                    productoNombre,
                    usuarioNombre,
                    fechaStr,
                    cantidadStr
                );

                panelListaMovimientos.add(card);
                panelListaMovimientos.add(Box.createVerticalStrut(10));
            }

            lblTotalMovimientos.setText(String.valueOf(total));
            lblTotalEntradas.setText(String.valueOf(entradas));
            lblTotalSalidas.setText(String.valueOf(salidas));
        }

        panelListaMovimientos.revalidate();
        panelListaMovimientos.repaint();
    }

    private void actualizarFiltros() {
        btnFiltroTodos.setBackground(filtroActual.equals("todos") ? 
            ColorConstants.GRIS_PIZARRA : ColorConstants.GRIS_CLARO);
        btnFiltroTodos.setForeground(filtroActual.equals("todos") ? Color.WHITE : ColorConstants.GRIS_PIZARRA);
        
        btnFiltroEntradas.setBackground(filtroActual.equals("entrada") ? 
            ColorConstants.GRIS_PIZARRA : ColorConstants.GRIS_CLARO);
        btnFiltroEntradas.setForeground(filtroActual.equals("entrada") ? Color.WHITE : ColorConstants.GRIS_PIZARRA);
        
        btnFiltroSalidas.setBackground(filtroActual.equals("salida") ? 
            ColorConstants.GRIS_PIZARRA : ColorConstants.GRIS_CLARO);
        btnFiltroSalidas.setForeground(filtroActual.equals("salida") ? Color.WHITE : ColorConstants.GRIS_PIZARRA);
    }

    private JPanel createCard(String title, JLabel label, Color accentColor) {
        JPanel card = new JPanel();
        card.setBackground(ColorConstants.BLANCO_PURO);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, accentColor),
            BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel l1 = new JLabel(title);
        l1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l1.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        l1.setAlignmentX(Component.CENTER_ALIGNMENT);

        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(accentColor);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(10));
        card.add(l1);
        card.add(label);
        card.add(Box.createVerticalStrut(10));

        return card;
    }

    private JButton createChip(String text, boolean active) {
        JButton chip = new JButton(text);
        chip.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chip.setFocusPainted(false);
        chip.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chip.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        if (active) {
            chip.setBackground(ColorConstants.GRIS_PIZARRA);
            chip.setForeground(Color.WHITE);
        } else {
            chip.setBackground(ColorConstants.GRIS_CLARO);
            chip.setForeground(ColorConstants.GRIS_PIZARRA);
        }

        chip.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!active) {
                    chip.setBackground(new Color(0xD1D5DB));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!active) {
                    chip.setBackground(ColorConstants.GRIS_CLARO);
        }
            }
        });

        return chip;
    }

    // RF3.3 - Tarjeta de movimiento mostrando fecha, tipo, cantidad y usuario
    public JPanel crearTarjetaMovimiento(String tipo, String producto, String usuario,
            String fecha, String cantidad) {

        Color color = tipo.equalsIgnoreCase("entrada")
                ? ColorConstants.VERDE_ESMERALDA
                : ColorConstants.NARANJA_AMBAR;

        JPanel card = new JPanel();
        card.setBackground(ColorConstants.BLANCO_PURO);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, color),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            )
        ));

        // Icono circular
        JPanel icon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        icon.setPreferredSize(new Dimension(12, 12));
        icon.setOpaque(false);

        // Info
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel lblProducto = new JLabel(producto);
        lblProducto.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblProducto.setForeground(ColorConstants.GRIS_PIZARRA);

        JLabel lblUsuario = new JLabel(usuario);
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUsuario.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);

        JLabel lblFecha = new JLabel(fecha);
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFecha.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);

        info.add(lblProducto);
        info.add(Box.createVerticalStrut(3));
        info.add(lblUsuario);
        info.add(Box.createVerticalStrut(3));
        info.add(lblFecha);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        left.add(icon);
        left.add(Box.createHorizontalStrut(5));
        left.add(info);

        // Cantidad y tipo
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);
        right.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblCantidad = new JLabel(cantidad);
        lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblCantidad.setForeground(color);

        JLabel lblTipo = new JLabel(tipo.toUpperCase());
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTipo.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);

        right.add(lblCantidad);
        right.add(lblTipo);

        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);

        return card;
    }
}
