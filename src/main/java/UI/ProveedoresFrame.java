package UI;

import Service.ProveedorService;
import Service.ArticuloService;
import Entities.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class ProveedoresFrame extends JInternalFrame {
    
    private ProveedorService proveedorService;
    private ArticuloService articuloService;
    
    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;
    
    // Campos del formulario
    private JTextField txtNombre;
    private JList<Articulo> listaArticulosDisponibles;
    private JList<ArticuloProveedor> listaArticulosAsociados;
    private DefaultListModel<Articulo> modeloArticulosDisponibles;
    private DefaultListModel<ArticuloProveedor> modeloArticulosAsociados;
    
    private Proveedor proveedorSeleccionado;
    
    public ProveedoresFrame() {
        super("Gestión de Proveedores", true, true, true, true);
        proveedorService = new ProveedorService();
        articuloService = new ArticuloService();
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        setSize(800, 600);
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Proveedor"));
        
        // Panel superior - Nombre
        JPanel panelNombre = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNombre.add(new JLabel("Nombre:"));
        txtNombre = new JTextField(30);
        panelNombre.add(txtNombre);
        panel.add(panelNombre, BorderLayout.NORTH);
        
        // Panel central - Artículos
        JPanel panelArticulos = new JPanel(new GridLayout(1, 3));
        
        // Lista de artículos disponibles
        JPanel panelDisponibles = new JPanel(new BorderLayout());
        panelDisponibles.setBorder(BorderFactory.createTitledBorder("Artículos Disponibles"));
        modeloArticulosDisponibles = new DefaultListModel<>();
        listaArticulosDisponibles = new JList<>(modeloArticulosDisponibles);
        panelDisponibles.add(new JScrollPane(listaArticulosDisponibles), BorderLayout.CENTER);
        
        // Botones centrales
        JPanel panelBotonesCentro = new JPanel(new GridBagLayout());
        JButton btnAgregar = new JButton(">");
        btnAgregar.addActionListener(e -> agregarArticulo());
        JButton btnQuitar = new JButton("<");
        btnQuitar.addActionListener(e -> quitarArticulo());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        panelBotonesCentro.add(btnAgregar, gbc);
        gbc.gridy = 1;
        panelBotonesCentro.add(btnQuitar, gbc);
        
        // Lista de artículos asociados
        JPanel panelAsociados = new JPanel(new BorderLayout());
        panelAsociados.setBorder(BorderFactory.createTitledBorder("Artículos del Proveedor"));
        modeloArticulosAsociados = new DefaultListModel<>();
        listaArticulosAsociados = new JList<>(modeloArticulosAsociados);
        listaArticulosAsociados.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof ArticuloProveedor) {
                    ArticuloProveedor ap = (ArticuloProveedor) value;
                    value = ap.getArticulo().getDescripcionArticulo() + 
                           " - Precio: $" + ap.getPrecioUnitario() +
                           " - Demora: " + ap.getDemoraEntrega() + " días";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        panelAsociados.add(new JScrollPane(listaArticulosAsociados), BorderLayout.CENTER);
        
        panelArticulos.add(panelDisponibles);
        panelArticulos.add(panelBotonesCentro);
        panelArticulos.add(panelAsociados);
        
        panel.add(panelArticulos, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Proveedores"));
        
        String[] columnas = {"ID", "Nombre", "Cantidad de Artículos"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaProveedores = new JTable(modeloTabla);
        tablaProveedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProveedores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarProveedorSeleccionado();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaProveedores);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnNuevo = new JButton("Nuevo");
        btnNuevo.addActionListener(e -> limpiarFormulario());
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> guardarProveedor());
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarProveedor());
        
        JButton btnEditarArticulo = new JButton("Editar Artículo");
        btnEditarArticulo.addActionListener(e -> editarArticuloProveedor());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panel.add(btnNuevo);
        panel.add(btnGuardar);
        panel.add(btnEliminar);
        panel.add(btnEditarArticulo);
        panel.add(btnCerrar);
        
        return panel;
    }
    
    private void cargarDatos() {
        actualizarTabla();
        cargarArticulosDisponibles();
    }
    
    private void actualizarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Proveedor> proveedores = proveedorService.obtenerTodos();
            
            for (Proveedor p : proveedores) {
                Object[] fila = {
                    p.getCodProveedor(),
                    p.getNombreProveedor(),
                    p.getArticulosProveedor() != null ? p.getArticulosProveedor().size() : 0
                };
                modeloTabla.addRow(fila);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proveedores: " + e.getMessage());
        }
    }
    
    private void cargarArticulosDisponibles() {
        try {
            modeloArticulosDisponibles.clear();
            List<Articulo> articulos = articuloService.obtenerTodos();
            for (Articulo a : articulos) {
                modeloArticulosDisponibles.addElement(a);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar artículos: " + e.getMessage());
        }
    }
    
    private void cargarProveedorSeleccionado() {
        int filaSeleccionada = tablaProveedores.getSelectedRow();
        if (filaSeleccionada >= 0) {
            Integer id = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
            proveedorSeleccionado = proveedorService.obtenerPorId(id);
            
            if (proveedorSeleccionado != null) {
                txtNombre.setText(proveedorSeleccionado.getNombreProveedor());
                
                // Cargar artículos asociados
                modeloArticulosAsociados.clear();
                if (proveedorSeleccionado.getArticulosProveedor() != null) {
                    for (ArticuloProveedor ap : proveedorSeleccionado.getArticulosProveedor()) {
                        if (ap.getActivo()) {
                            modeloArticulosAsociados.addElement(ap);
                        }
                    }
                }
            }
        }
    }
    
    private void limpiarFormulario() {
        proveedorSeleccionado = null;
        txtNombre.setText("");
        modeloArticulosAsociados.clear();
        tablaProveedores.clearSelection();
    }
    
    private void agregarArticulo() {
        Articulo articuloSeleccionado = listaArticulosDisponibles.getSelectedValue();
        if (articuloSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un artículo");
            return;
        }
        
        // Verificar si ya está asociado
        for (int i = 0; i < modeloArticulosAsociados.size(); i++) {
            ArticuloProveedor ap = modeloArticulosAsociados.get(i);
            if (ap.getArticulo().getCodArticulo().equals(articuloSeleccionado.getCodArticulo())) {
                JOptionPane.showMessageDialog(this, "El artículo ya está asociado");
                return;
            }
        }
        
        // Pedir datos adicionales
        ArticuloProveedorDialog dialog = new ArticuloProveedorDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            articuloSeleccionado
        );
        dialog.setVisible(true);
        
        if (dialog.isAceptado()) {
            ArticuloProveedor ap = dialog.getArticuloProveedor();
            modeloArticulosAsociados.addElement(ap);
        }
    }
    
    private void quitarArticulo() {
        ArticuloProveedor apSeleccionado = listaArticulosAsociados.getSelectedValue();
        if (apSeleccionado != null) {
            modeloArticulosAsociados.removeElement(apSeleccionado);
        }
    }
    
    private void editarArticuloProveedor() {
        ArticuloProveedor apSeleccionado = listaArticulosAsociados.getSelectedValue();
        if (apSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un artículo de la lista");
            return;
        }
        
        ArticuloProveedorDialog dialog = new ArticuloProveedorDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            apSeleccionado
        );
        dialog.setVisible(true);
        
        if (dialog.isAceptado()) {
            // Actualizar la lista
            listaArticulosAsociados.repaint();
        }
    }
    
    private void guardarProveedor() {
        try {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un nombre");
                return;
            }
            
            if (modeloArticulosAsociados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe asociar al menos un artículo");
                return;
            }
            
            Proveedor proveedor = proveedorSeleccionado != null ? proveedorSeleccionado : new Proveedor();
            proveedor.setNombreProveedor(txtNombre.getText().trim());
            
            // Convertir modelo a lista - TODAS las asociaciones del modelo
            List<ArticuloProveedor> articulos = new ArrayList<>();
            for (int i = 0; i < modeloArticulosAsociados.size(); i++) {
                ArticuloProveedor ap = modeloArticulosAsociados.get(i);
                
                // IMPORTANTE: Asegurar que el proveedor esté asignado
                ap.setProveedor(proveedor);
                
                // Si el ArticuloProveedor no tiene ID, es nuevo
                if (ap.getId() == null) {
                    ap.setActivo(true);
                }
                
                articulos.add(ap);
            }
            proveedor.setArticulosProveedor(articulos);
            
            if (proveedorSeleccionado == null) {
                // Nuevo proveedor
                proveedor.setActivo(true);
                proveedorService.crearProveedor(proveedor);
                JOptionPane.showMessageDialog(this, "Proveedor creado exitosamente");
            } else {
                // Actualizar proveedor existente
                proveedorService.actualizarProveedor(proveedor);
                JOptionPane.showMessageDialog(this, "Proveedor actualizado exitosamente");
            }
            
            limpiarFormulario();
            actualizarTabla();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
            e.printStackTrace(); // Para debug
        }
    }
    
    private void eliminarProveedor() {
        if (proveedorSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un proveedor");
            return;
        }
        
        int respuesta = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar el proveedor?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);
            
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                proveedorService.eliminarProveedor(proveedorSeleccionado.getCodProveedor());
                JOptionPane.showMessageDialog(this, "Proveedor eliminado exitosamente");
                limpiarFormulario();
                actualizarTabla();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
            }
        }
    }
}