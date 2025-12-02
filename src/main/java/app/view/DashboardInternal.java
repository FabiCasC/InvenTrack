package app.view;

import app.controller.ProductoController;
import app.controller.PedidoController;
import app.model.Productos;
import app.model.Lotes;
import app.model.Pedidos;
import app.utils.ColorConstants;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashboardInternal extends javax.swing.JInternalFrame {
    
    private ProductoController productoController;
    private PedidoController pedidoController;
    private Firestore db;
    private static final String COLLECTION_PRODUCTOS = "productos";
    private static final String COLLECTION_LOTES = "Lotes";
    

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

        this.productoController = new ProductoController();
        this.pedidoController = new PedidoController();
        this.db = FirestoreClient.getFirestore();

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
        
        // Cargar datos al inicializar
        cargarDatosDashboard();
    }
    
    private void cargarDatosDashboard() {
        try {
            // Cargar productos
            List<Productos> productos = productoController.listarProductos();
            
            // Cargar pedidos
            List<Pedidos> pedidos = pedidoController.listarPedidos();
            
            // Calcular métricas
            int totalProductos = productos != null ? productos.size() : 0;
            int alertasVencimiento = 0;
            int stockBajo = 0;
            int pedidosPendientes = 0;
            
            // Listas para las secciones
            List<String> lotesPorVencer = new ArrayList<>();
            List<String> productosStockBajo = new ArrayList<>();
            List<String> altaDemanda = new ArrayList<>();
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date hoy = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(hoy);
            cal.add(Calendar.DAY_OF_MONTH, 30);
            Date dentro30Dias = cal.getTime();
            
            // Analizar productos
            if (productos != null) {
                for (Productos producto : productos) {
                    // Verificar stock bajo
                    if (producto.getStock_actual() <= producto.getStock_minimo()) {
                        stockBajo++;
                        productosStockBajo.add(producto.getNombre() + " - Stock: " + 
                            producto.getStock_actual() + " (Mín: " + producto.getStock_minimo() + ")");
                    }
                    
                    // Verificar lotes próximos a vencer
                    try {
                        QuerySnapshot lotesSnapshot = db.collection(COLLECTION_PRODUCTOS)
                                .document(producto.getProductoId())
                                .collection(COLLECTION_LOTES)
                                .get()
                                .get();
                        
                        for (QueryDocumentSnapshot loteDoc : lotesSnapshot.getDocuments()) {
                            Lotes lote = loteDoc.toObject(Lotes.class);
                            if (lote != null && lote.getFecha_Vencimiento() != null) {
                                Date fechaVenc = lote.getFecha_Vencimiento();
                                if (fechaVenc.after(hoy) && fechaVenc.before(dentro30Dias)) {
                                    alertasVencimiento++;
                                    long diasRestantes = (fechaVenc.getTime() - hoy.getTime()) / (1000 * 60 * 60 * 24);
                                    lotesPorVencer.add(producto.getNombre() + " - Lote " + lote.getLoteId() + 
                                        " (" + lote.getCantidad() + " unidades) - Vence: " + 
                                        sdf.format(fechaVenc) + " (" + diasRestantes + " días)");
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Ignorar errores al obtener lotes
                    }
                }
            }
            
            // Analizar pedidos
            if (pedidos != null) {
                for (Pedidos pedido : pedidos) {
                    String estado = pedido.getEstado() != null ? pedido.getEstado().toLowerCase() : "";
                    if (estado.equals("pendiente") || estado.equals("en proceso")) {
                        pedidosPendientes++;
                    }
                }
            }
            
            // Detectar alta demanda (productos con muchos pedidos)
            if (pedidos != null && productos != null) {
                for (Productos producto : productos) {
                    int contadorPedidos = 0;
                    for (Pedidos pedido : pedidos) {
                        if (producto.getProductoId().equals(pedido.getProductoId()) &&
                            (pedido.getEstado().equalsIgnoreCase("pendiente") || 
                             pedido.getEstado().equalsIgnoreCase("en proceso"))) {
                            contadorPedidos++;
                        }
                    }
                    if (contadorPedidos >= 3) {
                        altaDemanda.add(producto.getNombre() + " - " + contadorPedidos + " pedidos pendientes");
                    }
                }
            }
            
            // Actualizar labels
            lblTotalProductos.setText(String.valueOf(totalProductos));
            lblAlertas.setText(String.valueOf(alertasVencimiento));
            lblStockBajo.setText(String.valueOf(stockBajo));
            lblPedidosPendientes.setText(String.valueOf(pedidosPendientes));
            
            // Actualizar áreas de texto
            txtLotes.setText(lotesPorVencer.isEmpty() ? 
                "No hay lotes próximos a vencer" : 
                String.join("\n", lotesPorVencer));
            
            txtStockBajo.setText(productosStockBajo.isEmpty() ? 
                "No hay productos con stock bajo" : 
                String.join("\n", productosStockBajo));
            
            txtAltaDemanda.setText(altaDemanda.isEmpty() ? 
                "No hay productos con alta demanda" : 
                String.join("\n", altaDemanda));
            
        } catch (Exception e) {
            System.err.println("Error al cargar datos del dashboard: " + e.getMessage());
            e.printStackTrace();
        }
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

}
