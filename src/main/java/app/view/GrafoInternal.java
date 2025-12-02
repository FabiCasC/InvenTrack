package app.view;

import app.controller.ProductoController;
import app.controller.ProveedorController;
import app.model.Productos;
import app.model.Proveedores;
import app.service.GrafoService;
import app.utils.ColorConstants;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * RF4.5 - Vista para visualizar el grafo de relaciones Productos-Proveedores
 */
public class GrafoInternal extends javax.swing.JInternalFrame {
    
    public JTextArea areaGrafo;
    public JButton btnRefrescar;
    public JButton btnVerProveedorCritico;
    public JPanel panelInfo;
    
    private GrafoService grafoService;
    private ProductoController productoController;
    private ProveedorController proveedorController;
    
    public GrafoInternal() {
        setTitle("Grafo de Relaciones Productos-Proveedores");
        setClosable(false);
        setResizable(false);
        setBorder(null);
        setSize(970, 650);
        
        this.grafoService = new GrafoService();
        this.productoController = new ProductoController();
        this.proveedorController = new ProveedorController();
        
        initComponents();
        cargarGrafo();
    }
    
    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(ColorConstants.BLANCO_HUMO);
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel titulo = new JLabel("Grafo de Relaciones");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(ColorConstants.GRIS_PIZARRA);
        
        JLabel subtitulo = new JLabel("RF4.5 - Visualización de relaciones entre productos y proveedores");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        
        header.add(titulo, BorderLayout.WEST);
        header.add(subtitulo, BorderLayout.SOUTH);
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        
        btnRefrescar = new JButton("Refrescar Grafo");
        btnRefrescar.setBackground(ColorConstants.AZUL_ACERO);
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRefrescar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefrescar.addActionListener(e -> cargarGrafo());
        
        btnVerProveedorCritico = new JButton("Ver Proveedor Crítico");
        btnVerProveedorCritico.setBackground(ColorConstants.ROJO_ALERTA);
        btnVerProveedorCritico.setForeground(Color.WHITE);
        btnVerProveedorCritico.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVerProveedorCritico.setFocusPainted(false);
        btnVerProveedorCritico.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnVerProveedorCritico.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerProveedorCritico.addActionListener(e -> mostrarProveedorCritico());
        
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnVerProveedorCritico);
        
        header.add(panelBotones, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);
        
        // Panel de información del grafo
        panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(ColorConstants.BLANCO_PURO);
        panelInfo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
            "Información del Grafo",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            ColorConstants.GRIS_PIZARRA
        ));
        panelInfo.setPreferredSize(new Dimension(300, 0));
        
        // Área de texto para mostrar el grafo
        areaGrafo = new JTextArea();
        areaGrafo.setEditable(false);
        areaGrafo.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaGrafo.setBackground(ColorConstants.BLANCO_PURO);
        areaGrafo.setForeground(ColorConstants.GRIS_PIZARRA);
        areaGrafo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollGrafo = new JScrollPane(areaGrafo);
        scrollGrafo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
            "Visualización del Grafo",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            ColorConstants.GRIS_PIZARRA
        ));
        scrollGrafo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollGrafo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Panel central dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollGrafo, panelInfo);
        splitPane.setDividerLocation(650);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        
        main.add(splitPane, BorderLayout.CENTER);
        
        add(main);
    }
    
    private void cargarGrafo() {
        try {
            // RF4.5 - Obtener información del grafo
            String infoGrafo = grafoService.obtenerInformacionGrafo();
            
            // Construir visualización del grafo
            StringBuilder visualizacion = new StringBuilder();
            visualizacion.append("=== GRAFO DE RELACIONES PRODUCTOS-PROVEEDORES ===\n\n");
            visualizacion.append(infoGrafo).append("\n\n");
            
            // Obtener todas las dependencias
            Map<String, List<String>> dependencias = grafoService.detectarDependencias();
            
            visualizacion.append("=== DEPENDENCIAS DE REABASTECIMIENTO ===\n\n");
            int contador = 1;
            for (Map.Entry<String, List<String>> entry : dependencias.entrySet()) {
                String productoId = entry.getKey();
                List<String> proveedoresIds = entry.getValue();
                
                try {
                    Productos producto = productoController.obtenerProducto(productoId);
                    visualizacion.append(contador).append(". Producto: ").append(producto.getNombre()).append("\n");
                    visualizacion.append("   ID: ").append(productoId).append("\n");
                    visualizacion.append("   Proveedor(es):\n");
                    
                    for (String proveedorId : proveedoresIds) {
                        try {
                            Proveedores proveedor = proveedorController.obtenerProveedor(proveedorId);
                            visualizacion.append("     - ").append(proveedor.getNombre())
                                .append(" (").append(proveedorId).append(")\n");
                        } catch (Exception e) {
                            visualizacion.append("     - ID: ").append(proveedorId).append("\n");
                        }
                    }
                    visualizacion.append("\n");
                    contador++;
                } catch (Exception e) {
                    visualizacion.append(contador).append(". Producto ID: ").append(productoId).append("\n");
                    contador++;
                }
            }
            
            // Mostrar productos por proveedor
            visualizacion.append("\n=== PRODUCTOS POR PROVEEDOR ===\n\n");
            try {
                List<Proveedores> proveedores = proveedorController.listarProveedores();
                for (Proveedores proveedor : proveedores) {
                    List<String> productosIds = grafoService.obtenerProductosDeProveedor(proveedor.getProveedorId());
                    if (productosIds != null && !productosIds.isEmpty()) {
                        visualizacion.append("Proveedor: ").append(proveedor.getNombre()).append("\n");
                        visualizacion.append("  Abastece ").append(productosIds.size()).append(" producto(s):\n");
                        
                        for (String productoId : productosIds) {
                            try {
                                Productos producto = productoController.obtenerProducto(productoId);
                                visualizacion.append("    - ").append(producto.getNombre())
                                    .append(" (").append(productoId).append(")\n");
                            } catch (Exception e) {
                                visualizacion.append("    - ID: ").append(productoId).append("\n");
                            }
                        }
                        visualizacion.append("\n");
                    }
                }
            } catch (Exception e) {
                visualizacion.append("Error al cargar productos por proveedor: ").append(e.getMessage());
            }
            
            areaGrafo.setText(visualizacion.toString());
            actualizarPanelInfo();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar el grafo: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarPanelInfo() {
        panelInfo.removeAll();
        
        try {
            // RF4.5 - Detectar proveedor crítico
            String proveedorCriticoId = grafoService.detectarProveedorCritico();
            
            if (proveedorCriticoId != null) {
                List<String> productosIds = grafoService.obtenerProductosDeProveedor(proveedorCriticoId);
                int grado = productosIds != null ? productosIds.size() : 0;
                
                try {
                    Proveedores proveedor = proveedorController.obtenerProveedor(proveedorCriticoId);
                    
                    JPanel cardCritico = crearCardInfo(
                        "Proveedor Crítico",
                        proveedor.getNombre(),
                        "Grado: " + grado + " productos",
                        ColorConstants.ROJO_ALERTA
                    );
                    panelInfo.add(cardCritico);
                    panelInfo.add(Box.createVerticalStrut(15));
                } catch (Exception e) {
                    // Ignorar error
                }
            }
            
            // Estadísticas generales
            Map<String, List<String>> dependencias = grafoService.detectarDependencias();
            
            JPanel cardEstadisticas = crearCardInfo(
                "Estadísticas",
                dependencias.size() + " productos con dependencias",
                "Total de relaciones",
                ColorConstants.AZUL_ACERO
            );
            panelInfo.add(cardEstadisticas);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al actualizar información: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        panelInfo.revalidate();
        panelInfo.repaint();
    }
    
    private void mostrarProveedorCritico() {
        try {
            String proveedorCriticoId = grafoService.detectarProveedorCritico();
            
            if (proveedorCriticoId == null) {
                JOptionPane.showMessageDialog(this, 
                    "No se encontró un proveedor crítico", 
                    "Información", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            List<String> productosIds = grafoService.obtenerProductosDeProveedor(proveedorCriticoId);
            int grado = productosIds != null ? productosIds.size() : 0;
            
            Proveedores proveedor = proveedorController.obtenerProveedor(proveedorCriticoId);
            
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("=== PROVEEDOR CRÍTICO ===\n\n");
            mensaje.append("Nombre: ").append(proveedor.getNombre()).append("\n");
            mensaje.append("ID: ").append(proveedorCriticoId).append("\n");
            mensaje.append("Grado: ").append(grado).append(" producto(s)\n\n");
            mensaje.append("Productos abastecidos:\n");
            
            for (int i = 0; i < productosIds.size(); i++) {
                String productoId = productosIds.get(i);
                try {
                    Productos producto = productoController.obtenerProducto(productoId);
                    mensaje.append((i + 1)).append(". ").append(producto.getNombre())
                        .append(" (").append(productoId).append(")\n");
                } catch (Exception e) {
                    mensaje.append((i + 1)).append(". ID: ").append(productoId).append("\n");
                }
            }
            
            JOptionPane.showMessageDialog(this, 
                mensaje.toString(), 
                "Proveedor Crítico - Mayor Grado", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al obtener proveedor crítico: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel crearCardInfo(String titulo, String valor1, String valor2, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblValor1 = new JLabel(valor1);
        lblValor1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblValor1.setForeground(Color.WHITE);
        
        JLabel lblValor2 = new JLabel(valor2);
        lblValor2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblValor2.setForeground(new Color(255, 255, 255, 200));
        
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(5));
        card.add(lblValor1);
        card.add(Box.createVerticalStrut(3));
        card.add(lblValor2);
        
        return card;
    }
}

