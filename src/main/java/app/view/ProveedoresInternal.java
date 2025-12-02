package app.view;

import app.controller.ProveedorController;
import app.controller.ProductoController;
import app.model.Proveedores;
import app.model.Productos;
import app.utils.ColorConstants;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedoresInternal extends javax.swing.JInternalFrame {

    public JButton btnNuevoProveedor;
    public JLabel lblTotalProv;
    public JLabel lblActivos;
    public JLabel lblAlertas;
    public JPanel panelProveedores;
    
    private ProveedorController proveedorController;
    private ProductoController productoController;

    public ProveedoresInternal() {
        setTitle("Proveedores");
        setBorder(null);
        setClosable(true);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ColorConstants.BLANCO_HUMO);
        setSize(970, 650);

        this.proveedorController = new ProveedorController();
        this.productoController = new ProductoController();

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(ColorConstants.BLANCO_HUMO);
        add(main, BorderLayout.CENTER);

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ColorConstants.BLANCO_HUMO);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel titleBox = new JPanel();
        titleBox.setBackground(ColorConstants.BLANCO_HUMO);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Proveedores");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ColorConstants.GRIS_PIZARRA);

        JLabel subtitle = new JLabel("Gestión de proveedores de productos lácteos");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(subtitle);

        // Botón Nuevo Proveedor
        btnNuevoProveedor = new JButton("+ Nuevo Proveedor");
        btnNuevoProveedor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNuevoProveedor.setBackground(ColorConstants.AZUL_ACERO);
        btnNuevoProveedor.setForeground(Color.WHITE);
        btnNuevoProveedor.setFocusPainted(false);
        btnNuevoProveedor.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevoProveedor.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnNuevoProveedor.setPreferredSize(new Dimension(170, 35));
        btnNuevoProveedor.addActionListener(e -> abrirFormularioNuevo());

        header.add(titleBox, BorderLayout.WEST);
        header.add(btnNuevoProveedor, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        // CONTENT
        JPanel content = new JPanel();
        content.setBackground(ColorConstants.BLANCO_HUMO);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 20, 20, 20));
        main.add(content);

        // STATS
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setBackground(ColorConstants.BLANCO_HUMO);

        lblTotalProv = new JLabel("—");
        lblActivos = new JLabel("—");
        lblAlertas = new JLabel("—");

        statsRow.add(createStatCard("Total Proveedores", lblTotalProv, ColorConstants.AZUL_ACERO));
        statsRow.add(createStatCard("Proveedores Activos", lblActivos, ColorConstants.VERDE_EXITO));
        statsRow.add(createStatCard("En Alerta", lblAlertas, ColorConstants.AMARILLO_ADVERTENCIA));
        statsRow.add(createStatCard("Entregas Este Mes", new JLabel("—"), ColorConstants.CIAN));

        content.add(statsRow);
        content.add(Box.createVerticalStrut(15));

        // LISTA DE PROVEEDORES
        panelProveedores = new JPanel();
        panelProveedores.setBackground(ColorConstants.BLANCO_HUMO);
        panelProveedores.setLayout(new BoxLayout(panelProveedores, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(panelProveedores);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(ColorConstants.BLANCO_HUMO);

        content.add(scroll);
        
        // Cargar proveedores al inicializar
        cargarProveedores();
    }
    
    private void abrirFormularioNuevo() {
        ProveedorFormInternal form = new ProveedorFormInternal("nuevo");
        form.setSize(600, 450);
        form.setLocation(
            (getDesktopPane().getWidth() - form.getWidth()) / 2,
            (getDesktopPane().getHeight() - form.getHeight()) / 2
        );
        getDesktopPane().add(form);
        form.setVisible(true);
        
        // Recargar después de guardar
        form.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                cargarProveedores();
            }
        });
    }
    
    private void cargarProveedores() {
        try {
            // Limpiar panel
            panelProveedores.removeAll();
            
            // Cargar proveedores desde Firebase
            List<Proveedores> proveedores = proveedorController.listarProveedores();
            List<Productos> todosProductos = productoController.listarProductos();
            
            if (proveedores == null || proveedores.isEmpty()) {
                JLabel lblVacio = new JLabel("No hay proveedores registrados");
                lblVacio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                lblVacio.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
                lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
                panelProveedores.add(Box.createVerticalGlue());
                panelProveedores.add(lblVacio);
                panelProveedores.add(Box.createVerticalGlue());
                
                lblTotalProv.setText("0");
                lblActivos.setText("0");
                lblAlertas.setText("0");
            } else {
                int total = proveedores.size();
                int activos = 0;
                int enAlerta = 0;
                
                for (Proveedores proveedor : proveedores) {
                    // Obtener productos asociados al proveedor
                    List<String> productosNombre = new ArrayList<>();
                    if (todosProductos != null) {
                        for (Productos producto : todosProductos) {
                            if (proveedor.getProveedorId().equals(producto.getProveedorId())) {
                                productosNombre.add(producto.getNombre());
                            }
                        }
                    }
                    
                    // Información del proveedor
                    String nombre = proveedor.getNombre() != null ? proveedor.getNombre() : "Sin nombre";
                    String telefono = proveedor.getTelefono() != null && !proveedor.getTelefono().trim().isEmpty() 
                        ? proveedor.getTelefono() : "No disponible";
                    String email = proveedor.getEmail() != null ? proveedor.getEmail() : "No disponible";
                    String contacto = nombre; // Usar nombre como contacto
                    String direccion = "No disponible"; // No tenemos dirección en el modelo
                    
                    // Si tiene productos, está activo
                    String estado = !productosNombre.isEmpty() ? "Activo" : "Alerta";
                    if (estado.equals("Activo")) {
                        activos++;
                    } else {
                        enAlerta++;
                    }
                    
                    // Crear tarjeta
                    addProveedorCard(
                        nombre,
                        contacto,
                        telefono,
                        email,
                        direccion,
                        productosNombre.isEmpty() ? List.of("Sin productos") : productosNombre,
                        "N/A", // Última entrega
                        productosNombre.size(), // Total de entregas (productos)
                        85, // Confiabilidad (placeholder)
                        estado
                    );
                }
                
                // Actualizar estadísticas
                lblTotalProv.setText(String.valueOf(total));
                lblActivos.setText(String.valueOf(activos));
                lblAlertas.setText(String.valueOf(enAlerta));
            }
            
            panelProveedores.revalidate();
            panelProveedores.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar proveedores: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel();
        card.setBackground(ColorConstants.BLANCO_PURO);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
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

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(accentColor);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);

        return card;
    }

    public JPanel addProveedorCard(
            String nombreProveedor,
            String contacto,
            String telefono,
            String correo,
            String direccion,
            List<String> productos,
            String ultimaEntrega,
            int entregasTotales,
            int confiabilidad,
            String estado // "Activo" o "Alerta"
    ) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(ColorConstants.BLANCO_PURO);
        card.setBorder(new CompoundBorder(
            new LineBorder(ColorConstants.GRIS_CLARO, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // IZQUIERDA
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(ColorConstants.BLANCO_PURO);

        JLabel title = new JLabel(nombreProveedor);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(ColorConstants.GRIS_PIZARRA);

        JLabel lblContacto = new JLabel("Contacto: " + contacto);
        JLabel lblTel = new JLabel("📞 " + telefono);
        JLabel lblCorreo = new JLabel("✉ " + correo);
        JLabel lblDir = new JLabel("📍 " + direccion);

        lblContacto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDir.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        lblContacto.setForeground(ColorConstants.GRIS_PIZARRA);
        lblTel.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        lblCorreo.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        lblDir.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);

        // CHIPS DE PRODUCTOS
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        chips.setBackground(ColorConstants.BLANCO_PURO);

        for (String p : productos) {
            JLabel chip = new JLabel(p);
            chip.setOpaque(true);
            chip.setBackground(ColorConstants.GRIS_TENUE);
            chip.setForeground(ColorConstants.GRIS_PIZARRA);
            chip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            chip.setBorder(new EmptyBorder(4, 8, 4, 8));
            chips.add(chip);
        }

        left.add(title);
        left.add(Box.createVerticalStrut(10));
        left.add(lblContacto);
        left.add(lblTel);
        left.add(lblCorreo);
        left.add(lblDir);
        left.add(Box.createVerticalStrut(10));
        left.add(chips);

        // DERECHA
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(ColorConstants.BLANCO_PURO);
        right.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblEntrega = new JLabel("Última Entrega: " + ultimaEntrega);
        JLabel lblTotal = new JLabel("Entregas Totales: " + entregasTotales);
        JLabel lblConf = new JLabel("Confiabilidad: " + confiabilidad + "%");

        lblEntrega.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTotal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblConf.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        lblEntrega.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        lblTotal.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        lblConf.setForeground(ColorConstants.GRIS_PIZARRA);

        JLabel chipEstado = new JLabel(estado);
        chipEstado.setOpaque(true);
        chipEstado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chipEstado.setBorder(new EmptyBorder(5, 10, 5, 10));

        // Usar colores de alertas
        if (estado.equalsIgnoreCase("Activo")) {
            chipEstado.setBackground(ColorConstants.VERDE_EXITO);
            chipEstado.setForeground(Color.WHITE);
        } else {
            chipEstado.setBackground(ColorConstants.AMARILLO_ADVERTENCIA);
            chipEstado.setForeground(Color.WHITE);
        }

        right.add(lblEntrega);
        right.add(lblTotal);
        right.add(lblConf);
        right.add(Box.createVerticalStrut(10));
        right.add(chipEstado);

        // ARMADO
        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);

        panelProveedores.add(card);
        panelProveedores.add(Box.createVerticalStrut(15));
        panelProveedores.revalidate();

        return card;
    }
}
