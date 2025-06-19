package UI;

import Service.ArticuloService;
import Service.OrdenCompraService;
import Entities.Articulo;
import Entities.ModeloInventario;
import Entities.OrdenCompra;
import Entities.ArticuloProveedor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
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
        setSize(1000, 600);
        setLayout(new BorderLayout());
        
        // Panel superior - Título
        JPanel panelTitulo = new JPanel();
        JLabel lblTitulo = new JLabel("Productos que Requieren Reposición");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central - Tabla
        String[] columnas = {"ID", "Descripción", "Stock Actual", "Modelo", 
                            "Parámetro Control", "Cantidad a Pedir", "Proveedor Predeterminado", "Estado"};
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
                        String estado = (String) table.getValueAt(row, 7);
                        
                        if ("CRÍTICO - En Stock Seguridad".equals(estado)) {
                            c.setBackground(new Color(255, 200, 200)); // Rojo claro para críticos
                        } else if ("ADVERTENCIA - Alcanzó Punto Pedido".equals(estado)) {
                            c.setBackground(new Color(255, 255, 200)); // Amarillo claro para advertencia
                        } else if ("REVISAR - Tiempo de Intervalo".equals(estado)) {
                            c.setBackground(new Color(200, 230, 255)); // Azul claro para tiempo fijo
                        } else {
                            c.setBackground(Color.WHITE);
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
        
        JLabel lblAdvertencia = new JLabel("■ Lote Fijo - Punto de Pedido");
        lblAdvertencia.setOpaque(true);
        lblAdvertencia.setBackground(new Color(255, 255, 200));
        panelLeyenda.add(lblAdvertencia);
        
        JLabel lblCritico = new JLabel("■ En Stock Seguridad");
        lblCritico.setOpaque(true);
        lblCritico.setBackground(new Color(255, 200, 200));
        panelLeyenda.add(lblCritico);
        
        JLabel lblTiempoFijo = new JLabel("■ Tiempo Fijo - Revisar");
        lblTiempoFijo.setOpaque(true);
        lblTiempoFijo.setBackground(new Color(200, 230, 255));
        panelLeyenda.add(lblTiempoFijo);
        
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
            List<Articulo> todosArticulos = articuloService.obtenerTodos();
            
            for (Articulo a : todosArticulos) {
                if (debeReponer(a)) {
                    String proveedorPred = a.getProveedorPredeterminado() != null ? 
                        a.getProveedorPredeterminado().getNombreProveedor() : "Sin asignar";
                    
                    String modelo = a.getModeloInventario().getNombreMetodo();
                    String parametroControl = obtenerParametroControl(a);
                    Integer cantidadAPedir = calcularCantidadAPedir(a);
                    String estado = determinarEstado(a);
                    
                    Object[] fila = {
                        a.getCodArticulo(),
                        a.getDescripcionArticulo(),
                        a.getStockActual(),
                        modelo,
                        parametroControl,
                        cantidadAPedir,
                        proveedorPred,
                        estado
                    };
                    modeloTabla.addRow(fila);
                }
            }
            
            if (modeloTabla.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No hay productos que requieran reposición en este momento");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }
    
    private boolean debeReponer(Articulo articulo) {
        if (articulo.getModeloInventario() == null) return false;
        
        String modelo = articulo.getModeloInventario().getNombreMetodo();
        
        if (ModeloInventario.LOTE_FIJO.equals(modelo)) {
            // Verificar si alcanzó el punto de pedido y no tiene orden activa
            return articulo.getPuntoPedido() != null && 
                   articulo.getStockActual() <= articulo.getPuntoPedido() &&
                   !tieneOrdenActiva(articulo);
        } else if (ModeloInventario.INTERVALO_FIJO.equals(modelo)) {
            // Para tiempo fijo, siempre mostrar para que el usuario revise manualmente
            // En una implementación completa, esto se basaría en fechas
            return articulo.getTiempoIntervalo() != null && articulo.getTiempoIntervalo() > 0;
        }
        
        return false;
    }
    
    private boolean tieneOrdenActiva(Articulo articulo) {
        // Verificar si tiene órdenes pendientes o enviadas
        return articulo.getOrdenesCompra() != null && 
               articulo.getOrdenesCompra().stream()
                   .anyMatch(oc -> oc.getEstadoActual() != null && 
                       ("PENDIENTE".equals(oc.getEstadoActual().getEstado().getNombreEstadoOrdenCompra()) ||
                        "ENVIADA".equals(oc.getEstadoActual().getEstado().getNombreEstadoOrdenCompra())));
    }
    
    private String obtenerParametroControl(Articulo articulo) {
        String modelo = articulo.getModeloInventario().getNombreMetodo();
        
        if (ModeloInventario.LOTE_FIJO.equals(modelo)) {
            return "Punto Pedido: " + (articulo.getPuntoPedido() != null ? 
                String.format("%.0f", articulo.getPuntoPedido()) : "0");
        } else if (ModeloInventario.INTERVALO_FIJO.equals(modelo)) {
            return "Intervalo: " + (articulo.getTiempoIntervalo() != null ? 
                articulo.getTiempoIntervalo() + " días" : "No definido");
        }
        
        return "N/A";
    }
    
    private Integer calcularCantidadAPedir(Articulo articulo) {
        String modelo = articulo.getModeloInventario().getNombreMetodo();
        
        if (ModeloInventario.LOTE_FIJO.equals(modelo)) {
            // Para lote fijo, usar el lote óptimo
            return articulo.getLoteOptimo() != null ? 
                articulo.getLoteOptimo().intValue() : 0;
        } else if (ModeloInventario.INTERVALO_FIJO.equals(modelo)) {
            // Para tiempo fijo, calcular cantidad necesaria
            return articulo.calcularCantidadAPedirTiempoFijo();
        }
        
        return 0;
    }
    
    private String determinarEstado(Articulo articulo) {
        // Verificar si está en stock de seguridad (más crítico)
        if (articulo.estaEnStockSeguridad()) {
            return "CRÍTICO - En Stock Seguridad";
        }
        
        String modelo = articulo.getModeloInventario().getNombreMetodo();
        
        if (ModeloInventario.LOTE_FIJO.equals(modelo)) {
            return "ADVERTENCIA - Alcanzó Punto Pedido";
        } else if (ModeloInventario.INTERVALO_FIJO.equals(modelo)) {
            return "REVISAR - Tiempo de Intervalo";
        }
        
        return "REVISAR";
    }
    
    private void generarOrdenCompra() {
        int filaSeleccionada = tablaArticulos.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un artículo");
            return;
        }
        
        Integer idArticulo = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
        Integer cantidadAPedir = (Integer) modeloTabla.getValueAt(filaSeleccionada, 5);
        
        Articulo articulo = articuloService.obtenerPorId(idArticulo);
        
        if (articulo == null) {
            JOptionPane.showMessageDialog(this, "Error al obtener el artículo seleccionado");
            return;
        }
        
        if (articulo.getProveedorPredeterminado() == null) {
            JOptionPane.showMessageDialog(this, 
                "El artículo no tiene proveedor predeterminado asignado.\n" +
                "Debe asignar un proveedor predeterminado antes de generar la orden.");
            return;
        }
        
        if (cantidadAPedir <= 0) {
            JOptionPane.showMessageDialog(this, 
                "No se puede calcular la cantidad a pedir.\n" +
                "Verifique que el artículo tenga configurados correctamente los parámetros del modelo.");
            return;
        }
        
        String modelo = articulo.getModeloInventario().getNombreMetodo();
        String detalleModelo = ModeloInventario.LOTE_FIJO.equals(modelo) ? 
            "Lote Óptimo" : "Cantidad calculada para Tiempo Fijo";
        
        String mensaje = String.format(
            "¿Desea generar una orden de compra para:\n\n" +
            "Artículo: %s\n" +
            "Modelo: %s\n" +
            "Cantidad (%s): %d unidades\n" +
            "Proveedor: %s",
            articulo.getDescripcionArticulo(),
            modelo,
            detalleModelo,
            cantidadAPedir,
            articulo.getProveedorPredeterminado().getNombreProveedor()
        );
        
        int respuesta = JOptionPane.showConfirmDialog(this, mensaje, 
            "Confirmar Orden de Compra", JOptionPane.YES_NO_OPTION);
            
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                OrdenCompra orden = new OrdenCompra();
                orden.setArticulo(articulo);
                orden.setProveedor(articulo.getProveedorPredeterminado());
                orden.setCantidad(cantidadAPedir);
                
                ordenCompraService.crearOrdenCompra(orden);
                
                JOptionPane.showMessageDialog(this, 
                    "Orden de compra generada exitosamente\nID: " + orden.getCodOC());
                
                cargarDatos(); // Actualizar la lista
                
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