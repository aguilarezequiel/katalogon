package UI;

import Service.VentaService;
import Service.ArticuloService;
import Entities.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VentasFrame extends JInternalFrame {
    
    private VentaService ventaService;
    private ArticuloService articuloService;
    
    private JTable tablaVentas;
    private DefaultTableModel modeloTablaVentas;
    
    private JTable tablaDetalle;
    private DefaultTableModel modeloTablaDetalle;
    
    private JComboBox<Articulo> cmbArticulo;
    private JSpinner spnCantidad;
    private JTextField txtPrecio;
    private JLabel lblTotal;
    
    private List<VentaArticulo> detalleVenta;
    
    public VentasFrame() {
        super("Registro de Ventas", true, true, true, true);
        ventaService = new VentaService();
        articuloService = new ArticuloService();
        detalleVenta = new ArrayList<>();
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        setSize(900, 600);
        setLayout(new BorderLayout());
        
        // Panel superior - Nueva venta
        JPanel panelNuevaVenta = createNuevaVentaPanel();
        add(panelNuevaVenta, BorderLayout.NORTH);
        
        // Panel central - Lista de ventas
        JPanel panelVentas = createVentasPanel();
        add(panelVentas, BorderLayout.CENTER);
    }
    
    private JPanel createNuevaVentaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Nueva Venta"));
        
        // Panel de entrada
        JPanel panelEntrada = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        panelEntrada.add(new JLabel("Artículo:"));
        cmbArticulo = new JComboBox<>();
        cmbArticulo.setPreferredSize(new Dimension(250, 25));
        cmbArticulo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Articulo) {
                    setText(((Articulo) value).getDescripcionArticulo());
                }
                return this;
            }
        });
        cmbArticulo.addActionListener(e -> actualizarPrecio());
        panelEntrada.add(cmbArticulo);
        
        panelEntrada.add(new JLabel("Cantidad:"));
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        spnCantidad.setPreferredSize(new Dimension(80, 25));
        panelEntrada.add(spnCantidad);
        
        panelEntrada.add(new JLabel("Precio:"));
        txtPrecio = new JTextField(10);
        // CAMBIO: Hacer el campo editable para que se pueda ingresar precio manualmente
        txtPrecio.setEditable(true);
        panelEntrada.add(txtPrecio);
        
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> agregarArticulo());
        panelEntrada.add(btnAgregar);
        
        JButton btnQuitar = new JButton("Quitar");
        btnQuitar.addActionListener(e -> quitarArticulo());
        panelEntrada.add(btnQuitar);
        
        panel.add(panelEntrada, BorderLayout.NORTH);
        
        // Tabla de detalle
        String[] columnas = {"Artículo", "Cantidad", "Precio Unit.", "Subtotal"};
        modeloTablaDetalle = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaDetalle = new JTable(modeloTablaDetalle);
        JScrollPane scrollDetalle = new JScrollPane(tablaDetalle);
        scrollDetalle.setPreferredSize(new Dimension(0, 150));
        panel.add(scrollDetalle, BorderLayout.CENTER);
        
        // Panel de total y botones
        JPanel panelInferior = new JPanel(new BorderLayout());
        
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.add(new JLabel("TOTAL:"));
        lblTotal = new JLabel("$ 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        panelTotal.add(lblTotal);
        panelInferior.add(panelTotal, BorderLayout.NORTH);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnRegistrar = new JButton("Registrar Venta");
        btnRegistrar.addActionListener(e -> registrarVenta());
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarVenta());
        
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnLimpiar);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        
        panel.add(panelInferior, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createVentasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Historial de Ventas"));
        
        String[] columnas = {"ID", "Fecha/Hora", "Total", "Cantidad Items"};
        modeloTablaVentas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaVentas = new JTable(modeloTablaVentas);
        tablaVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tablaVentas);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnVerDetalle = new JButton("Ver Detalle");
        btnVerDetalle.addActionListener(e -> verDetalleVenta());
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizarTablaVentas());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnVerDetalle);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCerrar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void cargarDatos() {
        cargarArticulos();
        actualizarTablaVentas();
    }
    
    private void cargarArticulos() {
        try {
            cmbArticulo.removeAllItems();
            List<Articulo> articulos = articuloService.obtenerTodos();
            for (Articulo a : articulos) {
                if (a.getStockActual() > 0) {
                    cmbArticulo.addItem(a);
                }
            }
            
            // CAMBIO: Actualizar precio al cargar la primera vez
            if (cmbArticulo.getItemCount() > 0) {
                actualizarPrecio();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar artículos: " + e.getMessage());
        }
    }
    
    private void actualizarPrecio() {
        Articulo articulo = (Articulo) cmbArticulo.getSelectedItem();
        if (articulo != null) {
            try {
                // CAMBIO: Mejorar la lógica de búsqueda de precio
                Double precio = null;
                
                // 1. Intentar obtener precio del proveedor predeterminado
                if (articulo.getProveedorPredeterminado() != null && 
                    articulo.getListaProveedores() != null) {
                    
                    for (ArticuloProveedor ap : articulo.getListaProveedores()) {
                        if (ap.getProveedor().getCodProveedor().equals(
                                articulo.getProveedorPredeterminado().getCodProveedor()) && 
                            ap.getActivo()) {
                            precio = ap.getPrecioUnitario();
                            break;
                        }
                    }
                }
                
                // 2. Si no hay proveedor predeterminado, tomar el primer proveedor activo
                if (precio == null && articulo.getListaProveedores() != null) {
                    for (ArticuloProveedor ap : articulo.getListaProveedores()) {
                        if (ap.getActivo()) {
                            precio = ap.getPrecioUnitario();
                            break;
                        }
                    }
                }
                
                // 3. Si no hay proveedores, usar un precio base
                if (precio == null) {
                    precio = 100.0; // Precio por defecto
                }
                
                txtPrecio.setText(String.format("%.2f", precio));
                
            } catch (Exception e) {
                System.out.println("Error al actualizar precio: " + e.getMessage());
                txtPrecio.setText("100.00"); // Precio por defecto en caso de error
            }
        } else {
            txtPrecio.setText("0.00");
        }
    }
    
    private void agregarArticulo() {
        Articulo articulo = (Articulo) cmbArticulo.getSelectedItem();
        if (articulo == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un artículo");
            return;
        }
        
        int cantidad = (Integer) spnCantidad.getValue();
        
        // Validar stock
        if (cantidad > articulo.getStockActual()) {
            JOptionPane.showMessageDialog(this, 
                "Stock insuficiente. Stock actual: " + articulo.getStockActual());
            return;
        }
        
        // CAMBIO: Mejorar validación de precio
        double precio = 0;
        try {
            String precioTexto = txtPrecio.getText().trim();
            if (precioTexto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un precio");
                return;
            }
            precio = Double.parseDouble(precioTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio inválido. Debe ser un número válido.");
            return;
        }
        
        if (precio <= 0) {
            JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0");
            return;
        }
        
        // Verificar si el artículo ya está en el detalle
        for (VentaArticulo va : detalleVenta) {
            if (va.getArticulo().getCodArticulo().equals(articulo.getCodArticulo())) {
                JOptionPane.showMessageDialog(this, 
                    "El artículo ya está en la venta. Para modificar la cantidad, quítelo y agréguelo nuevamente.");
                return;
            }
        }
        
        // Crear detalle
        VentaArticulo detalle = new VentaArticulo();
        detalle.setArticulo(articulo);
        detalle.setCantidadVentaArticulo(cantidad);
        detalle.setPrecioVenta(precio);
        
        detalleVenta.add(detalle);
        actualizarTablaDetalle();
        
        // CAMBIO: Limpiar campos después de agregar
        spnCantidad.setValue(1);
        if (cmbArticulo.getItemCount() > 0) {
            cmbArticulo.setSelectedIndex(0);
            actualizarPrecio();
        }
    }
    
    private void quitarArticulo() {
        int filaSeleccionada = tablaDetalle.getSelectedRow();
        if (filaSeleccionada >= 0) {
            detalleVenta.remove(filaSeleccionada);
            actualizarTablaDetalle();
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un artículo de la lista");
        }
    }
    
    private void actualizarTablaDetalle() {
        modeloTablaDetalle.setRowCount(0);
        double total = 0;
        
        for (VentaArticulo detalle : detalleVenta) {
            double subtotal = detalle.getCantidadVentaArticulo() * detalle.getPrecioVenta();
            Object[] fila = {
                detalle.getArticulo().getDescripcionArticulo(),
                detalle.getCantidadVentaArticulo(),
                String.format("$ %.2f", detalle.getPrecioVenta()),
                String.format("$ %.2f", subtotal)
            };
            modeloTablaDetalle.addRow(fila);
            total += subtotal;
        }
        
        lblTotal.setText(String.format("$ %.2f", total));
    }
    
    private void registrarVenta() {
        if (detalleVenta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe agregar al menos un artículo");
            return;
        }
        
        int respuesta = JOptionPane.showConfirmDialog(this, 
            "¿Confirma el registro de la venta?", 
            "Confirmar Venta", 
            JOptionPane.YES_NO_OPTION);
            
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                Venta venta = new Venta();
                venta.setDetalleArticulos(new ArrayList<>(detalleVenta));
                
                venta = ventaService.crearVenta(venta);
                
                JOptionPane.showMessageDialog(this, 
                    "Venta registrada exitosamente\n" +
                    "ID: " + venta.getCodVenta() + "\n" +
                    "Total: $ " + String.format("%.2f", venta.getTotal()));
                
                limpiarVenta();
                actualizarTablaVentas();
                cargarArticulos(); // Recargar artículos por si cambió el stock
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al registrar venta: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limpiarVenta() {
        detalleVenta.clear();
        actualizarTablaDetalle();
        spnCantidad.setValue(1);
        txtPrecio.setText("");
        if (cmbArticulo.getItemCount() > 0) {
            cmbArticulo.setSelectedIndex(0);
            actualizarPrecio();
        }
    }
    
    private void actualizarTablaVentas() {
        try {
            modeloTablaVentas.setRowCount(0);
            List<Venta> ventas = ventaService.obtenerTodas();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (Venta v : ventas) {
                int cantidadItems = v.getDetalleArticulos() != null ? 
                    v.getDetalleArticulos().stream()
                        .mapToInt(VentaArticulo::getCantidadVentaArticulo)
                        .sum() : 0;
                
                Object[] fila = {
                    v.getCodVenta(),
                    v.getFechaHoraVenta().format(formatter),
                    String.format("$ %.2f", v.getTotal()),
                    cantidadItems
                };
                modeloTablaVentas.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar ventas: " + e.getMessage());
        }
    }
    
    private void verDetalleVenta() {
        int filaSeleccionada = tablaVentas.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una venta");
            return;
        }
        
        Integer idVenta = (Integer) modeloTablaVentas.getValueAt(filaSeleccionada, 0);
        Venta venta = ventaService.obtenerPorId(idVenta);
        
        if (venta != null) {
            DetalleVentaDialog dialog = new DetalleVentaDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                venta
            );
            dialog.setVisible(true);
        }
    }
}