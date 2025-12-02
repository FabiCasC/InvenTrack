package app.view;

import app.controller.ProductoController;
import app.model.Productos;
import app.service.PrediccionService;
import app.utils.ColorConstants;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * RF4.4 - Vista para mostrar predicciones de demanda usando Árbol de Decisión
 */
public class PrediccionesInternal extends javax.swing.JInternalFrame {
    
    public JComboBox<String> comboProducto;
    public JComboBox<Integer> comboDiasProyeccion;
    public JButton btnPredecir;
    public JPanel panelResultado;
    
    private ProductoController productoController;
    private PrediccionService prediccionService;
    private java.util.Map<String, String> productosMap; // Nombre -> ProductoId
    
    public PrediccionesInternal() {
        setTitle("Predicciones de Demanda");
        setClosable(false);
        setResizable(false);
        setBorder(null);
        setSize(970, 650);
        
        this.productoController = new ProductoController();
        this.prediccionService = new PrediccionService();
        this.productosMap = new java.util.HashMap<>();
        
        initComponents();
        cargarProductos();
    }
    
    private void initComponents() {
        JPanel main = new JPanel();
        main.setBackground(ColorConstants.BLANCO_HUMO);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Título
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel titulo = new JLabel("Predicciones de Demanda");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(ColorConstants.GRIS_PIZARRA);
        
        JLabel subtitulo = new JLabel("RF4.4 - Árbol de Decisión basado en ventas históricas, categorías, estacionalidad y comportamiento de pedidos");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        
        header.add(titulo, BorderLayout.WEST);
        header.add(subtitulo, BorderLayout.SOUTH);
        main.add(header);
        main.add(Box.createVerticalStrut(25));
        
        // Panel de controles
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelControles.setOpaque(false);
        panelControles.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
            "Configurar Predicción",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            ColorConstants.GRIS_PIZARRA
        ));
        panelControles.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        // Producto
        JLabel lblProducto = new JLabel("Producto:");
        lblProducto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        comboProducto = new JComboBox<>();
        comboProducto.setPreferredSize(new Dimension(300, 35));
        comboProducto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Días de proyección
        JLabel lblDias = new JLabel("Días de proyección:");
        lblDias.setFont(new Font("Segoe UI", Font.BOLD, 13));
        comboDiasProyeccion = new JComboBox<>(new Integer[]{7, 15, 30, 60, 90});
        comboDiasProyeccion.setSelectedItem(30);
        comboDiasProyeccion.setPreferredSize(new Dimension(100, 35));
        comboDiasProyeccion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Botón predecir
        btnPredecir = new JButton("Generar Predicción");
        btnPredecir.setBackground(ColorConstants.VERDE_ESMERALDA);
        btnPredecir.setForeground(Color.WHITE);
        btnPredecir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPredecir.setFocusPainted(false);
        btnPredecir.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnPredecir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPredecir.addActionListener(e -> generarPrediccion());
        
        panelControles.add(lblProducto);
        panelControles.add(comboProducto);
        panelControles.add(Box.createHorizontalStrut(10));
        panelControles.add(lblDias);
        panelControles.add(comboDiasProyeccion);
        panelControles.add(Box.createHorizontalStrut(10));
        panelControles.add(btnPredecir);
        
        main.add(panelControles);
        main.add(Box.createVerticalStrut(20));
        
        // Panel de resultado
        panelResultado = new JPanel();
        panelResultado.setLayout(new BoxLayout(panelResultado, BoxLayout.Y_AXIS));
        panelResultado.setBackground(ColorConstants.BLANCO_PURO);
        panelResultado.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
            "Resultado de la Predicción",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            ColorConstants.GRIS_PIZARRA
        ));
        panelResultado.setVisible(false);
        
        JScrollPane scrollResultado = new JScrollPane(panelResultado);
        scrollResultado.setBorder(null);
        scrollResultado.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollResultado.setOpaque(false);
        scrollResultado.getViewport().setOpaque(false);
        
        main.add(scrollResultado);
        
        add(main);
    }
    
    private void cargarProductos() {
        try {
            List<Productos> productos = productoController.listarProductos();
            comboProducto.removeAllItems();
            comboProducto.addItem("Selecciona un producto");
            productosMap.clear();
            
            if (productos != null && !productos.isEmpty()) {
                for (Productos p : productos) {
                    String nombreCompleto = p.getNombre() + " (Stock: " + p.getStock_actual() + ")";
                    comboProducto.addItem(nombreCompleto);
                    productosMap.put(nombreCompleto, p.getProductoId());
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
    
    private void generarPrediccion() {
        String productoNombre = (String) comboProducto.getSelectedItem();
        
        if (productoNombre == null || productoNombre.equals("Selecciona un producto") ||
            productoNombre.equals("No hay productos registrados") ||
            productoNombre.equals("Error al cargar")) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar un producto válido", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String productoId = productosMap.get(productoNombre);
        if (productoId == null) {
            JOptionPane.showMessageDialog(this, 
                "Error: No se pudo obtener el ID del producto", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Integer dias = (Integer) comboDiasProyeccion.getSelectedItem();
        if (dias == null) {
            dias = 30;
        }
        
        try {
            btnPredecir.setEnabled(false);
            btnPredecir.setText("Generando...");
            
            // RF4.4 - Generar predicción usando árbol de decisión
            PrediccionService.PrediccionInventario prediccion = 
                prediccionService.predecirInventario(productoId, dias);
            
            mostrarPrediccion(prediccion);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al generar predicción: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            btnPredecir.setEnabled(true);
            btnPredecir.setText("Generar Predicción");
        }
    }
    
    private void mostrarPrediccion(PrediccionService.PrediccionInventario prediccion) {
        panelResultado.removeAll();
        panelResultado.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título del producto
        JLabel lblTitulo = new JLabel("Predicción para: " + prediccion.getProductoNombre());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(ColorConstants.GRIS_PIZARRA);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelResultado.add(lblTitulo);
        panelResultado.add(Box.createVerticalStrut(20));
        
        // Card de stock actual
        panelResultado.add(crearCardMetrica(
            "Stock Actual",
            String.valueOf(prediccion.getStockActual()),
            ColorConstants.AZUL_ACERO
        ));
        panelResultado.add(Box.createVerticalStrut(15));
        
        // Card de stock proyectado
        Color colorProyectado = prediccion.getStockProyectado() < prediccion.getStockActual() * 0.5 
            ? ColorConstants.ROJO_ALERTA 
            : ColorConstants.VERDE_ESMERALDA;
        panelResultado.add(crearCardMetrica(
            "Stock Proyectado (" + prediccion.getDiasProyeccion() + " días)",
            String.valueOf(prediccion.getStockProyectado()),
            colorProyectado
        ));
        panelResultado.add(Box.createVerticalStrut(15));
        
        // RF4.4 - Predicción de demanda del árbol de decisión
        String prediccionDemanda = prediccion.getPrediccionDemanda();
        Color colorDemanda;
        String iconoDemanda;
        
        if ("subirá".equalsIgnoreCase(prediccionDemanda)) {
            colorDemanda = ColorConstants.VERDE_ESMERALDA;
            iconoDemanda = "↑";
        } else if ("bajará".equalsIgnoreCase(prediccionDemanda)) {
            colorDemanda = ColorConstants.ROJO_ALERTA;
            iconoDemanda = "↓";
        } else {
            colorDemanda = ColorConstants.NARANJA_AMBAR;
            iconoDemanda = "→";
        }
        
        JPanel cardDemanda = crearCardMetrica(
            "Predicción de Demanda",
            iconoDemanda + " " + prediccionDemanda.toUpperCase(),
            colorDemanda
        );
        panelResultado.add(cardDemanda);
        panelResultado.add(Box.createVerticalStrut(15));
        
        // Información adicional
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(ColorConstants.BLANCO_PURO);
        panelInfo.setBorder(BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO));
        panelInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        agregarInfo(panelInfo, "Consumo Promedio Diario:", 
            String.format("%.2f unidades/día", prediccion.getConsumoPromedioDiario()));
        agregarInfo(panelInfo, "Fecha de Proyección:", 
            new java.text.SimpleDateFormat("dd/MM/yyyy").format(prediccion.getFechaProyeccion()));
        agregarInfo(panelInfo, "Nivel de Riesgo:", prediccion.getRiesgo());
        
        panelResultado.add(panelInfo);
        
        panelResultado.setVisible(true);
        panelResultado.revalidate();
        panelResultado.repaint();
    }
    
    private JPanel crearCardMetrica(String titulo, String valor, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(Color.WHITE);
        
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(5));
        card.add(lblValor);
        
        return card;
    }
    
    private void agregarInfo(JPanel panel, String etiqueta, String valor) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ColorConstants.GRIS_PIZARRA);
        
        JLabel val = new JLabel(valor);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        
        fila.add(lbl, BorderLayout.WEST);
        fila.add(val, BorderLayout.EAST);
        panel.add(fila);
    }
}

