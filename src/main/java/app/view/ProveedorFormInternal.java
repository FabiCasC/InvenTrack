package app.view;

import app.controller.ProveedorController;
import app.model.Proveedores;
import app.utils.ColorConstants;
import javax.swing.*;
import java.awt.*;

public class ProveedorFormInternal extends javax.swing.JInternalFrame {

    public JTextField txtNombre;
    public JTextField txtTelefono;
    public JTextField txtEmail;
    public JButton btnCancelar;
    public JButton btnGuardar;

    private final String modo;
    private String proveedorIdEditar;
    private ProveedorController proveedorController;

    public ProveedorFormInternal(String modo) {
        this(modo, null);
    }

    public ProveedorFormInternal(String modo, Proveedores proveedorEditar) {
        this.modo = modo;
        this.proveedorController = new ProveedorController();

        setTitle(modo.equals("nuevo") ? "Nuevo Proveedor" : "Editar Proveedor");
        setSize(600, 450);
        setClosable(true);
        setIconifiable(true);
        setResizable(false);

        if (modo.equals("editar") && proveedorEditar != null) {
            this.proveedorIdEditar = proveedorEditar.getProveedorId();
        }

        initComponents();

        // Si es edición, cargar datos
        if (modo.equals("editar") && proveedorEditar != null) {
            cargarDatosProveedor(proveedorEditar);
        }
    }

    private void initComponents() {
        JPanel main = new JPanel();
        main.setBackground(ColorConstants.BLANCO_HUMO);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titulo = new JLabel(
                modo.equals("nuevo") ? "Nuevo Proveedor" : "Editar Proveedor"
        );
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(ColorConstants.GRIS_PIZARRA);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Completa la información del proveedor");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        main.add(titulo);
        main.add(Box.createVerticalStrut(5));
        main.add(subtitulo);
        main.add(Box.createVerticalStrut(20));

        // Nombre del Proveedor (Obligatorio)
        main.add(createLabel("Nombre del Proveedor *"));
        txtNombre = createTextField();
        main.add(txtNombre);
        main.add(Box.createVerticalStrut(15));

        // Teléfono
        main.add(createLabel("Teléfono"));
        txtTelefono = createTextField();
        main.add(txtTelefono);
        main.add(Box.createVerticalStrut(15));

        // Email (Obligatorio)
        main.add(createLabel("Email *"));
        txtEmail = createTextField();
        main.add(txtEmail);
        main.add(Box.createVerticalStrut(30));

        // Botones
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.RIGHT_ALIGNMENT);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(ColorConstants.GRIS_NEUTRO);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dispose());

        btnGuardar = new JButton(
                modo.equals("nuevo") ? "Guardar Proveedor" : "Actualizar Proveedor"
        );
        btnGuardar.setBackground(ColorConstants.AZUL_ACERO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarProveedor());

        buttons.add(btnCancelar);
        buttons.add(btnGuardar);
        main.add(buttons);

        add(main);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ColorConstants.GRIS_PIZARRA);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorConstants.GRIS_CLARO),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private void cargarDatosProveedor(Proveedores proveedor) {
        txtNombre.setText(proveedor.getNombre() != null ? proveedor.getNombre() : "");
        txtTelefono.setText(proveedor.getTelefono() != null ? proveedor.getTelefono() : "");
        txtEmail.setText(proveedor.getEmail() != null ? proveedor.getEmail() : "");
    }

    private void guardarProveedor() {
        // Validaciones
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();

        // Validar campos obligatorios
        if (nombre.isEmpty()) {
            mostrarError("El nombre del proveedor es obligatorio");
            txtNombre.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            mostrarError("El email del proveedor es obligatorio");
            txtEmail.requestFocus();
            return;
        }

        // Validar formato básico de email
        if (!email.contains("@") || !email.contains(".")) {
            mostrarError("El email no tiene un formato válido");
            txtEmail.requestFocus();
            return;
        }

        // Guardar o actualizar
        boolean exito;
        if (modo.equals("nuevo")) {
            exito = proveedorController.registrarProveedor(nombre, telefono, email);
        } else {
            exito = proveedorController.actualizarProveedor(proveedorIdEditar, nombre, telefono, email);
        }

        if (exito) {
            dispose();
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

