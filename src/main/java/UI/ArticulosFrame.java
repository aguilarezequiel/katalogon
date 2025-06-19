package UI;

import Service.ArticuloService;
import Service.ProveedorService;
import Entities.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ArticulosFrame extends JInternalFrame {
    
    private ArticuloService articuloService;
    private ProveedorService proveedorService;
    
    private JTable tablaArticulos;
    private DefaultTableModel modeloTabla;
    
    // Campos del formulario
    private JTextField txtDescripcion;
    private JSpinner spnStockActual;
    private JSpinner spnStockSeguridad;
    private JSpinner spnDemanda;
    private JSpinner spnCostoAlmacenamiento;
    private JSpinner spnTiempoIntervalo;
    private JComboBox<Proveedor> cmbProveedorPredeterminado;
    private JComboBox<String> cmbModeloInventario;
    
    // Campos calculados (solo lectura)
    private JTextField txtLoteOptimo;
    private JTextField txtPuntoPedido;
    private JTextField txtCGI;
    
    // Panel dinámico para campos específicos del modelo
    private JPanel panelModeloEspecifico;
    private CardLayout cardLayout;
    
    private Articulo articuloSeleccionado;
    
    public ArticulosFrame() {
        super("Gestión de Artículos", true, true, true, true);
        articuloService = new ArticuloService();
        proveedorService = new ProveedorService();
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        setSize(1000, 700);
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
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Artículo"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fila 1 - Descripción
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Descripción:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtDescripcion = new JTextField(30);
        panel.add(txtDescripcion, gbc);
        
        // Fila 2 - Stock y Modelo
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Stock Actual:"), gbc);
        
        gbc.gridx = 1;
        spnStockActual = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        panel.add(spnStockActual, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Modelo Inventario:"), gbc);
        
        gbc.gridx = 3;
        cmbModeloInventario = new JComboBox<>(new String[]{"LOTE_FIJO", "INTERVALO_FIJO"});
        cmbModeloInventario.addActionListener(e -> cambiarModeloInventario());
        panel.add(cmbModeloInventario, gbc);
        
        // Fila 3 - Stock Seguridad y Demanda
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Stock Seguridad:"), gbc);
        
        gbc.gridx = 1;
        spnStockSeguridad = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999999.0, 1.0));
        panel.add(spnStockSeguridad, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Demanda (anual):"), gbc);
        
        gbc.gridx = 3;
        spnDemanda = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999999.0, 1.0));
        panel.add(spnDemanda, gbc);
        
        // Fila 4 - Costo Almacenamiento y Proveedor
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Costo Almacenamiento:"), gbc);
        
        gbc.gridx = 1;
        spnCostoAlmacenamiento = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999999.0, 0.01));
        panel.add(spnCostoAlmacenamiento, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Proveedor Predeterminado:"), gbc);
        
        gbc.gridx = 3;
        cmbProveedorPredeterminado = new JComboBox<>();
        panel.add(cmbProveedorPredeterminado, gbc);
        
        // Fila 5 - Panel específico del modelo
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        panelModeloEspecifico = createPanelModeloEspecifico();
        panel.add(panelModeloEspecifico, gbc);
        
        // Fila 6 - Valores calculados
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        panel.add(new JLabel("CGI:"), gbc);
        
        gbc.gridx = 1;
        txtCGI = new JTextField();
        txtCGI.setEditable(false);
        txtCGI.setBackground(Color.LIGHT_GRAY);
        panel.add(txtCGI, gbc);
        
        return panel;
    }
    
    private JPanel createPanelModeloEspecifico() {
        cardLayout = new CardLayout();
        JPanel panel = new JPanel(cardLayout);
        panel.setBorder(BorderFactory.createTitledBorder("Parámetros del Modelo"));
        
        // Panel para modelo Lote Fijo
        JPanel panelLoteFijo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        panelLoteFijo.add(new JLabel("Lote Óptimo:"));
        txtLoteOptimo = new JTextField(10);
        txtLoteOptimo.setEditable(false);
        txtLoteOptimo.setBackground(Color.LIGHT_GRAY);
        panelLoteFijo.add(txtLoteOptimo);
        
        panelLoteFijo.add(new JLabel("Punto Pedido:"));
        txtPuntoPedido = new JTextField(10);
        txtPuntoPedido.setEditable(false);
        txtPuntoPedido.setBackground(Color.LIGHT_GRAY);
        panelLoteFijo.add(txtPuntoPedido);
        
        // Panel para modelo Tiempo Fijo
        JPanel panelTiempoFijo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        panelTiempoFijo.add(new JLabel("Intervalo de Tiempo (días):"));
        spnTiempoIntervalo = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        panelTiempoFijo.add(spnTiempoIntervalo);
        
        panel.add(panelLoteFijo, "LOTE_FIJO");
        panel.add(panelTiempoFijo, "INTERVALO_FIJO");
        
        return panel;
    }
    
    private void cambiarModeloInventario() {
        String modelo = (String) cmbModeloInventario.getSelectedItem();
        cardLayout.show(panelModeloEspecifico, modelo);
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Artículos"));
        
        String[] columnas = {"ID", "Descripción", "Stock Actual", "Stock Seguridad", 
                            "Demanda", "Modelo", "Proveedor Pred.", "CGI"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaArticulos = new JTable(modeloTabla);
        tablaArticulos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaArticulos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarArticuloSeleccionado();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaArticulos);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnNuevo = new JButton("Nuevo");
        btnNuevo.addActionListener(e -> limpiarFormulario());
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarArticulo());
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarArticulo());
        
        JButton btnCalcular = new JButton("Recalcular");
        btnCalcular.addActionListener(e -> recalcularValores());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnEliminar);
        panel.add(btnCalcular);
        panel.add(btnCerrar);
        
        return panel;
    }
    
    private void cargarDatos() {
        // Cargar proveedores en combo
        try {
            List<Proveedor> proveedores = proveedorService.obtenerTodos();
            cmbProveedorPredeterminado.removeAllItems();
            cmbProveedorPredeterminado.addItem(null); // Opción vacía
            for (Proveedor p : proveedores) {
                cmbProveedorPredeterminado.addItem(p);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proveedores: " + e.getMessage());
        }
        
        // Cargar artículos en tabla
        actualizarTabla();
        cambiarModeloInventario(); // Mostrar panel correcto
    }
    
    private void actualizarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Articulo> articulos = articuloService.obtenerTodos();
            
            for (Articulo a : articulos) {
                String proveedorPred = a.getProveedorPredeterminado() != null ? 
                    a.getProveedorPredeterminado().getNombreProveedor() : "Sin asignar";
                
                Object[] fila = {
                    a.getCodArticulo(),
                    a.getDescripcionArticulo(),
                    a.getStockActual(),
                    a.getStockSeguridad(),
                    a.getDemanda(),
                    a.getModeloInventario().getNombreMetodo(),
                    proveedorPred,
                    a.getCgi() != null ? String.format("%.2f", a.getCgi()) : "0.00"
                };
                modeloTabla.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar artículos: " + e.getMessage());
        }
    }
    
    private void cargarArticuloSeleccionado() {
        int filaSeleccionada = tablaArticulos.getSelectedRow();
        if (filaSeleccionada >= 0) {
            Integer id = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
            
            // CAMBIO IMPORTANTE: Recargar completamente el artículo
            articuloSeleccionado = articuloService.obtenerPorId(id);
            
            if (articuloSeleccionado != null) {
                txtDescripcion.setText(articuloSeleccionado.getDescripcionArticulo());
                spnStockActual.setValue(articuloSeleccionado.getStockActual());
                spnStockSeguridad.setValue(articuloSeleccionado.getStockSeguridad());
                spnDemanda.setValue(articuloSeleccionado.getDemanda());
                spnCostoAlmacenamiento.setValue(articuloSeleccionado.getCostoAlmacenamiento());
                cmbModeloInventario.setSelectedItem(articuloSeleccionado.getModeloInventario().getNombreMetodo());
                
                // CAMBIO IMPORTANTE: Buscar y seleccionar el proveedor predeterminado en el combo
                if (articuloSeleccionado.getProveedorPredeterminado() != null) {
                    // Buscar el proveedor en el combo por ID
                    for (int i = 0; i < cmbProveedorPredeterminado.getItemCount(); i++) {
                        Proveedor item = cmbProveedorPredeterminado.getItemAt(i);
                        if (item != null && 
                            item.getCodProveedor().equals(articuloSeleccionado.getProveedorPredeterminado().getCodProveedor())) {
                            cmbProveedorPredeterminado.setSelectedIndex(i);
                            break;
                        }
                    }
                } else {
                    cmbProveedorPredeterminado.setSelectedIndex(0); // Seleccionar la opción vacía
                }
                
                // Cargar campos específicos del modelo
                String modelo = articuloSeleccionado.getModeloInventario().getNombreMetodo();
                if ("LOTE_FIJO".equals(modelo)) {
                    txtLoteOptimo.setText(articuloSeleccionado.getLoteOptimo() != null ? 
                        String.format("%.2f", articuloSeleccionado.getLoteOptimo()) : "0.00");
                    txtPuntoPedido.setText(articuloSeleccionado.getPuntoPedido() != null ? 
                        String.format("%.2f", articuloSeleccionado.getPuntoPedido()) : "0.00");
                } else if ("INTERVALO_FIJO".equals(modelo)) {
                    spnTiempoIntervalo.setValue(articuloSeleccionado.getTiempoIntervalo() != null ? 
                        articuloSeleccionado.getTiempoIntervalo() : 30);
                }
                
                txtCGI.setText(articuloSeleccionado.getCgi() != null ? 
                    String.format("%.2f", articuloSeleccionado.getCgi()) : "0.00");
                
                cambiarModeloInventario();
            }
        }
    }
    
    private void limpiarFormulario() {
        articuloSeleccionado = null;
        txtDescripcion.setText("");
        spnStockActual.setValue(0);
        spnStockSeguridad.setValue(0.0);
        spnDemanda.setValue(0.0);
        spnCostoAlmacenamiento.setValue(0.0);
        spnTiempoIntervalo.setValue(30);
        cmbModeloInventario.setSelectedIndex(0);
        cmbProveedorPredeterminado.setSelectedIndex(0);
        txtLoteOptimo.setText("");
        txtPuntoPedido.setText("");
        txtCGI.setText("");
        tablaArticulos.clearSelection();
        cambiarModeloInventario();
    }
    
    private void guardarArticulo() {
        try {
            if (txtDescripcion.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar una descripción");
                return;
            }
            
            Articulo articulo = articuloSeleccionado != null ? articuloSeleccionado : new Articulo();
            
            articulo.setDescripcionArticulo(txtDescripcion.getText().trim());
            articulo.setStockActual((Integer) spnStockActual.getValue());
            articulo.setStockSeguridad((Double) spnStockSeguridad.getValue());
            articulo.setDemanda((Double) spnDemanda.getValue());
            articulo.setCostoAlmacenamiento((Double) spnCostoAlmacenamiento.getValue());
            
            // Obtener proveedor seleccionado
            Proveedor proveedorSeleccionado = (Proveedor) cmbProveedorPredeterminado.getSelectedItem();
            
            // Crear modelo de inventario
            ModeloInventario modelo = new ModeloInventario();
            modelo.setNombreMetodo((String) cmbModeloInventario.getSelectedItem());
            articulo.setModeloInventario(modelo);
            
            // Asignar campos específicos del modelo
            if ("INTERVALO_FIJO".equals(modelo.getNombreMetodo())) {
                articulo.setTiempoIntervalo((Integer) spnTiempoIntervalo.getValue());
            }
            
            if (articuloSeleccionado == null) {
                // Artículo nuevo - NO asignar proveedor predeterminado aún
                articulo.setActivo(true);
                articulo.setLoteOptimo(0.0);
                articulo.setPuntoPedido(0.0);
                articulo.setCgi(0.0);
                
                // Guardar primero el artículo sin proveedor predeterminado
                Articulo articuloGuardado = articuloService.crearArticulo(articulo);
                
                // DESPUÉS manejar el proveedor predeterminado si fue seleccionado
                if (proveedorSeleccionado != null) {
                    manejarProveedorDespuesDelGuardado(articuloGuardado, proveedorSeleccionado);
                }
                
                JOptionPane.showMessageDialog(this, "Artículo creado exitosamente");
            } else {
                // Artículo existente - manejar normalmente
                if (proveedorSeleccionado != null) {
                    if (manejarProveedorPredeterminado(articulo, proveedorSeleccionado)) {
                        articulo.setProveedorPredeterminado(proveedorSeleccionado);
                    } else {
                        // CAMBIO: Si no se puede asociar, mantener el proveedor actual o null
                        articulo.setProveedorPredeterminado(null);
                        // IMPORTANTE: Recargar combo para mostrar el estado actual
                        cargarDatos();
                        return; // Salir sin actualizar para que el usuario vea el estado actual
                    }
                } else {
                    articulo.setProveedorPredeterminado(null);
                }
                
                articuloService.actualizarArticulo(articulo);
                JOptionPane.showMessageDialog(this, "Artículo actualizado exitosamente");
            }
            
            limpiarFormulario();
            actualizarTabla();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
            // Recargar datos para mostrar el estado actual
            cargarDatos();
        }
    }
    
    // Nuevo método para manejar proveedor después del guardado
    private void manejarProveedorDespuesDelGuardado(Articulo articuloGuardado, Proveedor proveedor) {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Desea asociar el proveedor " + proveedor.getNombreProveedor() + 
            " a este artículo y configurarlo como predeterminado?",
            "Asociar Proveedor",
            JOptionPane.YES_NO_OPTION);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            ArticuloProveedorDialog dialog = new ArticuloProveedorDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), articuloGuardado);
            dialog.setVisible(true);
            
            if (dialog.isAceptado()) {
                try {
                    ArticuloProveedor ap = dialog.getArticuloProveedor();
                    
                    // Crear la asociación
                    articuloService.crearAsociacionArticuloProveedor(
                        articuloGuardado, proveedor, 
                        ap.getPrecioUnitario(), 
                        ap.getDemoraEntrega(), 
                        ap.getCostoPedido()
                    );
                    
                    // Asignar como proveedor predeterminado
                    articuloGuardado.setProveedorPredeterminado(proveedor);
                    articuloService.actualizarArticulo(articuloGuardado);
                    
                    JOptionPane.showMessageDialog(this, "Proveedor asociado y configurado como predeterminado");
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error al asociar proveedor: " + e.getMessage());
                }
            }
        }
    }
    
    private boolean manejarProveedorPredeterminado(Articulo articulo, Proveedor proveedor) {
        // Solo para artículos ya guardados
        if (articulo.getCodArticulo() == null) {
            return false;
        }
        
        try {
            // CAMBIO IMPORTANTE: Recargar el artículo con las asociaciones desde la base de datos
            Articulo articuloActualizado = articuloService.obtenerPorId(articulo.getCodArticulo());
            
            // Verificar si ya existe la asociación usando el artículo actualizado
            if (articuloActualizado.getListaProveedores() != null) {
                boolean asociacionExiste = articuloActualizado.getListaProveedores().stream()
                    .anyMatch(ap -> ap.getProveedor().getCodProveedor().equals(proveedor.getCodProveedor()) && ap.getActivo());
                
                if (asociacionExiste) {
                    return true;
                }
            }
            
            // Si no existe la asociación, preguntar si crear
            int respuesta = JOptionPane.showConfirmDialog(this,
                "El proveedor seleccionado no está asociado a este artículo.\n" +
                "¿Desea crear la asociación artículo-proveedor ahora?",
                "Crear Asociación",
                JOptionPane.YES_NO_OPTION);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                ArticuloProveedorDialog dialog = new ArticuloProveedorDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), articulo);
                dialog.setVisible(true);
                
                if (dialog.isAceptado()) {
                    ArticuloProveedor ap = dialog.getArticuloProveedor();
                    
                    articuloService.crearAsociacionArticuloProveedor(
                        articulo, proveedor, 
                        ap.getPrecioUnitario(), 
                        ap.getDemoraEntrega(), 
                        ap.getCostoPedido()
                    );
                    
                    JOptionPane.showMessageDialog(this, "Asociación creada exitosamente");
                    return true;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al verificar asociaciones: " + e.getMessage());
            return false;
        }
    }
    
    private void eliminarArticulo() {
        if (articuloSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un artículo");
            return;
        }
        
        int respuesta = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar el artículo?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);
            
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                articuloService.eliminarArticulo(articuloSeleccionado.getCodArticulo());
                JOptionPane.showMessageDialog(this, "Artículo eliminado exitosamente");
                limpiarFormulario();
                actualizarTabla();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
            }
        }
    }
    
    private void recalcularValores() {
        try {
            if (articuloSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un artículo");
                return;
            }
            
            // Actualizar valores del artículo desde el formulario
            articuloSeleccionado.setDemanda((Double) spnDemanda.getValue());
            articuloSeleccionado.setCostoAlmacenamiento((Double) spnCostoAlmacenamiento.getValue());
            articuloSeleccionado.setStockSeguridad((Double) spnStockSeguridad.getValue());
            articuloSeleccionado.setProveedorPredeterminado((Proveedor) cmbProveedorPredeterminado.getSelectedItem());
            
            String modelo = (String) cmbModeloInventario.getSelectedItem();
            if ("INTERVALO_FIJO".equals(modelo)) {
                articuloSeleccionado.setTiempoIntervalo((Integer) spnTiempoIntervalo.getValue());
            }
            
            // Llamar al servicio para recalcular
            articuloService.recalcularArticulo(articuloSeleccionado.getCodArticulo());
            
            // Recargar el artículo actualizado
            articuloSeleccionado = articuloService.obtenerPorId(articuloSeleccionado.getCodArticulo());
            
            // Actualizar campos calculados en la UI
            if ("LOTE_FIJO".equals(modelo)) {
                txtLoteOptimo.setText(articuloSeleccionado.getLoteOptimo() != null ? 
                    String.format("%.2f", articuloSeleccionado.getLoteOptimo()) : "0.00");
                txtPuntoPedido.setText(articuloSeleccionado.getPuntoPedido() != null ? 
                    String.format("%.2f", articuloSeleccionado.getPuntoPedido()) : "0.00");
            }
            
            txtCGI.setText(articuloSeleccionado.getCgi() != null ? 
                String.format("%.2f", articuloSeleccionado.getCgi()) : "0.00");
            
            // Actualizar tabla
            actualizarTabla();
            
            JOptionPane.showMessageDialog(this, "Valores recalculados exitosamente");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al recalcular: " + e.getMessage());
        }
    }
}