package UI;

import Service.ProveedorService;
import Service.ArticuloService;
import Entities.Proveedor;
import Entities.Articulo;
import Entities.ArticuloProveedor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;  // Asegúrate de que esté importado
import java.awt.*;
import java.util.List;

public class ReporteArticulosPorProveedorFrame extends JInternalFrame {
    
    private ProveedorService proveedorService;
    private ArticuloService articuloService;
    
    private JComboBox<Proveedor> cmbProveedor;
    private JTable tablaArticulos;
    private DefaultTableModel modeloTabla;
    
    public ReporteArticulosPorProveedorFrame() {
        super("Reporte - Artículos por Proveedor", true, true, true, true);
        proveedorService = new ProveedorService();
        articuloService = new ArticuloService();
        initComponents();
        cargarProveedores();
    }
    
    private void initComponents() {
        setSize(900, 500);
        setLayout(new BorderLayout());
        
        // Panel superior - Selección
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panelSuperior.add(new JLabel("Seleccione Proveedor:"));
        
        cmbProveedor = new JComboBox<>();
        cmbProveedor.setPreferredSize(new Dimension(300, 25));
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
        cmbProveedor.addActionListener(e -> cargarArticulos());
        panelSuperior.add(cmbProveedor);
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> cargarArticulos());
        panelSuperior.add(btnBuscar);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central - Tabla
        String[] columnas = {"ID", "Artículo", "Stock Actual", "Precio Unitario", 
                            "Demora Entrega", "Costo Pedido", "Es Prov. Predeterminado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaArticulos = new JTable(modeloTabla);
        
        // Resaltar artículos donde este es el proveedor predeterminado
        tablaArticulos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String esPredeterminado = (String) table.getValueAt(row, 6);
                    if ("SÍ".equals(esPredeterminado)) {
                        c.setBackground(new Color(200, 230, 255));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaArticulos);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior - Resumen y botones
        JPanel panelInferior = new JPanel(new BorderLayout());
        
        // Panel de resumen
        JPanel panelResumen = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelResumen.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel lblTotalArticulos = new JLabel("Total de artículos: 0");
        lblTotalArticulos.setName("lblTotal");
        panelResumen.add(lblTotalArticulos);
        
        panelResumen.add(new JLabel(" | "));
        
        JLabel lblArticulosPred = new JLabel("Como proveedor predeterminado: 0");
        lblArticulosPred.setName("lblPred");
        panelResumen.add(lblArticulosPred);
        
        panelInferior.add(panelResumen, BorderLayout.WEST);
        
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
    


    private void cargarArticulos() {
        Proveedor proveedor = (Proveedor) cmbProveedor.getSelectedItem();
        if (proveedor == null) {
            return;
        }
        
        try {
            modeloTabla.setRowCount(0);
            int totalArticulos = 0;
            int articulosPredeterminados = 0;
            
            // CORRECCIÓN: Cargar el proveedor completo con sus asociaciones
            Proveedor proveedorCompleto = proveedorService.obtenerPorId(proveedor.getCodProveedor());
            
            if (proveedorCompleto != null && proveedorCompleto.getArticulosProveedor() != null) {
                for (ArticuloProveedor ap : proveedorCompleto.getArticulosProveedor()) {
                    if (ap.getActivo() && ap.getArticulo() != null) {
                        // Recargar el artículo completo para asegurar que las asociaciones estén disponibles
                        Articulo articuloCompleto = articuloService.obtenerPorId(ap.getArticulo().getCodArticulo());
                        
                        if (articuloCompleto != null) {
                            boolean esPredeterminado = articuloCompleto.getProveedorPredeterminado() != null &&
                                articuloCompleto.getProveedorPredeterminado().getCodProveedor()
                                    .equals(proveedor.getCodProveedor());
                            
                            if (esPredeterminado) {
                                articulosPredeterminados++;
                            }
                            
                            Object[] fila = {
                                articuloCompleto.getCodArticulo(),
                                articuloCompleto.getDescripcionArticulo(),
                                articuloCompleto.getStockActual(),
                                String.format("$ %.2f", ap.getPrecioUnitario()),
                                ap.getDemoraEntrega() + " días",
                                String.format("$ %.2f", ap.getCostoPedido()),
                                esPredeterminado ? "SÍ" : "NO"
                            };
                            modeloTabla.addRow(fila);
                            totalArticulos++;
                        }
                    }
                }
            }
            
            // Actualizar contadores
            actualizarContadores(totalArticulos, articulosPredeterminados);
            
            if (totalArticulos == 0) {
                JOptionPane.showMessageDialog(this, 
                    "El proveedor no tiene artículos asociados");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar artículos: " + e.getMessage());
            e.printStackTrace(); // Para debug
        }
    }
    
    private void actualizarContadores(int total, int predeterminados) {
        // Buscar los labels por su nombre de forma más robusta
        actualizarLabelPorNombre(this, "lblTotal", "Total de artículos: " + total);
        actualizarLabelPorNombre(this, "lblPred", "Como proveedor predeterminado: " + predeterminados);
    }
    
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