package UI;

import Service.OrdenCompraService;
import Service.ArticuloService;
import Service.ProveedorService;
import Entities.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrdenesCompraFrame extends JInternalFrame {
    
    private OrdenCompraService ordenCompraService;
    private ArticuloService articuloService;
    private ProveedorService proveedorService;
    
    private JTable tablaOrdenes;
    private DefaultTableModel modeloTabla;
    
    // Campos del formulario
    private JComboBox<Articulo> cmbArticulo;
    private JComboBox<Proveedor> cmbProveedor;
    private JSpinner spnCantidad;
    private JTextField txtEstado;
    private JTextField txtLoteOptimo;
    
    private OrdenCompra ordenSeleccionada;
    
    public OrdenesCompraFrame() {
        super("Gestión de Órdenes de Compra", true, true, true, true);
        ordenCompraService = new OrdenCompraService();
        articuloService = new ArticuloService();
        proveedorService = new ProveedorService();
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        setSize(900, 600);
        setLayout(new BorderLayout());
        
        // Panel superior - Formulario
        JPanel panelFormulario = createFormPanel();
        add(panelFormulario, BorderLayout.NORTH);
        
        // Panel central - Tabla
        JPanel panelTabla = createTablePanel();
        add(panelTabla, BorderLayout.CENTER);
        
        // Panel inferior - Botones
        JPanel panelBotones = createButtonPanel();
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos de la Orden de Compra"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fila 1
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Artículo:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        cmbArticulo = new JComboBox<>();
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
        cmbArticulo.addActionListener(e -> actualizarDatosArticulo());
        panel.add(cmbArticulo, gbc);
        
        gbc.gridx = 3; gbc.gridwidth = 1;
        panel.add(new JLabel("Lote Óptimo:"), gbc);
        
        gbc.gridx = 4;
        txtLoteOptimo = new JTextField(10);
        txtLoteOptimo.setEditable(false);
        txtLoteOptimo.setBackground(Color.LIGHT_GRAY);
        panel.add(txtLoteOptimo, gbc);
        
        // Fila 2
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Proveedor:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        cmbProveedor = new JComboBox<>();
        cmbProveedor.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Proveedor) {
                    setText(((Proveedor) value).getNombreProveedor());
                }
                return this;
            }
        });
        panel.add(cmbProveedor, gbc);
        
        gbc.gridx = 3; gbc.gridwidth = 1;
        panel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 4;
        txtEstado = new JTextField(10);
        txtEstado.setEditable(false);
        txtEstado.setBackground(Color.LIGHT_GRAY);
        panel.add(txtEstado, gbc);
        
        // Fila 3
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Cantidad:"), gbc);
        
        gbc.gridx = 1;
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 99999, 1));
        panel.add(spnCantidad, gbc);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Órdenes de Compra"));
        
        String[] columnas = {"ID", "Artículo", "Proveedor", "Cantidad", "Estado", "Fecha Creación"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaOrdenes = new JTable(modeloTabla);
        tablaOrdenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaOrdenes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarOrdenSeleccionada();
            }
        });
        
        // Colorear filas según estado
        tablaOrdenes.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String estado = (String) table.getValueAt(row, 4);
                    if ("PENDIENTE".equals(estado)) {
                        c.setBackground(new Color(255, 255, 200));
                    } else if ("ENVIADA".equals(estado)) {
                        c.setBackground(new Color(200, 230, 255));
                    } else if ("FINALIZADA".equals(estado)) {
                        c.setBackground(new Color(200, 255, 200));
                    } else if ("CANCELADA".equals(estado)) {
                        c.setBackground(new Color(255, 200, 200));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaOrdenes);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnNuevo = new JButton("Nueva Orden");
        btnNuevo.addActionListener(e -> nuevaOrden());
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarOrden());
        
        JButton btnEnviar = new JButton("Enviar");
        btnEnviar.addActionListener(e -> enviarOrden());
        
        JButton btnFinalizar = new JButton("Finalizar");
        btnFinalizar.addActionListener(e -> finalizarOrden());
        
        JButton btnCancelar = new JButton("Cancelar Orden");
        btnCancelar.addActionListener(e -> cancelarOrden());
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizarTabla());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnEnviar);
        panel.add(btnFinalizar);
        panel.add(btnCancelar);
        panel.add(btnActualizar);
        panel.add(btnCerrar);
        
        return panel;
    }
    
    private void cargarDatos() {
        cargarArticulos();
        cargarProveedores();
        actualizarTabla();
    }
    
    private void cargarArticulos() {
        try {
            cmbArticulo.removeAllItems();
            List<Articulo> articulos = articuloService.obtenerTodos();
            for (Articulo a : articulos) {
                cmbArticulo.addItem(a);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar artículos: " + e.getMessage());
        }
    }
    
    private void cargarProveedores() {
        try {
            cmbProveedor.removeAllItems();
            List<Proveedor> proveedores = proveedorService.obtenerTodos();
            for (Proveedor p : proveedores) {
                cmbProveedor.addItem(p);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proveedores: " + e.getMessage());
        }
    }
    
    private void actualizarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<OrdenCompra> ordenes = ordenCompraService.obtenerTodas();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (OrdenCompra oc : ordenes) {
                String estado = oc.getEstadoActual() != null ? 
                    oc.getEstadoActual().getEstado().getNombreEstadoOrdenCompra() : "N/A";
                
                Object[] fila = {
                    oc.getCodOC(),
                    oc.getArticulo().getDescripcionArticulo(),
                    oc.getProveedor().getNombreProveedor(),
                    oc.getCantidad(),
                    estado,
                    oc.getFechaCreacion().format(formatter)
                };
                modeloTabla.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar órdenes: " + e.getMessage());
        }
    }
    
    private void actualizarDatosArticulo() {
        Articulo articulo = (Articulo) cmbArticulo.getSelectedItem();
        if (articulo == null) {
            // Si no hay artículo, limpiar todo
            txtLoteOptimo.setText("");
            spnCantidad.setValue(1);
            cargarProveedores(); // Cargar todos los proveedores
            return;
        }

        // Mostrar lote óptimo
        if (articulo.getLoteOptimo() != null) {
            txtLoteOptimo.setText(String.format("%.0f", articulo.getLoteOptimo()));
            spnCantidad.setValue(articulo.getLoteOptimo().intValue());
        } else {
            txtLoteOptimo.setText("0");
            spnCantidad.setValue(1);
        }

        // CRÍTICO: Filtrar proveedores según el artículo seleccionado
        cargarProveedoresPorArticulo(articulo);

        // Solo auto-seleccionar proveedor predeterminado si no estamos cargando una orden existente
        if (ordenSeleccionada == null && articulo.getProveedorPredeterminado() != null) {
            // Buscar el proveedor predeterminado en el combo filtrado
            for (int i = 0; i < cmbProveedor.getItemCount(); i++) {
                Proveedor proveedorCombo = cmbProveedor.getItemAt(i);
                if (proveedorCombo != null && 
                    proveedorCombo.getCodProveedor().equals(articulo.getProveedorPredeterminado().getCodProveedor())) {
                    cmbProveedor.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    // Nuevo método para cargar proveedores filtrados por artículo
    private void cargarProveedoresPorArticulo(Articulo articulo) {
        try {
            // Guardar la selección actual antes de limpiar
            Proveedor proveedorActualmenteSeleccionado = (Proveedor) cmbProveedor.getSelectedItem();

            cmbProveedor.removeAllItems();

            if (articulo.getListaProveedores() != null && !articulo.getListaProveedores().isEmpty()) {
                boolean proveedorAnteriorEncontrado = false;

                for (ArticuloProveedor ap : articulo.getListaProveedores()) {
                    if (ap.getActivo() && ap.getProveedor() != null) {
                        cmbProveedor.addItem(ap.getProveedor());

                        // Verificar si el proveedor anteriormente seleccionado está en la nueva lista
                        if (proveedorActualmenteSeleccionado != null && 
                            ap.getProveedor().getCodProveedor().equals(proveedorActualmenteSeleccionado.getCodProveedor())) {
                            proveedorAnteriorEncontrado = true;
                        }
                    }
                }

                // Si el proveedor anterior sigue siendo válido, reseleccionarlo
                if (proveedorAnteriorEncontrado && proveedorActualmenteSeleccionado != null) {
                    cmbProveedor.setSelectedItem(proveedorActualmenteSeleccionado);
                }
            }

            // Si no hay proveedores asociados, mostrar mensaje
            if (cmbProveedor.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "El artículo seleccionado no tiene proveedores asociados.\n" +
                    "Debe asociar proveedores en el maestro de artículos.",
                    "Sin Proveedores",
                    JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proveedores: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void cargarOrdenSeleccionada() {
        int filaSeleccionada = tablaOrdenes.getSelectedRow();
        if (filaSeleccionada >= 0) {
            Integer id = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
            ordenSeleccionada = ordenCompraService.obtenerPorId(id);

            if (ordenSeleccionada != null) {
                // PASO 1: Cargar artículo PRIMERO
                cmbArticulo.setSelectedItem(ordenSeleccionada.getArticulo());

                // PASO 2: Esto automáticamente filtra los proveedores a través del listener
                // de actualizarDatosArticulo(), así que esperamos un poco para que se complete
                SwingUtilities.invokeLater(() -> {
                    // PASO 3: Ahora seleccionar el proveedor específico de la orden
                    cmbProveedor.setSelectedItem(ordenSeleccionada.getProveedor());

                    // PASO 4: Cargar otros datos
                    spnCantidad.setValue(ordenSeleccionada.getCantidad());

                    String estado = ordenSeleccionada.getEstadoActual() != null ? 
                        ordenSeleccionada.getEstadoActual().getEstado().getNombreEstadoOrdenCompra() : "N/A";
                    txtEstado.setText(estado);

                    // PASO 5: Deshabilitar edición si no está en estado PENDIENTE
                    boolean editable = estado.equals("PENDIENTE");
                    cmbArticulo.setEnabled(editable);
                    cmbProveedor.setEnabled(editable);
                    spnCantidad.setEnabled(editable);
                });
            }
        }
    }
    
    private void nuevaOrden() {
        // IMPORTANTE: Limpiar la referencia a orden seleccionada PRIMERO
        ordenSeleccionada = null;

        // Limpiar estado visual
        txtEstado.setText("NUEVA");
        txtLoteOptimo.setText("");

        // Habilitar controles para edición
        cmbArticulo.setEnabled(true);
        cmbProveedor.setEnabled(true);
        spnCantidad.setEnabled(true);

        // Limpiar selección de tabla
        tablaOrdenes.clearSelection();

        // Resetear valores
        spnCantidad.setValue(1);

        // Cargar datos iniciales
        if (cmbArticulo.getItemCount() > 0) {
            cmbArticulo.setSelectedIndex(0);
            // actualizarDatosArticulo() se ejecutará automáticamente por el listener
        } else {
            // Si no hay artículos, cargar todos los proveedores
            cargarProveedores();
        }
    }
    
    private void guardarOrden() {
        try {
            Articulo articulo = (Articulo) cmbArticulo.getSelectedItem();
            Proveedor proveedor = (Proveedor) cmbProveedor.getSelectedItem();
            
            if (articulo == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un artículo");
                return;
            }
            
            if (proveedor == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un proveedor");
                return;
            }
            
            if (ordenSeleccionada == null) {
                // Nueva orden
                OrdenCompra orden = new OrdenCompra();
                orden.setArticulo(articulo);
                orden.setProveedor(proveedor);
                orden.setCantidad((Integer) spnCantidad.getValue());
                
                ordenCompraService.crearOrdenCompra(orden);
                JOptionPane.showMessageDialog(this, "Orden de compra creada exitosamente");
            } else {
                // Actualizar orden existente
                ordenSeleccionada.setArticulo(articulo);
                ordenSeleccionada.setProveedor(proveedor);
                ordenSeleccionada.setCantidad((Integer) spnCantidad.getValue());
                
                ordenCompraService.actualizarOrdenCompra(ordenSeleccionada);
                JOptionPane.showMessageDialog(this, "Orden de compra actualizada exitosamente");
            }
            
            actualizarTabla();
            nuevaOrden();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
        }
    }
    
    private void enviarOrden() {
        if (ordenSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una orden");
            return;
        }
        
        int respuesta = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de enviar la orden de compra?", 
            "Confirmar envío", 
            JOptionPane.YES_NO_OPTION);
            
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                ordenCompraService.enviarOrdenCompra(ordenSeleccionada.getCodOC());
                JOptionPane.showMessageDialog(this, "Orden enviada exitosamente");
                actualizarTabla();
                nuevaOrden();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al enviar: " + e.getMessage());
            }
        }
    }
    
    private void finalizarOrden() {
        if (ordenSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una orden");
            return;
        }
        
        int respuesta = JOptionPane.showConfirmDialog(this, 
            "¿Confirma la recepción de la mercadería?\n" +
            "Esto actualizará el inventario del artículo.", 
            "Confirmar finalización", 
            JOptionPane.YES_NO_OPTION);
            
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                ordenCompraService.finalizarOrdenCompra(ordenSeleccionada.getCodOC());
                JOptionPane.showMessageDialog(this, "Orden finalizada exitosamente\n" +
                    "El inventario ha sido actualizado");
                actualizarTabla();
                nuevaOrden();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Advertencia", 
                    JOptionPane.WARNING_MESSAGE);
                actualizarTabla();
                nuevaOrden();
            }
        }
    }
    
    private void cancelarOrden() {
        if (ordenSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una orden");
            return;
        }
        
        int respuesta = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de cancelar la orden de compra?", 
            "Confirmar cancelación", 
            JOptionPane.YES_NO_OPTION);
            
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                ordenCompraService.cancelarOrdenCompra(ordenSeleccionada.getCodOC());
                JOptionPane.showMessageDialog(this, "Orden cancelada exitosamente");
                actualizarTabla();
                nuevaOrden();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cancelar: " + e.getMessage());
            }
        }
    }
}