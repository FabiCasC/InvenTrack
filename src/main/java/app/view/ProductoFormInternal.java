package app.view;

import app.controller.ProductoController;
import app.controller.ProveedorController;
import app.model.Productos;
import app.model.Proveedores;
import app.utils.ColorConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoFormInternal extends javax.swing.JInternalFrame {

    public JTextField txtNombreProducto;
    public JComboBox<String> cbCategoria; // Perecible / No Perecible
    public JTextField txtStockActual;
    public JTextField txtStockMinimo;
    public JTextField txtStockMaximo;
    public JComboBox<String> cbProveedor;
    public JTextField txtFechaIngreso;

    public JButton btnCancelar;
    public JButton btnGuardar;

    private final String modo;
    private String productoIdEditar; // ID del producto a editar
    private DefaultTableModel inventarioModel;
    private ProductoController productoController;
    private ProveedorController proveedorController;
    private Map<String, String> proveedoresMap; // Mapa: nombre -> ID

    public ProductoFormInternal(String modo) {
        this(modo, null);
    }
    
    public ProductoFormInternal(String modo, DefaultTableModel model) {
        this.modo = modo;
        this.inventarioModel = model;
        this.productoController = new ProductoController();
        this.proveedorController = new ProveedorController();
        this.proveedoresMap = new HashMap<>();

        setTitle(modo.equals("nuevo") ? "Nuevo Producto" : "Editar Producto");
        setSize(600, 620);
        setClosable(true);
        setIconifiable(true);
        setResizable(false);
        
        initComponents();
        cargarProveedores();
        
        // Si es edición, cargar datos del producto
        if (modo.equals("editar") && model != null) {
            // La carga de datos se hará desde InventarioInternal
        }
    }

    private void initComponents() {
        JPanel main = new JPanel();
        main.setBackground(ColorConstants.BLANCO_HUMO);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titulo = new JLabel(
                modo.equals("nuevo") ? "Nuevo Producto" : "Editar Producto"
        );
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(ColorConstants.GRIS_PIZARRA);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Completa la información del producto");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        main.add(titulo);
        main.add(Box.createVerticalStrut(5));
        main.add(subtitulo);
        main.add(Box.createVerticalStrut(20));

        // Nombre del Producto (RF2.1 - Obligatorio)
        main.add(createLabel("Nombre del Producto *"));
        txtNombreProducto = createTextField();
        main.add(txtNombreProducto);
        main.add(Box.createVerticalStrut(12));

        // Categoría (RF2.1 - Obligatorio: perecible / no perecible)
        main.add(createLabel("Categoría *"));
        cbCategoria = new JComboBox<>();
        cbCategoria.addItem("Selecciona una categoría");
        cbCategoria.addItem("Perecible");
        cbCategoria.addItem("No Perecible");
        cbCategoria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        main.add(cbCategoria);
        
        // Indicador del método de rotación automático
        JLabel lblMetodoRotacion = new JLabel(" ");
        lblMetodoRotacion.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblMetodoRotacion.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
        lblMetodoRotacion.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(Box.createVerticalStrut(5));
        main.add(lblMetodoRotacion);
        
        // Listener para mostrar el método de rotación asignado automáticamente
        cbCategoria.addActionListener(e -> {
            String categoria = (String) cbCategoria.getSelectedItem();
            if (categoria != null && !categoria.equals("Selecciona una categoría")) {
                String metodo = determinarMetodoRotacion(categoria);
                if (metodo != null) {
                    lblMetodoRotacion.setText("ℹ️ Método de rotación automático: " + metodo);
                    lblMetodoRotacion.setForeground(new Color(0x3B82F6)); // Azul acero
                } else {
                    lblMetodoRotacion.setText(" ");
                }
            } else {
                lblMetodoRotacion.setText(" ");
            }
        });
        
        main.add(Box.createVerticalStrut(12));

        // Stock Actual - Solo lectura (se calcula desde lotes)
        if (modo.equals("nuevo")) {
            // Para productos nuevos, el stock siempre inicia en 0
            // El stock se agrega mediante movimientos de entrada que crean lotes
            JLabel lblStockInfo = new JLabel("El stock inicial será 0. Agregue stock mediante movimientos de entrada.");
            lblStockInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblStockInfo.setForeground(ColorConstants.GRIS_TEXTO_SECUNDARIO);
            lblStockInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
            main.add(createLabel("Stock Actual: 0 (se agrega mediante lotes)"));
            txtStockActual = createTextField();
            txtStockActual.setText("0");
            txtStockActual.setEnabled(false); // Deshabilitado para productos nuevos
            txtStockActual.setBackground(ColorConstants.GRIS_CLARO);
            txtStockActual.setToolTipText("El stock se agrega mediante movimientos de entrada que crean lotes");
            main.add(txtStockActual);
            main.add(Box.createVerticalStrut(5));
            main.add(lblStockInfo);
            main.add(Box.createVerticalStrut(12));
        } else {
            // Para edición, mostrar el stock actual (solo informativo, calculado desde lotes)
            main.add(createLabel("Stock Actual (calculado desde lotes)"));
            txtStockActual = createTextField();
            txtStockActual.setEnabled(false); // Solo lectura
            txtStockActual.setBackground(ColorConstants.GRIS_CLARO);
            txtStockActual.setToolTipText("El stock se calcula automáticamente desde la suma de todos los lotes");
            main.add(txtStockActual);
            main.add(Box.createVerticalStrut(12));
        }

        // Stock Mínimo
        main.add(createLabel("Stock Mínimo"));
        txtStockMinimo = createTextField();
        txtStockMinimo.setText("0");
        main.add(txtStockMinimo);
        main.add(Box.createVerticalStrut(12));

        // Stock Máximo
        main.add(createLabel("Stock Máximo"));
        txtStockMaximo = createTextField();
        txtStockMaximo.setText("100");
        main.add(txtStockMaximo);
        main.add(Box.createVerticalStrut(12));

        // Proveedor (RF2.1 - Obligatorio)
        main.add(createLabel("Proveedor *"));
        cbProveedor = new JComboBox<>();
        cbProveedor.addItem("Cargando proveedores...");
        cbProveedor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbProveedor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        main.add(cbProveedor);
        main.add(Box.createVerticalStrut(12));

        // Fecha de Ingreso (RF2.1 - Obligatorio)
        main.add(createLabel("Fecha de Ingreso * (dd/MM/yyyy)"));
        txtFechaIngreso = createTextField();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtFechaIngreso.setText(sdf.format(new Date()));
        txtFechaIngreso.setToolTipText("Formato: dd/MM/yyyy");
        main.add(txtFechaIngreso);
        main.add(Box.createVerticalStrut(20));

        // Botones
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(ColorConstants.GRIS_NEUTRO);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dispose());

        btnGuardar = new JButton(
                modo.equals("nuevo") ? "Guardar Producto" : "Actualizar Producto"
        );
        btnGuardar.setBackground(ColorConstants.AZUL_ACERO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarProducto());

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

    private void cargarProveedores() {
        try {
            List<Proveedores> proveedores = proveedorController.listarProveedores();
            cbProveedor.removeAllItems();
            cbProveedor.addItem("Selecciona un proveedor");
            proveedoresMap.clear();
            
            if (proveedores != null && !proveedores.isEmpty()) {
                for (Proveedores p : proveedores) {
                    cbProveedor.addItem(p.getNombre());
                    proveedoresMap.put(p.getNombre(), p.getProveedorId());
                }
            } else {
                cbProveedor.addItem("No hay proveedores registrados");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar proveedores: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            cbProveedor.addItem("Error al cargar");
        }
    }

    private void guardarProducto() {
        // Validaciones RF2.1 - Campos obligatorios
        String nombre = txtNombreProducto.getText().trim();
        String categoria = (String) cbCategoria.getSelectedItem();
        String proveedorNombre = (String) cbProveedor.getSelectedItem();
        String fechaIngresoTxt = txtFechaIngreso.getText().trim();

        // Validar campos obligatorios
        if (nombre.isEmpty()) {
            mostrarError("El nombre del producto es obligatorio");
            txtNombreProducto.requestFocus();
            return;
        }

        if (categoria == null || categoria.equals("Selecciona una categoría")) {
            mostrarError("Debe seleccionar una categoría (Perecible o No Perecible)");
            cbCategoria.requestFocus();
            return;
        }

        if (proveedorNombre == null || proveedorNombre.equals("Selecciona un proveedor") || 
            proveedorNombre.equals("Cargando proveedores...") || 
            proveedorNombre.equals("No hay proveedores registrados") ||
            proveedorNombre.equals("Error al cargar")) {
            mostrarError("Debe seleccionar un proveedor válido");
            cbProveedor.requestFocus();
            return;
        }

        if (fechaIngresoTxt.isEmpty()) {
            mostrarError("La fecha de ingreso es obligatoria");
            txtFechaIngreso.requestFocus();
            return;
        }

        // Validar y parsear fecha
        Date fechaIngreso;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            fechaIngreso = sdf.parse(fechaIngresoTxt);
        } catch (ParseException e) {
            mostrarError("Formato de fecha inválido. Use el formato dd/MM/yyyy");
            txtFechaIngreso.requestFocus();
            return;
        }

        // Validar y parsear stocks
        int stockActual, stockMinimo, stockMaximo;
        try {
            // Para productos nuevos, el stock siempre es 0 (se agrega mediante lotes)
            if (modo.equals("nuevo")) {
                stockActual = 0; // Siempre 0 para productos nuevos
            } else {
                // Para edición, leer el valor (pero no se puede editar directamente)
                stockActual = Integer.parseInt(txtStockActual.getText().trim());
            }
            
            stockMinimo = txtStockMinimo.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtStockMinimo.getText().trim());
            stockMaximo = txtStockMaximo.getText().trim().isEmpty() ? 100 : Integer.parseInt(txtStockMaximo.getText().trim());
            
            if (stockActual < 0) {
                mostrarError("El stock actual no puede ser negativo");
                return;
            }
            if (stockMinimo < 0) {
                mostrarError("El stock mínimo no puede ser negativo");
                return;
            }
            if (stockMaximo <= stockMinimo) {
                mostrarError("El stock máximo debe ser mayor al stock mínimo");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("Los valores de stock deben ser números válidos");
            return;
        }

        // Obtener ID del proveedor
        String proveedorId = proveedoresMap.get(proveedorNombre);
        if (proveedorId == null) {
            mostrarError("Error: No se pudo obtener el ID del proveedor");
            return;
        }

        // Determinar método de rotación automáticamente según la categoría
        // Perecible → FIFO, No Perecible → LIFO
        String metodoRotacion = determinarMetodoRotacion(categoria);

        // Guardar en Firebase según el modo
        try {
            btnGuardar.setEnabled(false);
            btnGuardar.setText("Guardando...");

            if (modo.equals("nuevo")) {
                // RF2.1 - Registrar nuevo producto
                boolean exito = productoController.registrarProducto(
                    nombre,
                    categoria, // tipo/categoría
                    metodoRotacion,
                    stockActual,
                    stockMinimo,
                    stockMaximo,
                    proveedorId,
                    fechaIngreso,
                    null // Lote inicial - se puede agregar después
                );

                if (exito) {
                    JOptionPane.showMessageDialog(this, 
                        "Producto registrado exitosamente en Firebase", 
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Actualizar tabla si existe
                    if (inventarioModel != null) {
                        recargarInventario();
                    }
                    
                    dispose();
                }
            } else {
                // RF2.2 - Editar producto existente
                if (productoIdEditar == null) {
                    mostrarError("No se puede editar: ID de producto no especificado");
                    return;
                }

                boolean exito = productoController.actualizarProducto(
                    productoIdEditar,
                    nombre,
                    categoria,
                    metodoRotacion,
                    stockActual,
                    stockMinimo,
                    stockMaximo,
                    proveedorId
                );

                if (exito) {
                    JOptionPane.showMessageDialog(this, 
                        "Producto actualizado exitosamente en Firebase", 
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Actualizar tabla si existe
                    if (inventarioModel != null) {
                        recargarInventario();
                    }
                    
                    dispose();
                }
            }
        } catch (Exception e) {
            mostrarError("Error al guardar producto: " + e.getMessage());
        } finally {
            btnGuardar.setEnabled(true);
            btnGuardar.setText(modo.equals("nuevo") ? "Guardar Producto" : "Actualizar Producto");
        }
    }

    private void recargarInventario() {
        // Este método será llamado desde InventarioInternal para recargar la tabla
        // Por ahora solo cerramos el formulario
    }

    public void cargarDatosProducto(Productos producto) {
        if (producto == null) return;
        
        productoIdEditar = producto.getProductoId();
        txtNombreProducto.setText(producto.getNombre());
        
        // Cargar categoría (esto activará automáticamente el mensaje del método de rotación)
        if (producto.getTipo() != null) {
            cbCategoria.setSelectedItem(producto.getTipo());
            // El listener del combo ya actualizará el label del método de rotación
        }
        
        txtStockActual.setText(String.valueOf(producto.getStock_actual()));
        txtStockMinimo.setText(String.valueOf(producto.getStock_minimo()));
        txtStockMaximo.setText(String.valueOf(producto.getStock_maximo()));
        
        // Cargar fecha de ingreso
        if (producto.getFechaIngreso() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txtFechaIngreso.setText(sdf.format(producto.getFechaIngreso()));
        }
        
        // Cargar proveedor (necesitamos buscar el nombre)
        if (producto.getProveedorId() != null) {
            cargarProveedores(); // Recargar primero
            // Buscar el proveedor por ID
            for (Map.Entry<String, String> entry : proveedoresMap.entrySet()) {
                if (entry.getValue().equals(producto.getProveedorId())) {
                    cbProveedor.setSelectedItem(entry.getKey());
                    break;
                }
            }
        }
    }

    /**
     * Determina automáticamente el método de rotación según la categoría del producto
     * @param categoria "Perecible" o "No Perecible"
     * @return "FIFO" para Perecible, "LIFO" para No Perecible
     */
    private String determinarMetodoRotacion(String categoria) {
        if (categoria == null) {
            return null;
        }
        
        if (categoria.equalsIgnoreCase("Perecible")) {
            return "FIFO"; // First In First Out - productos que vencen
        } else if (categoria.equalsIgnoreCase("No Perecible")) {
            return "LIFO"; // Last In First Out - productos apilados
        }
        
        return null;
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Validación", JOptionPane.ERROR_MESSAGE);
    }

}
