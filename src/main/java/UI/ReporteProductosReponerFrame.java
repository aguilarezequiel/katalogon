package UI;

import Service.ArticuloService;
import Service.OrdenCompraService;
import Entities.Articulo;
import Entities.OrdenCompra;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;  // IMPORT AGREGADO
import java.awt.*;
import java.util.List;

public class ReporteProductosReponerFrame extends JInternalFrame {
    
    private ArticuloService articuloService;
    private OrdenCompraService ordenCompraService;
    
    private JTable tablaArticulos;
    private DefaultTableModel modeloTabla;
    
    public ReporteProductosReponerFrame() {
        super("Reporte - Productos a Reponer", true, true, true, true);
        articuloService = new ArticuloService();
        ordenCompraService = new OrdenCompraService();
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        setSize(800, 500);
        setLayout(new BorderLayout());
        
        // Panel superior - Título
        JPanel panelTitulo = new JPanel();
        JLabel lblTitulo = new JLabel("Productos que Requieren Reposición");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central - Tabla
        String[] columnas = {"ID", "Descripción", "Stock Actual", "Punto Pedido", 
                            "Stock Seguridad", "Lote Óptimo", "Proveedor Predeterminado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaArticulos = new JTable(modeloTabla);
        
        // Resaltar filas críticas
        tablaArticulos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    try {
                        int stockActual = (Integer) table.getValueAt(row, 2);
                        int stockSeguridad = (Integer) table.getValueAt(row, 4);
                        
                        if (stockActual <= stockSeguridad) {
                            c.setBackground(new Color(255, 200, 200)); // Rojo claro para críticos
                        } else {
                            c.setBackground(new Color(255, 255, 200)); // Amarillo claro para advertencia
                        }
                    } catch (Exception e) {
                        c.setBackground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaArticulos);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior - Botones y leyenda
        JPanel panelInferior = new JPanel(new BorderLayout());
        
        // Leyenda
        JPanel panelLeyenda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelLeyenda.add(new JLabel("Leyenda:"));
        
        JLabel lblAdvertencia = new JLabel("■ Por debajo del punto de pedido");
        lblAdvertencia.setOpaque(true);
        lblAdvertencia.setBackground(new Color(255, 255, 200));
        panelLeyenda.add(lblAdvertencia);
        
        JLabel lblCritico = new JLabel("■ En stock de seguridad");
        lblCritico.setOpaque(true);
        lblCritico.setBackground(new Color(255, 200, 200));
        panelLeyenda.add(lblCritico);
        
        panelInferior.add(panelLeyenda, BorderLayout.WEST);
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnGenerarOC = new JButton("Generar O.C.");
        btnGenerarOC.addActionListener(e -> generarOrdenCompra());
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarDatos());
        
        JButton btnExportar = new JButton("Exportar");
        btnExportar.addActionListener(e -> exportarReporte());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnGenerarOC);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnExportar);
        panelBotones.add(btnCerrar);
        
        panelInferior.add(panelBotones, BorderLayout.EAST);
        
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private void cargarDatos() {
        try {
            modeloTabla.setRowCount(0);
            List<Articulo> articulos = articuloService.obtenerArticulosAReponer();
            
            for (Articulo a : articulos) {
                String proveedorPred = a.getProveedorPredeterminado() != null ? 
                    a.getProveedorPredeterminado().getNombreProveedor() : "No asignado";
                
                Object[] fila = {
                    a.getCodArticulo(),
                    a.getDescripcionArticulo(),
                    a.getStockActual(),
                    String.format("%.0f", a.getPuntoPedido()),
                    a.getStockSeguridad(),
                    String.format("%.0f", a.getLoteOptimo()),
                    proveedorPred
                };
                modeloTabla.addRow(fila);
            }
            
            if (articulos.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No hay productos que requieran reposición en este momento");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }
    
    private void generarOrdenCompra() {
        int filaSeleccionada = tablaArticulos.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un artículo");
            return;
        }
        
        Integer idArticulo = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
        Articulo articulo = articuloService.obtenerPorId(idArticulo);
        
        if (articulo == null) {
            JOptionPane.showMessageDialog(this, "Error al obtener el artículo seleccionado");
            return;
        }
        
        if (articulo.getProveedorPredeterminado() == null) {
            JOptionPane.showMessageDialog(this, 
                "El artículo no tiene proveedor predeterminado asignado");
            return;
        }
        
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Desea generar una orden de compra para:\n" +
            articulo.getDescripcionArticulo() + "\n" +
            "Cantidad: " + String.format("%.0f", articulo.getLoteOptimo()) + " unidades?",
            "Confirmar Orden de Compra",
            JOptionPane.YES_NO_OPTION);
            
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                OrdenCompra orden = new OrdenCompra();
                orden.setArticulo(articulo);
                orden.setProveedor(articulo.getProveedorPredeterminado());
                orden.setCantidad(articulo.getLoteOptimo().intValue());
                
                ordenCompraService.crearOrdenCompra(orden);
                
                JOptionPane.showMessageDialog(this, 
                    "Orden de compra generada exitosamente");
                
                cargarDatos();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al generar orden: " + e.getMessage());
            }
        }
    }
    
    private void exportarReporte() {
        // Implementación simplificada
        JOptionPane.showMessageDialog(this, 
            "Funcionalidad de exportación no implementada en este prototipo");
    }
}