package UI;

import Service.ArticuloService;
import Entities.Articulo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;  // IMPORT AGREGADO
import java.awt.*;
import java.util.List;

public class ReporteProductosFaltantesFrame extends JInternalFrame {
    
    private ArticuloService articuloService;
    
    private JTable tablaArticulos;
    private DefaultTableModel modeloTabla;
    
    public ReporteProductosFaltantesFrame() {
        super("Reporte - Productos Faltantes", true, true, true, true);
        articuloService = new ArticuloService();
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        setSize(800, 500);
        setLayout(new BorderLayout());
        
        // Panel superior - Título y descripción
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel("Productos en Stock de Seguridad");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelTitulo.add(lblTitulo, BorderLayout.NORTH);
        
        JLabel lblDescripcion = new JLabel(
            "<html>Los siguientes productos están dentro de su stock de seguridad.<br>" +
            "Se requiere atención inmediata para evitar quiebres de stock.</html>");
        lblDescripcion.setForeground(Color.RED);
        panelTitulo.add(lblDescripcion, BorderLayout.CENTER);
        
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central - Tabla
        String[] columnas = {"ID", "Descripción", "Stock Actual", "Stock Seguridad", 
                            "Diferencia", "Modelo Inventario", "Estado Crítico"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaArticulos = new JTable(modeloTabla);
        
        // Colorear toda la fila en rojo
        tablaArticulos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(new Color(255, 200, 200));
                    
                    // Resaltar en negrita la columna de estado crítico
                    if (column == 6) {
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaArticulos);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior - Estadísticas y botones
        JPanel panelInferior = new JPanel(new BorderLayout());
        
        // Panel de estadísticas
        JPanel panelEstadisticas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel lblTotalArticulos = new JLabel("Total de artículos críticos: 0");
        lblTotalArticulos.setName("lblTotal");
        panelEstadisticas.add(lblTotalArticulos);
        
        panelInferior.add(panelEstadisticas, BorderLayout.WEST);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarDatos());
        
        JButton btnExportar = new JButton("Exportar");
        btnExportar.addActionListener(e -> exportarReporte());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnActualizar);
        panelBotones.add(btnExportar);
        panelBotones.add(btnCerrar);
        
        panelInferior.add(panelBotones, BorderLayout.EAST);
        
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private void cargarDatos() {
        try {
            modeloTabla.setRowCount(0);
            List<Articulo> articulos = articuloService.obtenerArticulosFaltantes();
            
            for (Articulo a : articulos) {
                int diferencia = (int) (a.getStockActual() - a.getStockSeguridad());
                String estadoCritico = diferencia < 0 ? "MUY CRÍTICO" : "CRÍTICO";
                
                Object[] fila = {
                    a.getCodArticulo(),
                    a.getDescripcionArticulo(),
                    a.getStockActual(),
                    a.getStockSeguridad(),
                    diferencia,
                    a.getModeloInventario().getNombreMetodo(),
                    estadoCritico
                };
                modeloTabla.addRow(fila);
            }
            
            // Actualizar contador - Método mejorado
            actualizarLabelPorNombre(this, "lblTotal", "Total de artículos críticos: " + articulos.size());
            
            if (articulos.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No hay productos en stock de seguridad",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
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