package app.view;

import app.model.algoritmos.ArbolDecision;
import app.service.AlertaInteligenteService;
import app.utils.ColorConstants;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * RF4.6 - Vista para mostrar Alertas Inteligentes del sistema
 */
public class AlertasInternal extends javax.swing.JInternalFrame {
    
    public JPanel panelAlertas;
    public JButton btnRefrescar;
    public JLabel lblContadorAlertas;
    
    private AlertaInteligenteService alertaService;
    
    public AlertasInternal() {
        setTitle("Alertas Inteligentes");
        setClosable(false);
        setResizable(false);
        setBorder(null);
        setSize(970, 650);
        
        this.alertaService = new AlertaInteligenteService();
        
        initComponents();
        cargarAlertas();
    }
    
    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(ColorConstants.BLANCO_HUMO);
        main.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel titulo = new JLabel("Alertas Inteligentes");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(ColorConstants.GRIS_PIZARRA);
        
        JLabel subtitulo = new JLabel("RF4.6 - Sistema de alertas basado en predicciones, patrones y dependencias críticas");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        
        // Contador y botón refrescar
        JPanel panelHeaderDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelHeaderDerecha.setOpaque(false);
        
        lblContadorAlertas = new JLabel("0 alertas");
        lblContadorAlertas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblContadorAlertas.setForeground(ColorConstants.GRIS_PIZARRA);
        
        btnRefrescar = new JButton("Refrescar Alertas");
        btnRefrescar.setBackground(ColorConstants.AZUL_ACERO);
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRefrescar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefrescar.addActionListener(e -> cargarAlertas());
        
        panelHeaderDerecha.add(lblContadorAlertas);
        panelHeaderDerecha.add(btnRefrescar);
        
        header.add(titulo, BorderLayout.WEST);
        header.add(subtitulo, BorderLayout.SOUTH);
        header.add(panelHeaderDerecha, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);
        
        // Panel de alertas
        panelAlertas = new JPanel();
        panelAlertas.setLayout(new BoxLayout(panelAlertas, BoxLayout.Y_AXIS));
        panelAlertas.setBackground(ColorConstants.BLANCO_HUMO);
        panelAlertas.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JScrollPane scrollAlertas = new JScrollPane(panelAlertas);
        scrollAlertas.setBorder(null);
        scrollAlertas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollAlertas.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollAlertas.setOpaque(false);
        scrollAlertas.getViewport().setOpaque(false);
        
        main.add(scrollAlertas, BorderLayout.CENTER);
        
        add(main);
    }
    
    private void cargarAlertas() {
        try {
            btnRefrescar.setEnabled(false);
            btnRefrescar.setText("Cargando...");
            
            panelAlertas.removeAll();
            
            // RF4.6 - Generar todas las alertas inteligentes
            List<ArbolDecision.Alerta> alertas = alertaService.generarAlertasInteligentes();
            
            if (alertas == null || alertas.isEmpty()) {
                mostrarSinAlertas();
                lblContadorAlertas.setText("0 alertas - Todo en orden");
                lblContadorAlertas.setForeground(ColorConstants.VERDE_ESMERALDA);
            } else {
                lblContadorAlertas.setText(alertas.size() + " alerta(s) detectada(s)");
                
                int criticas = 0;
                for (ArbolDecision.Alerta alerta : alertas) {
                    if ("CRITICO".equals(alerta.getNivel())) {
                        criticas++;
                    }
                    panelAlertas.add(crearTarjetaAlerta(alerta));
                    panelAlertas.add(Box.createVerticalStrut(15));
                }
                
                if (criticas > 0) {
                    lblContadorAlertas.setForeground(ColorConstants.ROJO_ALERTA);
                    lblContadorAlertas.setText(alertas.size() + " alertas (" + criticas + " críticas)");
                } else {
                    lblContadorAlertas.setForeground(ColorConstants.NARANJA_AMBAR);
                }
            }
            
            panelAlertas.revalidate();
            panelAlertas.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar alertas: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            btnRefrescar.setEnabled(true);
            btnRefrescar.setText("Refrescar Alertas");
        }
    }
    
    private void mostrarSinAlertas() {
        JPanel panelSinAlertas = new JPanel();
        panelSinAlertas.setLayout(new BoxLayout(panelSinAlertas, BoxLayout.Y_AXIS));
        panelSinAlertas.setBackground(ColorConstants.BLANCO_PURO);
        panelSinAlertas.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        panelSinAlertas.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel icono = new JLabel("✓");
        icono.setFont(new Font("Segoe UI", Font.BOLD, 72));
        icono.setForeground(ColorConstants.VERDE_ESMERALDA);
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel mensaje = new JLabel("No hay alertas activas");
        mensaje.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mensaje.setForeground(ColorConstants.GRIS_PIZARRA);
        mensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitulo = new JLabel("Todos los productos están en orden");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelSinAlertas.add(icono);
        panelSinAlertas.add(Box.createVerticalStrut(15));
        panelSinAlertas.add(mensaje);
        panelSinAlertas.add(Box.createVerticalStrut(5));
        panelSinAlertas.add(subtitulo);
        
        panelAlertas.add(Box.createVerticalGlue());
        panelAlertas.add(panelSinAlertas);
        panelAlertas.add(Box.createVerticalGlue());
    }
    
    private JPanel crearTarjetaAlerta(ArbolDecision.Alerta alerta) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(ColorConstants.BLANCO_PURO);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(obtenerColorNivel(alerta.getNivel()), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        // Header con nivel
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel lblProducto = new JLabel(alerta.getProductoNombre());
        lblProducto.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblProducto.setForeground(ColorConstants.GRIS_PIZARRA);
        
        JLabel lblNivel = new JLabel(alerta.getNivel());
        lblNivel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNivel.setForeground(obtenerColorNivel(alerta.getNivel()));
        lblNivel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(obtenerColorNivel(alerta.getNivel())),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        header.add(lblProducto, BorderLayout.WEST);
        header.add(lblNivel, BorderLayout.EAST);
        tarjeta.add(header);
        tarjeta.add(Box.createVerticalStrut(10));
        
        // Mensaje
        JLabel lblMensaje = new JLabel("<html>" + alerta.getMensaje() + "</html>");
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMensaje.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        lblMensaje.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(lblMensaje);
        tarjeta.add(Box.createVerticalStrut(10));
        
        // Acción recomendada
        JPanel panelAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelAccion.setOpaque(false);
        panelAccion.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblAccion = new JLabel("💡 " + alerta.getAccionRecomendada());
        lblAccion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAccion.setForeground(ColorConstants.GRIS_PIZARRA);
        
        panelAccion.add(lblAccion);
        tarjeta.add(panelAccion);
        
        // Fecha
        if (alerta.getFecha() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            JLabel lblFecha = new JLabel("Fecha: " + sdf.format(alerta.getFecha()));
            lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblFecha.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
            lblFecha.setAlignmentX(Component.LEFT_ALIGNMENT);
            tarjeta.add(Box.createVerticalStrut(5));
            tarjeta.add(lblFecha);
        }
        
        return tarjeta;
    }
    
    private Color obtenerColorNivel(String nivel) {
        if ("CRITICO".equals(nivel)) {
            return ColorConstants.ROJO_ALERTA;
        } else if ("ALTO".equals(nivel)) {
            return ColorConstants.NARANJA_AMBAR;
        } else if ("MEDIO".equals(nivel)) {
            return ColorConstants.AMARILLO_ADVERTENCIA;
        } else {
            return ColorConstants.GRIS_NEUTRO;
        }
    }
}

