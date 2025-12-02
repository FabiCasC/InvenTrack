package app.view;

import app.controller.RegistroController;
import app.utils.AnimationUtils;
import app.utils.ColorConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegistroView extends javax.swing.JFrame {

    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtContra;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JLabel lblError;

    public RegistroView() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("InvenTrack - Registro");
        setSize(1200, 700);
        setResizable(false);
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
                    0, getHeight(), new Color(0x1E40AF)
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
        iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/icons/nuevo.png")));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setPreferredSize(new Dimension(120, 120));
        
        // Título
        JLabel titleLabel = new JLabel("ÚNETE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtítulo
        JLabel subtitleLabel = new JLabel("<html><center>Crea tu cuenta y comienza<br>a gestionar tu inventario</center></html>");
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
        rightPanel.setBorder(BorderFactory.createEmptyBorder(80, 80, 80, 80));
        
        // Título del formulario
        JLabel formTitle = new JLabel("Crear Cuenta");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        formTitle.setForeground(ColorConstants.GRIS_PIZARRA);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel formSubtitle = new JLabel("Completa los datos para registrarte");
        formSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formSubtitle.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        formSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        rightPanel.add(formTitle);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(formSubtitle);
        rightPanel.add(Box.createVerticalStrut(40));
        
        // Campo Nombre
        JLabel nombreLabel = new JLabel("Nombre completo");
        nombreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nombreLabel.setForeground(ColorConstants.GRIS_PIZARRA);
        nombreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtNombre = createTextField(400);
        txtNombre.addFocusListener(createFocusListener(txtNombre));
        
        rightPanel.add(nombreLabel);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(txtNombre);
        rightPanel.add(Box.createVerticalStrut(20));
        
        // Campo Email
        JLabel emailLabel = new JLabel("Correo electrónico");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setForeground(ColorConstants.GRIS_PIZARRA);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtEmail = createTextField(400);
        txtEmail.addFocusListener(createFocusListener(txtEmail));
        
        rightPanel.add(emailLabel);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(txtEmail);
        rightPanel.add(Box.createVerticalStrut(20));
        
        // Campo Contraseña
        JLabel passwordLabel = new JLabel("Contraseña");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(ColorConstants.GRIS_PIZARRA);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtContra = createPasswordField(400);
        txtContra.addFocusListener(createFocusListener(txtContra));
        txtContra.addActionListener(e -> btnRegistrar.doClick());
        
        rightPanel.add(passwordLabel);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(txtContra);
        rightPanel.add(Box.createVerticalStrut(30));
        
        // Label de error
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(ColorConstants.ROJO_ALERTA);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(lblError);
        rightPanel.add(Box.createVerticalStrut(10));
        
        // Botón Registrarse
        btnRegistrar = new JButton("Registrarse");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setBackground(ColorConstants.AZUL_ACERO);
        btnRegistrar.setPreferredSize(new Dimension(400, 50));
        btnRegistrar.setMaximumSize(new Dimension(400, 50));
        btnRegistrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.setBorder(BorderFactory.createEmptyBorder());
        btnRegistrar.addActionListener(this::btnRegistrarActionPerformed);
        
        // Efecto hover
        btnRegistrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                AnimationUtils.animateColor(btnRegistrar, ColorConstants.AZUL_ACERO, ColorConstants.AZUL_HOVER, 200);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                AnimationUtils.animateColor(btnRegistrar, btnRegistrar.getBackground(), ColorConstants.AZUL_ACERO, 200);
            }
        });
        
        rightPanel.add(btnRegistrar);
        rightPanel.add(Box.createVerticalStrut(20));
        
        // Botón Volver a Login
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginPanel.setOpaque(false);
        loginPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel loginLabel = new JLabel("¿Ya tienes cuenta?");
        loginLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginLabel.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        
        JButton btnVolverLogin = new JButton("Inicia sesión aquí");
        btnVolverLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVolverLogin.setForeground(ColorConstants.AZUL_ACERO);
        btnVolverLogin.setContentAreaFilled(false);
        btnVolverLogin.setBorderPainted(false);
        btnVolverLogin.setFocusPainted(false);
        btnVolverLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolverLogin.addActionListener(e -> {
            LoginView l = new LoginView();
            l.setVisible(true);
            dispose();
        });
        btnVolverLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnVolverLogin.setForeground(ColorConstants.AZUL_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnVolverLogin.setForeground(ColorConstants.AZUL_ACERO);
            }
        });
        
        loginPanel.add(loginLabel);
        loginPanel.add(btnVolverLogin);
        rightPanel.add(loginPanel);
        rightPanel.add(Box.createVerticalGlue());
        
        // Agregar paneles al panel principal
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        getContentPane().add(mainPanel);
        pack();
    }
    
    private JTextField createTextField(int width) {
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
    
    private JPasswordField createPasswordField(int width) {
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

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String contraseña = new String(txtContra.getPassword());

        if (nombre.isEmpty() || email.isEmpty() || contraseña.isEmpty()) {
            lblError.setText("Por favor, completa todos los campos");
            AnimationUtils.shake(txtNombre.getText().isEmpty() ? txtNombre : (txtEmail.getText().isEmpty() ? txtEmail : txtContra));
            return;
        }

        lblError.setText(" ");
        btnRegistrar.setEnabled(false);
        btnRegistrar.setText("Registrando...");
        AnimationUtils.pulseButton(btnRegistrar, ColorConstants.AZUL_ACERO);

        try {
            RegistroController controller = new RegistroController();
            boolean exitoso = controller.registrarUsuario(nombre, email, contraseña, contraseña);

            if (exitoso) {
                AnimationUtils.pulseButton(btnRegistrar, ColorConstants.VERDE_EXITO);
                btnRegistrar.setText("¡Éxito!");
                
                Timer successTimer = new Timer(500, e -> {
                    JOptionPane.showMessageDialog(this, 
                        "¡Cuenta creada exitosamente!", 
                        "Registro exitoso", 
                        JOptionPane.INFORMATION_MESSAGE);
                    new LoginView().setVisible(true);
                    dispose();
                });
                successTimer.setRepeats(false);
                successTimer.start();
            } else {
                lblError.setText("Error al registrar. Verifica los datos e intenta de nuevo.");
                AnimationUtils.shake(btnRegistrar);
                btnRegistrar.setEnabled(true);
                btnRegistrar.setText("Registrarse");
            }
        } catch (Exception e) {
            lblError.setText("Error al conectar. Verifica tu conexión.");
            AnimationUtils.shake(btnRegistrar);
            btnRegistrar.setEnabled(true);
            btnRegistrar.setText("Registrarse");
        }
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        java.awt.EventQueue.invokeLater(() -> {
            new RegistroView().setVisible(true);
        });
    }
}
