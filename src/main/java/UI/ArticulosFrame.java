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
    private JSpinner spnCostoPedido;
    private JSpinner spnCostoCompra;
    private JComboBox<Proveedor> cmbProveedorPredeterminado;
    private JComboBox<String> cmbModeloInventario;
    
    // Campos calculados (solo lectura)
    private JTextField txtLoteOptimo;
    private JTextField txtPuntoPedido;
    private JTextField txtInventarioMaximo;
    private JTextField txtCGI;
    
    private Articulo articuloSeleccionado;
    
    public ArticulosFrame() {
        super("Gestión de Artículos", true, true, true, true);
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
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Artículo"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Fila 1
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Descripción:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtDescripcion = new JTextField(30);
        panel.add(txtDescripcion, gbc);
        
        // Fila 2
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Stock Actual:"), gbc);
        
        gbc.gridx = 1;
        spnStockActual = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        panel.add(spnStockActual, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Stock Seguridad:"), gbc);
        
        gbc.gridx = 3;
        spnStockSeguridad = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999999.0, 1.0)); // Cambiar de Integer a Double
        panel.add(spnStockSeguridad, gbc);
        
        // Fila 3
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Demanda:"), gbc);
        
        gbc.gridx = 1;
        spnDemanda = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999999.0, 1.0));
        panel.add(spnDemanda, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Modelo Inventario:"), gbc);
        
        gbc.gridx = 3;
        cmbModeloInventario = new JComboBox<>(new String[]{"LOTE_FIJO", "INTERVALO_FIJO"});
        panel.add(cmbModeloInventario, gbc);
        
        // Fila 4 - Costos
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Costo Almacenamiento:"), gbc);
        
        gbc.gridx = 1;
        spnCostoAlmacenamiento = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999999.0, 0.01));
        panel.add(spnCostoAlmacenamiento, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Costo Pedido:"), gbc);
        
        gbc.gridx = 3;
        spnCostoPedido = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999999.0, 0.01));
        panel.add(spnCostoPedido, gbc);
        
        // Fila 5
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Costo Compra:"), gbc);
        
        gbc.gridx = 1;
        spnCostoCompra = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999999.0, 0.01));
        panel.add(spnCostoCompra, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Proveedor Predeterminado:"), gbc);
        
        gbc.gridx = 3;
        cmbProveedorPredeterminado = new JComboBox<>();
        panel.add(cmbProveedorPredeterminado, gbc);
        
        // Fila 6 - Valores calculados
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Lote Óptimo:"), gbc);
        
        gbc.gridx = 1;
        txtLoteOptimo = new JTextField();
        txtLoteOptimo.setEditable(false);
        txtLoteOptimo.setBackground(Color.LIGHT_GRAY);
        panel.add(txtLoteOptimo, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Punto Pedido:"), gbc);
        
        gbc.gridx = 3;
        txtPuntoPedido = new JTextField();
        txtPuntoPedido.setEditable(false);
        txtPuntoPedido.setBackground(Color.LIGHT_GRAY);
        panel.add(txtPuntoPedido, gbc);
        
        // Fila 7
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Inventario Máximo:"), gbc);
        
        gbc.gridx = 1;
        txtInventarioMaximo = new JTextField();
        txtInventarioMaximo.setEditable(false);
        txtInventarioMaximo.setBackground(Color.LIGHT_GRAY);
        panel.add(txtInventarioMaximo, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("CGI:"), gbc);
        
        gbc.gridx = 3;
        txtCGI = new JTextField();
        txtCGI.setEditable(false);
        txtCGI.setBackground(Color.LIGHT_GRAY);
        panel.add(txtCGI, gbc);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Artículos"));
        
        String[] columnas = {"ID", "Descripción", "Stock Actual", "Stock Seguridad", 
                            "Demanda", "Modelo", "Lote Óptimo", "Punto Pedido", "CGI"};
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
    }
    
    private void actualizarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Articulo> articulos = articuloService.obtenerTodos();
            
            for (Articulo a : articulos) {
                Object[] fila = {
                    a.getCodArticulo(),
                    a.getDescripcionArticulo(),
                    a.getStockActual(),
                    a.getStockSeguridad(),
                    a.getDemanda(),
                    a.getModeloInventario().getNombreMetodo(),
                    String.format("%.2f", a.getLoteOptimo()),
                    String.format("%.2f", a.getPuntoPedido()),
                    String.format("%.2f", a.getCgi())
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
            articuloSeleccionado = articuloService.obtenerPorId(id);
            
            if (articuloSeleccionado != null) {
                txtDescripcion.setText(articuloSeleccionado.getDescripcionArticulo());
                spnStockActual.setValue(articuloSeleccionado.getStockActual());
                spnStockSeguridad.setValue(articuloSeleccionado.getStockSeguridad());
                spnDemanda.setValue(articuloSeleccionado.getDemanda());
                spnCostoAlmacenamiento.setValue(articuloSeleccionado.getCostoAlmacenamiento());
                spnCostoPedido.setValue(articuloSeleccionado.getCostoPedido());
                spnCostoCompra.setValue(articuloSeleccionado.getCostoCompra());
                cmbModeloInventario.setSelectedItem(articuloSeleccionado.getModeloInventario().getNombreMetodo());
                cmbProveedorPredeterminado.setSelectedItem(articuloSeleccionado.getProveedorPredeterminado());
                
                // Mostrar valores calculados
                txtLoteOptimo.setText(String.format("%.2f", articuloSeleccionado.getLoteOptimo()));
                txtPuntoPedido.setText(String.format("%.2f", articuloSeleccionado.getPuntoPedido()));
                txtInventarioMaximo.setText(String.format("%.2f", articuloSeleccionado.getInventarioMaximo()));
                txtCGI.setText(String.format("%.2f", articuloSeleccionado.getCgi()));
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
        spnCostoPedido.setValue(0.0);
        spnCostoCompra.setValue(0.0);
        cmbModeloInventario.setSelectedIndex(0);
        cmbProveedorPredeterminado.setSelectedIndex(0);
        txtLoteOptimo.setText("");
        txtPuntoPedido.setText("");
        txtInventarioMaximo.setText("");
        txtCGI.setText("");
        tablaArticulos.clearSelection();
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
            articulo.setCostoPedido((Double) spnCostoPedido.getValue());
            articulo.setCostoCompra((Double) spnCostoCompra.getValue());
            articulo.setCostoMantenimiento((Double) spnCostoAlmacenamiento.getValue());
            articulo.setProveedorPredeterminado((Proveedor) cmbProveedorPredeterminado.getSelectedItem());
            
            // Crear modelo de inventario
            ModeloInventario modelo = new ModeloInventario();
            modelo.setNombreMetodo((String) cmbModeloInventario.getSelectedItem());
            articulo.setModeloInventario(modelo);
            
            if (articuloSeleccionado == null) {
                articulo.setActivo(true);
                articulo.setLoteOptimo(0.0);
                articulo.setPuntoPedido(0.0);
                articulo.setInventarioMaximo(0.0);
                articulo.setCgi(0.0);
                articuloService.crearArticulo(articulo);
                JOptionPane.showMessageDialog(this, "Artículo creado exitosamente");
            } else {
                articuloService.actualizarArticulo(articulo);
                JOptionPane.showMessageDialog(this, "Artículo actualizado exitosamente");
            }
            
            limpiarFormulario();
            actualizarTabla();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
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
            
            // Actualizar valores del formulario
            articuloSeleccionado.setDemanda((Double) spnDemanda.getValue());
            articuloSeleccionado.setCostoAlmacenamiento((Double) spnCostoAlmacenamiento.getValue());
            articuloSeleccionado.setCostoPedido((Double) spnCostoPedido.getValue());
            articuloSeleccionado.setCostoCompra((Double) spnCostoCompra.getValue());
            
            // Recalcular
            if (cmbModeloInventario.getSelectedItem().equals("LOTE_FIJO")) {
                articuloSeleccionado.calcularLoteFijo();
            } else {
                articuloSeleccionado.calcularIntervaloFijo();
            }
            articuloSeleccionado.calcularCGI();
            
            // Mostrar valores calculados
            txtLoteOptimo.setText(String.format("%.2f", articuloSeleccionado.getLoteOptimo()));
            txtPuntoPedido.setText(String.format("%.2f", articuloSeleccionado.getPuntoPedido()));
            txtInventarioMaximo.setText(String.format("%.2f", articuloSeleccionado.getInventarioMaximo()));
            txtCGI.setText(String.format("%.2f", articuloSeleccionado.getCgi()));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al recalcular: " + e.getMessage());
        }
    }
}