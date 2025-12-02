package app.view;

import app.controller.LoginController;
import app.model.Usuarios;
import app.utils.AnimationUtils;
import app.utils.ColorConstants;
import app.utils.SessionManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;

public class LoginView extends javax.swing.JFrame {

    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtContraseña;
    private javax.swing.JButton btnIniciar;
    private javax.swing.JButton btnRegistrarse;
    private javax.swing.JLabel lblError;

    public LoginView() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("InvenTrack - Inicio de Sesión");
        setSize(1200, 700);
        setResizable(false);
        
        animateEntry();
        
        try {
            app.config.FirebaseConfig.getFirestore();
            System.out.println("Firebase inicializado desde LoginView");
        } catch (Exception e) {
            System.err.println("Error al inicializar Firebase: " + e.getMessage());
        }
    }
    
    private void animateEntry() {
        // Animación simple sin usar setOpacity (que requiere frame undecorated)
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        // Panel principal con layout dividido
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ColorConstants.BLANCO_HUMO);
        
        // ===== PANEL IZQUIERDO - Branding =====
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradiente usando Azul Profundo
                GradientPaint gradient = new GradientPaint(
                    0, 0, ColorConstants.AZUL_PROFUNDO,
                    0, getHeight(), new Color(0x1E40AF) // Azul más oscuro para gradiente
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setPreferredSize(new Dimension(500, 700));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(60, 50, 60, 50));
        
        // Logo/Icono
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/icons/inventario.png")));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setPreferredSize(new Dimension(120, 120));
        
        // Título
        JLabel titleLabel = new JLabel("INVENTRACK");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtítulo
        JLabel subtitleLabel = new JLabel("<html><center>Sistema de Gestión<br>de Inventario Inteligente</center></html>");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(230, 230, 255));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(iconLabel);
        leftPanel.add(Box.createVerticalStrut(30));
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(subtitleLabel);
        leftPanel.add(Box.createVerticalGlue());
        
        // ===== PANEL DERECHO - Formulario =====
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(ColorConstants.BLANCO_PURO);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(100, 80, 100, 80));
        
        // Título del formulario
        JLabel formTitle = new JLabel("Bienvenido");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        formTitle.setForeground(ColorConstants.GRIS_PIZARRA);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel formSubtitle = new JLabel("Inicia sesión para continuar");
        formSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formSubtitle.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        formSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        rightPanel.add(formTitle);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(formSubtitle);
        rightPanel.add(Box.createVerticalStrut(50));
        
        // Campo Email
        JLabel emailLabel = new JLabel("Correo electrónico");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setForeground(ColorConstants.GRIS_PIZARRA);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtEmail = createAnimatedTextField(400);
        txtEmail.addFocusListener(createFocusListener(txtEmail));
        
        rightPanel.add(emailLabel);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(txtEmail);
        rightPanel.add(Box.createVerticalStrut(25));
        
        // Campo Contraseña
        JLabel passwordLabel = new JLabel("Contraseña");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(ColorConstants.GRIS_PIZARRA);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtContraseña = createAnimatedPasswordField(400);
        txtContraseña.addFocusListener(createFocusListener(txtContraseña));
        txtContraseña.addActionListener(e -> btnIniciar.doClick());
        
        rightPanel.add(passwordLabel);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(txtContraseña);
        rightPanel.add(Box.createVerticalStrut(30));
        
        // Label de error
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(ColorConstants.ROJO_ALERTA);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(lblError);
        rightPanel.add(Box.createVerticalStrut(10));
        
        // Botón Iniciar Sesión
        btnIniciar = createAnimatedButton("Iniciar Sesión", ColorConstants.AZUL_ACERO, 400);
        btnIniciar.addActionListener(this::btnIniciarActionPerformed);
        btnIniciar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                AnimationUtils.animateColor(btnIniciar, ColorConstants.AZUL_ACERO, ColorConstants.AZUL_HOVER, 200);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                AnimationUtils.animateColor(btnIniciar, btnIniciar.getBackground(), ColorConstants.AZUL_ACERO, 200);
            }
        });
        
        rightPanel.add(btnIniciar);
        rightPanel.add(Box.createVerticalStrut(20));
        
        // Botón Registrarse
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerPanel.setOpaque(false);
        registerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel registerLabel = new JLabel("¿No tienes cuenta?");
        registerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerLabel.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        
        btnRegistrarse = new JButton("Regístrate aquí");
        btnRegistrarse.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrarse.setForeground(ColorConstants.AZUL_ACERO);
        btnRegistrarse.setContentAreaFilled(false);
        btnRegistrarse.setBorderPainted(false);
        btnRegistrarse.setFocusPainted(false);
        btnRegistrarse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrarse.addActionListener(this::btnRegistrarseActionPerformed);
        btnRegistrarse.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnRegistrarse.setForeground(ColorConstants.AZUL_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnRegistrarse.setForeground(ColorConstants.AZUL_ACERO);
            }
        });
        
        registerPanel.add(registerLabel);
        registerPanel.add(btnRegistrarse);
        rightPanel.add(registerPanel);
        rightPanel.add(Box.createVerticalGlue());
        
        // Agregar paneles al panel principal
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        getContentPane().add(mainPanel);
        pack();
    }
    
    private JTextField createAnimatedTextField(int width) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(width, 45));
        field.setMaximumSize(new Dimension(width, 45));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }
    
    private JPasswordField createAnimatedPasswordField(int width) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(width, 45));
        field.setMaximumSize(new Dimension(width, 45));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }
    
    private FocusListener createFocusListener(JComponent component) {
        return new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                component.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ColorConstants.AZUL_ACERO, 2),
                    BorderFactory.createEmptyBorder(8, 13, 8, 13)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                component.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        };
    }
    
    private JButton createAnimatedButton(String text, Color bgColor, int width) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setPreferredSize(new Dimension(width, 50));
        button.setMaximumSize(new Dimension(width, 50));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder());
        return button;
    }

    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {
        String email = txtEmail.getText().trim();
        String contraseña = new String(txtContraseña.getPassword());

        if (email.isEmpty() || contraseña.isEmpty()) {
            lblError.setText("Por favor, completa todos los campos");
            AnimationUtils.shake(txtEmail.getText().isEmpty() ? txtEmail : txtContraseña);
            return;
        }

        lblError.setText(" ");
        btnIniciar.setEnabled(false);
        btnIniciar.setText("Iniciando...");
        AnimationUtils.pulseButton(btnIniciar, ColorConstants.AZUL_ACERO);

        try {
            LoginController controller = new LoginController();
            Usuarios usuario = controller.iniciarSesion(email, contraseña);

            if (usuario != null) {
                // Guardar usuario en sesión
                SessionManager.getInstance().setUsuarioActual(usuario);
                
                // Animación de éxito
                AnimationUtils.pulseButton(btnIniciar, ColorConstants.VERDE_EXITO);
                btnIniciar.setText("¡Éxito!");
                
                Timer successTimer = new Timer(500, e -> {
                    DashboardView d = new DashboardView();
                    d.setVisible(true);
                    animateExit(() -> dispose());
                });
                successTimer.setRepeats(false);
                successTimer.start();
            } else {
                lblError.setText("Credenciales incorrectas. Intenta de nuevo.");
                AnimationUtils.shake(btnIniciar);
                btnIniciar.setEnabled(true);
                btnIniciar.setText("Iniciar Sesión");
            }
        } catch (Exception e) {
            lblError.setText("Error al conectar. Verifica tu conexión.");
            AnimationUtils.shake(btnIniciar);
            btnIniciar.setEnabled(true);
            btnIniciar.setText("Iniciar Sesión");
        }
    }
    
    private void animateExit(Runnable onComplete) {
        Timer delayTimer = new Timer(100, e -> {
            onComplete.run();
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    private void btnRegistrarseActionPerformed(java.awt.event.ActionEvent evt) {
        animateExit(() -> {
            RegistroView r = new RegistroView();
            r.setVisible(true);
            dispose();
        });
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        java.awt.EventQueue.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}
