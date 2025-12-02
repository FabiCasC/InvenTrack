package app.view;

import app.utils.AnimationUtils;
import app.utils.ColorConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;

public class DashboardView extends javax.swing.JFrame {

    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private JPanel sidebar;
    private JLabel currentSelected;
    private JInternalFrame currentFrame;

    public DashboardView() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("InvenTrack - Sistema de Gestión");
        setSize(1200, 700);
        setResizable(false);
        
        animateEntry();
        
        openInternalFrame(new DashboardInternal());
    }
    
    private void animateEntry() {
        // Animación simple sin usar setOpacity
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        // ===== MENU BAR =====
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuBar1.setBackground(ColorConstants.BLANCO_PURO);
        jMenuBar1.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorConstants.GRIS_CLARO));
        
        JMenu menuInvenTrack = new JMenu("InvenTrack");
        menuInvenTrack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        menuInvenTrack.setForeground(ColorConstants.GRIS_PIZARRA);
        jMenuBar1.add(menuInvenTrack);
        
        JMenu menuSalir = new JMenu("Usuario");
        menuSalir.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        menuSalir.setForeground(ColorConstants.GRIS_PIZARRA);
        
        jMenuItem2 = new JMenuItem("Cerrar Sesión");
        jMenuItem2.setIcon(new ImageIcon(getClass().getResource("/assets/icons/cerrar-sesion.png")));
        jMenuItem2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        jMenuItem2.addActionListener(e -> {
            animateExit(() -> {
                LoginView v = new LoginView();
                v.setVisible(true);
                dispose();
            });
        });
        menuSalir.add(jMenuItem2);
        jMenuBar1.add(menuSalir);
        
        setJMenuBar(jMenuBar1);
        
        // ===== PANEL PRINCIPAL =====
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ColorConstants.BLANCO_HUMO);
        
        // ===== SIDEBAR =====
        sidebar = new JPanel();
        sidebar.setBackground(ColorConstants.AZUL_PROFUNDO); // #1E3A8A
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Logo/Título
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        JLabel logoLabel = new JLabel("INVENTRACK");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(ColorConstants.AZUL_ACERO);
        logoPanel.add(logoLabel);
        sidebar.add(logoPanel);
        
        // Menú items
        sidebar.add(createMenuItem("Dashboard", "/assets/icons/cuadro-predictivo.png", new DashboardInternal(), true));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createMenuItem("Inventario", "/assets/icons/inventario (1).png", new InventarioInternal(), false));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createMenuItem("Movimientos", "/assets/icons/caja.png", new MovimientosInternal(), false));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createMenuItem("Pedidos", "/assets/icons/pedido-en-linea.png", new PedidosInternal(), false));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createMenuItem("Proveedores", "/assets/icons/proveedor.png", new ProveedoresInternal(), false));
        
        // Separador para algoritmos
        sidebar.add(Box.createVerticalStrut(15));
        
        JSeparator separador = new JSeparator();
        separador.setForeground(new Color(255, 255, 255, 50));
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(separador);
        sidebar.add(Box.createVerticalStrut(8));
        
        // RF4 - Algoritmos y Análisis
        JLabel lblSeccion = new JLabel("ALGORITMOS");
        lblSeccion.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSeccion.setForeground(new Color(200, 200, 220));
        lblSeccion.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
        sidebar.add(lblSeccion);
        
        sidebar.add(createMenuItem("Predicciones", "/assets/icons/cuadro-predictivo.png", new PrediccionesInternal(), false));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createMenuItem("Grafo Relaciones", "/assets/icons/proveedor.png", new GrafoInternal(), false));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createMenuItem("Alertas", "/assets/icons/pedido-en-linea.png", new AlertasInternal(), false));
        
        // Separador para reportes
        sidebar.add(Box.createVerticalStrut(15));
        
        JSeparator separadorReportes = new JSeparator();
        separadorReportes.setForeground(new Color(255, 255, 255, 50));
        separadorReportes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(separadorReportes);
        sidebar.add(Box.createVerticalStrut(8));
        
        // RF8 - Reportes
        JLabel lblSeccionReportes = new JLabel("REPORTES");
        lblSeccionReportes.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSeccionReportes.setForeground(new Color(200, 200, 220));
        lblSeccionReportes.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
        sidebar.add(lblSeccionReportes);
        
        sidebar.add(createMenuItem("Inventario Crítico", "/assets/icons/proveedor.png", new ReporteInventarioCriticoInternal(), false));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createMenuItem("Predicción", "/assets/icons/cuadro-predictivo.png", new ReportePrediccionInternal(), false));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createMenuItem("Movimientos", "/assets/icons/caja.png", new ReporteMovimientosInternal(), false));
        
        // Hacer sidebar scrolleable
        JScrollPane sidebarScroll = new JScrollPane(sidebar);
        sidebarScroll.setBorder(null);
        sidebarScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sidebarScroll.getVerticalScrollBar().setUnitIncrement(16);
        sidebarScroll.setPreferredSize(new Dimension(220, 700));
        sidebarScroll.getViewport().setBackground(ColorConstants.AZUL_PROFUNDO);
        
        // ===== DESKTOP PANE =====
        jDesktopPane1 = new JDesktopPane();
        jDesktopPane1.setBackground(ColorConstants.BLANCO_HUMO);
        
        mainPanel.add(sidebarScroll, BorderLayout.WEST);
        mainPanel.add(jDesktopPane1, BorderLayout.CENTER);
        
        getContentPane().add(mainPanel);
    }
    
    private JPanel createMenuItem(String text, String iconPath, JInternalFrame frame, boolean selected) {
        JPanel menuItem = new JPanel(new BorderLayout());
        menuItem.setOpaque(false);
        menuItem.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        contentPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel();
        ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
        iconLabel.setIcon(icon);
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        if (selected) {
            menuItem.setBackground(ColorConstants.AZUL_ACERO); // #3B82F6
            textLabel.setForeground(Color.WHITE);
            currentSelected = textLabel;
        } else {
            textLabel.setForeground(new Color(200, 200, 220)); // Texto claro sobre azul profundo
        }
        
        contentPanel.add(iconLabel);
        contentPanel.add(textLabel);
        menuItem.add(contentPanel, BorderLayout.WEST);
        
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (currentSelected != textLabel) {
                    AnimationUtils.animateColor(menuItem, menuItem.getBackground(), ColorConstants.SIDEBAR_HOVER, 200);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (currentSelected != textLabel) {
                    AnimationUtils.animateColor(menuItem, menuItem.getBackground(), ColorConstants.AZUL_PROFUNDO, 200);
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                openInternalFrame(frame);
                
                // Actualizar selección con animación
                if (currentSelected != null && currentSelected != textLabel) {
                    JPanel parent = (JPanel) currentSelected.getParent().getParent();
                    AnimationUtils.animateColor(parent, parent.getBackground(), ColorConstants.AZUL_PROFUNDO, 200);
                    currentSelected.setForeground(new Color(200, 200, 220));
                }
                
                AnimationUtils.animateColor(menuItem, menuItem.getBackground(), ColorConstants.AZUL_ACERO, 200);
                textLabel.setForeground(Color.WHITE);
                currentSelected = textLabel;
            }
        });
        
        return menuItem;
    }
    
    private void openInternalFrame(JInternalFrame frame) {
        if (currentFrame != null) {
            animateFrameTransition(() -> {
                jDesktopPane1.removeAll();
                frame.setSize(970, 650);
                frame.setLocation(5, 5);
                frame.setVisible(false);
                jDesktopPane1.add(frame);
                animateFrameIn(frame);
            });
        } else {
            frame.setSize(970, 650);
            frame.setLocation(5, 5);
            jDesktopPane1.add(frame);
            animateFrameIn(frame);
        }
        
        currentFrame = frame;
    }
    
    private void animateFrameTransition(Runnable onComplete) {
        if (currentFrame == null) {
            onComplete.run();
            return;
        }
        
        Timer fadeTimer = new Timer(16, null);
        long startTime = System.currentTimeMillis();
        
        fadeTimer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, elapsed / 200.0f);
            
            jDesktopPane1.repaint();
            
            if (progress >= 1.0f) {
                fadeTimer.stop();
                onComplete.run();
            }
        });
        fadeTimer.start();
    }
    
    private void animateFrameIn(JInternalFrame frame) {
        frame.setVisible(true);
        Point startPos = new Point(5, -50);
        Point endPos = new Point(5, 5);
        
        frame.setLocation(startPos);
        Timer slideTimer = new Timer(16, null);
        long startTime = System.currentTimeMillis();
        
        slideTimer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, elapsed / 300.0f);
            
            // Easing function
            float easedProgress = 1.0f - (float) Math.pow(1.0 - progress, 3);
            
            int y = (int) (startPos.y + (endPos.y - startPos.y) * easedProgress);
            frame.setLocation(5, y);
            frame.repaint();
            
            if (progress >= 1.0f) {
                slideTimer.stop();
                frame.toFront();
            }
        });
        slideTimer.start();
    }
    
    private void animateExit(Runnable onComplete) {
        Timer delayTimer = new Timer(100, e -> {
            onComplete.run();
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        java.awt.EventQueue.invokeLater(() -> {
            new DashboardView().setVisible(true);
        });
    }
}
