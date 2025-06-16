package UI;

import Service.ArticuloService;
import Entities.Articulo;
import Entities.ArticuloProveedor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;  // IMPORT AGREGADO
import java.awt.*;
import java.util.List;

public class ReporteProveedoresPorArticuloFrame extends JInternalFrame {
    
    private ArticuloService articuloService;
    
    private JComboBox<Articulo> cmbArticulo;
    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;
    
    public ReporteProveedoresPorArticuloFrame() {
        super("Reporte - Proveedores por Artículo", true, true, true, true);
        articuloService = new ArticuloService();
        initComponents();
        cargarArticulos();
    }
    
    private void initComponents() {
        setSize(800, 500);
        setLayout(new BorderLayout());
        
        // Panel superior - Selección
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panelSuperior.add(new JLabel("Seleccione Artículo:"));
        
        cmbArticulo = new JComboBox<>();
        cmbArticulo.setPreferredSize(new Dimension(300, 25));
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
        cmbArticulo.addActionListener(e -> cargarProveedores());
        panelSuperior.add(cmbArticulo);
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> cargarProveedores());
        panelSuperior.add(btnBuscar);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central - Tabla
        String[] columnas = {"Proveedor", "Precio Unitario", "Demora Entrega (días)", 
                            "Costo Pedido", "Es Predeterminado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaProveedores = new JTable(modeloTabla);
        
        // Resaltar proveedor predeterminado
        tablaProveedores.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String esPredeterminado = (String) table.getValueAt(row, 4);
                    if ("SÍ".equals(esPredeterminado)) {
                        c.setBackground(new Color(200, 255, 200));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaProveedores);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior - Información y botones
        JPanel panelInferior = new JPanel(new BorderLayout());
        
        // Panel de información
        JPanel panelInfo = new JPanel(new GridLayout(3, 1));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel lblStock = new JLabel("Stock Actual: -");
        lblStock.setName("lblStock");
        panelInfo.add(lblStock);
        
        JLabel lblPuntoPedido = new JLabel("Punto de Pedido: -");
        lblPuntoPedido.setName("lblPuntoPedido");
        panelInfo.add(lblPuntoPedido);
        
        JLabel lblLoteOptimo = new JLabel("Lote Óptimo: -");
        lblLoteOptimo.setName("lblLoteOptimo");
        panelInfo.add(lblLoteOptimo);
        
        panelInferior.add(panelInfo, BorderLayout.WEST);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnExportar = new JButton("Exportar");
        btnExportar.addActionListener(e -> exportarReporte());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnExportar);
        panelBotones.add(btnCerrar);
        
        panelInferior.add(panelBotones, BorderLayout.EAST);
        
        add(panelInferior, BorderLayout.SOUTH);
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
        Articulo articulo = (Articulo) cmbArticulo.getSelectedItem();
        if (articulo == null) {
            return;
        }
        
        try {
            modeloTabla.setRowCount(0);
            
            // Actualizar información del artículo
            actualizarInfoArticulo(articulo);
            
            // Cargar proveedores
            if (articulo.getListaProveedores() != null) {
                for (ArticuloProveedor ap : articulo.getListaProveedores()) {
                    if (ap.getActivo()) {
                        String esPredeterminado = articulo.getProveedorPredeterminado() != null &&
                            articulo.getProveedorPredeterminado().equals(ap.getProveedor()) ? "SÍ" : "NO";
                        
                        Object[] fila = {
                            ap.getProveedor().getNombreProveedor(),
                            String.format("$ %.2f", ap.getPrecioUnitario()),
                            ap.getDemoraEntrega(),
                            String.format("$ %.2f", ap.getCostoPedido()),
                            esPredeterminado
                        };
                        modeloTabla.addRow(fila);
                    }
                }
            }
            
            if (modeloTabla.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "El artículo no tiene proveedores asociados");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proveedores: " + e.getMessage());
        }
    }
    
    private void actualizarInfoArticulo(Articulo articulo) {
        // Método mejorado para actualizar información
        actualizarLabelPorNombre(this, "lblStock", "Stock Actual: " + articulo.getStockActual());
        actualizarLabelPorNombre(this, "lblPuntoPedido", "Punto de Pedido: " + String.format("%.0f", articulo.getPuntoPedido()));
        actualizarLabelPorNombre(this, "lblLoteOptimo", "Lote Óptimo: " + String.format("%.0f", articulo.getLoteOptimo()));
    }
    
    // Método auxiliar para actualizar labels
    private void actualizarLabelPorNombre(Container container, String nombre, String texto) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel && nombre.equals(component.getName())) {
                ((JLabel) component).setText(texto);
                return;
            } else if (component instanceof Container) {
                actualizarLabelPorNombre((Container) component, nombre, texto);
            }
        }
    }
    
    private void exportarReporte() {
        // Implementación simplificada
        JOptionPane.showMessageDialog(this, 
            "Funcionalidad de exportación no implementada en este prototipo");
    }
}