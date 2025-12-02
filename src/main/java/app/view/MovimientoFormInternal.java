package app.view;

import app.controller.MovimientoController;
import app.controller.ProductoController;
import app.model.Productos;
import app.utils.ColorConstants;
import app.utils.SessionManager;
import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovimientoFormInternal extends javax.swing.JInternalFrame {

    public JComboBox<String> comboTipo;
    public JComboBox<String> comboProducto;
    public JTextField txtCantidad;
    public JTextField txtLote;
    public JTextField txtFecha;
    public JTextField txtFechaVencimiento;

    public JButton btnRegistrar;
    public JButton btnCancelar;

    private MovimientoController movimientoController;
    private ProductoController productoController;
    private Map<String, String> productosMap; // Nombre -> ProductoId
    private Map<String, Productos> productosData; // ProductoId -> Producto completo

    public MovimientoFormInternal() {
        this.movimientoController = new MovimientoController();
        this.productoController = new ProductoController();
        this.productosMap = new HashMap<>();
        this.productosData = new HashMap<>();

        setTitle("Nuevo Movimiento");
        setSize(600, 550);
        setClosable(true);
        setIconifiable(true);
        setResizable(false);

        initComponents();
        cargarProductos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel main = new JPanel();
        main.setBackground(ColorConstants.BLANCO_HUMO);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Scroll pane para contenido
        JScrollPane scrollPane = new JScrollPane(main);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Título
        JLabel titulo = new JLabel("Nuevo Movimiento");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(ColorConstants.GRIS_PIZARRA);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Registra una entrada o salida de inventario");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        main.add(titulo);
        main.add(Box.createVerticalStrut(5));
        main.add(subtitulo);
        main.add(Box.createVerticalStrut(20));

        // Tipo de Movimiento (RF3.1 y RF3.2)
        main.add(createLabel("Tipo de Movimiento *"));
        comboTipo = new JComboBox<>(new String[]{"Entrada", "Salida"});
        comboTipo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        comboTipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboTipo.addActionListener(e -> {
            // Mostrar/ocultar campo de lote según el tipo
            boolean esEntrada = comboTipo.getSelectedItem().equals("Entrada");
            txtLote.setEnabled(esEntrada);
            if (!esEntrada) {
                txtLote.setText("");
            }
        });
        main.add(comboTipo);
        main.add(Box.createVerticalStrut(12));

        // Producto (RF3.1 y RF3.2)
        main.add(createLabel("Producto *"));
        comboProducto = new JComboBox<>();
        comboProducto.addItem("Cargando productos...");
        comboProducto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        comboProducto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        main.add(comboProducto);
        main.add(Box.createVerticalStrut(12));

        // Cantidad (RF3.1 y RF3.2)
        main.add(createLabel("Cantidad *"));
        txtCantidad = createTextField();
        txtCantidad.setToolTipText("Ingrese la cantidad a mover");
        main.add(txtCantidad);
        main.add(Box.createVerticalStrut(12));

        // Número de Lote (solo para entradas)
        main.add(createLabel("Número de Lote (opcional para Entradas)"));
        txtLote = createTextField();
        txtLote.setEnabled(true);
        txtLote.putClientProperty("placeholderText", "Ej: LOT-PROD001-001");
        txtLote.setToolTipText("Opcional. Se generará automáticamente si se deja vacío");
        main.add(txtLote);
        main.add(Box.createVerticalStrut(12));

        // Fecha de Movimiento
        main.add(createLabel("Fecha del Movimiento * (dd/MM/yyyy)"));
        txtFecha = createTextField();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtFecha.setText(sdf.format(new Date()));
        txtFecha.setToolTipText("Formato: dd/MM/yyyy");
        main.add(txtFecha);
        main.add(Box.createVerticalStrut(12));

        // Fecha de Vencimiento (solo visible para entradas)
        main.add(createLabel("Fecha de Vencimiento (opcional para Entradas) (dd/MM/yyyy)"));
        txtFechaVencimiento = createTextField();
        txtFechaVencimiento.setEnabled(true);
        txtFechaVencimiento.putClientProperty("placeholderText", "Se calculará automáticamente si se deja vacío");
        txtFechaVencimiento.setToolTipText("Opcional. Se calculará automáticamente según el tipo de producto");
        
        // Hacer que solo sea visible/habilitado para entradas
        comboTipo.addActionListener(e -> {
            boolean esEntrada = comboTipo.getSelectedItem().equals("Entrada");
            txtFechaVencimiento.setEnabled(esEntrada);
            if (!esEntrada) {
                txtFechaVencimiento.setText("");
            }
        });
        
        main.add(txtFechaVencimiento);
        
        // Agregar contenido con scroll en el centro
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones fijo en la parte inferior (fuera del scroll)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBotones.setBackground(ColorConstants.BLANCO_HUMO);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        // Crear botones
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(ColorConstants.GRIS_NEUTRO);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dispose());

        btnRegistrar = new JButton("Registrar Movimiento");
        btnRegistrar.setBackground(ColorConstants.AZUL_ACERO);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.addActionListener(e -> registrarMovimiento());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnRegistrar);

        // Agregar botones fijos en la parte inferior
        add(panelBotones, BorderLayout.SOUTH);
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

    private void cargarProductos() {
        try {
            List<Productos> productos = productoController.listarProductos();
            comboProducto.removeAllItems();
            comboProducto.addItem("Selecciona un producto");
            productosMap.clear();
            productosData.clear();
            
            if (productos != null && !productos.isEmpty()) {
                for (Productos p : productos) {
                    String nombreCompleto = p.getNombre() + " (Stock: " + p.getStock_actual() + ")";
                    comboProducto.addItem(nombreCompleto);
                    productosMap.put(nombreCompleto, p.getProductoId());
                    productosData.put(p.getProductoId(), p);
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

    private void registrarMovimiento() {
        // Validaciones RF3.1 y RF3.2
        String tipoMovimiento = (String) comboTipo.getSelectedItem();
        String productoNombre = (String) comboProducto.getSelectedItem();
        String cantidadTxt = txtCantidad.getText().trim();
        String loteId = txtLote.getText().trim();

        // Validar tipo de movimiento
        if (tipoMovimiento == null || tipoMovimiento.equals("Selecciona un tipo")) {
            mostrarError("Debe seleccionar un tipo de movimiento");
            return;
        }

        // Validar producto
        if (productoNombre == null || productoNombre.equals("Selecciona un producto") ||
            productoNombre.equals("No hay productos registrados") ||
            productoNombre.equals("Error al cargar")) {
            mostrarError("Debe seleccionar un producto válido");
            return;
        }

        String productoId = productosMap.get(productoNombre);
        if (productoId == null) {
            mostrarError("Error: No se pudo obtener el ID del producto");
            return;
        }

        Productos producto = productosData.get(productoId);
        if (producto == null) {
            mostrarError("Error: No se encontraron datos del producto");
            return;
        }

        // Validar cantidad
        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadTxt);
            if (cantidad <= 0) {
                mostrarError("La cantidad debe ser mayor a 0");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("La cantidad debe ser un número válido");
            return;
        }

        // RF3.2 - Verificar stock disponible para salidas
        if (tipoMovimiento.equalsIgnoreCase("Salida")) {
            if (producto.getStock_actual() < cantidad) {
                mostrarError("Stock insuficiente. Stock disponible: " + producto.getStock_actual());
                return;
            }
        }

        // Obtener usuario actual
        String usuarioId = SessionManager.getInstance().getUsuarioId();
        if (usuarioId == null) {
            mostrarError("Error: No hay sesión de usuario activa. Por favor, inicie sesión nuevamente.");
            return;
        }

        // Obtener algoritmo del producto (se asignará automáticamente)
        String algoritmo = producto.getMetodo_rotacion();

        // Si el lote está vacío para entradas, se generará automáticamente
        if (loteId.isEmpty()) {
            loteId = null;
        }

        // Validar y parsear fecha de vencimiento (solo para entradas)
        Date fechaVencimiento = null;
        if (tipoMovimiento.equalsIgnoreCase("Entrada")) {
            String fechaVencimientoTxt = txtFechaVencimiento.getText().trim();
            if (!fechaVencimientoTxt.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    fechaVencimiento = sdf.parse(fechaVencimientoTxt);
                } catch (ParseException e) {
                    mostrarError("Formato de fecha de vencimiento inválido. Use el formato dd/MM/yyyy o déjelo vacío para cálculo automático.");
                    txtFechaVencimiento.requestFocus();
                    return;
                }
            }
        }

        // Registrar movimiento según el tipo
        try {
            btnRegistrar.setEnabled(false);
            btnRegistrar.setText("Registrando...");

            boolean exito;
            String tipoLowerCase = tipoMovimiento.toLowerCase();

            if (tipoLowerCase.equals("entrada")) {
                // RF3.1 - Registrar entrada (siempre crea un lote)
                exito = movimientoController.registrarEntrada(
                    productoId,
                    cantidad,
                    usuarioId,
                    algoritmo,
                    loteId,
                    fechaVencimiento
                );
            } else {
                // RF3.2 - Registrar salida
                exito = movimientoController.registrarSalida(
                    productoId,
                    cantidad,
                    usuarioId,
                    algoritmo
                );
            }

            if (exito) {
                JOptionPane.showMessageDialog(this, 
                    tipoMovimiento + " registrada exitosamente en Firebase", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (Exception e) {
            mostrarError("Error al registrar movimiento: " + e.getMessage());
        } finally {
            btnRegistrar.setEnabled(true);
            btnRegistrar.setText("Registrar Movimiento");
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Validación", JOptionPane.ERROR_MESSAGE);
    }
}
