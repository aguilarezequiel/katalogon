package UI;

import Service.ArticuloService;
import Entities.Articulo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReporteProductosFaltantesFrame extends JInternalFrame {
    
    private ArticuloService articuloService;
    
    private JTable tablaArticulos;
    private DefaultTableModel modeloTabla;
    
    // Label para mostrar fecha/hora actual del sistema
    private JLabel lblFechaHoraSistema;
    private Timer timerFechaHora;
    
    public ReporteProductosFaltantesFrame() {
        super("Reporte - Productos Faltantes", true, true, true, true);
        articuloService = new ArticuloService();
        initComponents();
        iniciarRelojSistema();
        cargarDatos();
    }
    
    private void initComponents() {
        setSize(900, 600);
        setLayout(new BorderLayout());
        
        // Panel superior - Título, descripción y fecha del sistema
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de título y descripción
        JPanel panelTituloDesc = new JPanel(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("Productos en Stock de Seguridad");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelTituloDesc.add(lblTitulo, BorderLayout.NORTH);
        
        JLabel lblDescripcion = new JLabel(
            "<html>Los siguientes productos están dentro de su stock de seguridad.<br>" +
            "Se requiere atención inmediata para evitar quiebres de stock.</html>");
        lblDescripcion.setForeground(Color.RED);
        lblDescripcion.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        panelTituloDesc.add(lblDescripcion, BorderLayout.CENTER);
        
        panelSuperior.add(panelTituloDesc, BorderLayout.CENTER);
        
        // Panel para fecha/hora del sistema (lado derecho)
        JPanel panelFechaSistema = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelFechaSistema.add(new JLabel("Fecha/Hora Sistema:"));
        lblFechaHoraSistema = new JLabel();
        lblFechaHoraSistema.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblFechaHoraSistema.setForeground(new Color(0, 100, 0));
        panelFechaSistema.add(lblFechaHoraSistema);
        
        panelSuperior.add(panelFechaSistema, BorderLayout.EAST);
        
        add(panelSuperior, BorderLayout.NORTH);
        
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
        
        // Colorear filas según criticidad
        tablaArticulos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    try {
                        String estadoCritico = (String) table.getValueAt(row, 6);
                        Integer diferencia = (Integer) table.getValueAt(row, 4);
                        
                        if ("MUY CRÍTICO".equals(estadoCritico) || diferencia < 0) {
                            c.setBackground(new Color(255, 150, 150)); // Rojo más intenso
                        } else if ("CRÍTICO".equals(estadoCritico)) {
                            c.setBackground(new Color(255, 200, 200)); // Rojo claro
                        } else {
                            c.setBackground(new Color(255, 220, 220)); // Rosa muy claro
                        }
                        
                        // Resaltar en negrita la columna de estado crítico
                        if (column == 6) {
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        }
                    } catch (Exception e) {
                        c.setBackground(new Color(255, 200, 200));
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
        lblTotalArticulos.setFont(new Font("Arial", Font.BOLD, 12));
        panelEstadisticas.add(lblTotalArticulos);
        
        panelEstadisticas.add(new JLabel(" | "));
        
        JLabel lblMuyCriticos = new JLabel("Muy críticos: 0");
        lblMuyCriticos.setName("lblMuyCriticos");
        lblMuyCriticos.setForeground(new Color(150, 0, 0));
        lblMuyCriticos.setFont(new Font("Arial", Font.BOLD, 12));
        panelEstadisticas.add(lblMuyCriticos);
        
        panelInferior.add(panelEstadisticas, BorderLayout.WEST);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarDatos());
        
        JButton btnExportar = new JButton("Exportar");
        btnExportar.addActionListener(e -> exportarReporte());
        
        JButton btnVerReponer = new JButton("Ver Productos a Reponer");
        btnVerReponer.addActionListener(e -> abrirReporteProductosReponer());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panelBotones.add(btnActualizar);
        panelBotones.add(btnExportar);
        panelBotones.add(btnVerReponer);
        panelBotones.add(btnCerrar);
        
        panelInferior.add(panelBotones, BorderLayout.EAST);
        
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    // Método para inicializar el reloj del sistema
    private void iniciarRelojSistema() {
        // Actualizar inmediatamente
        actualizarFechaHoraSistema();
        
        // Timer que se actualiza cada segundo
        timerFechaHora = new Timer(1000, e -> actualizarFechaHoraSistema());
        timerFechaHora.start();
    }
    
    // Método para actualizar la fecha/hora mostrada
    private void actualizarFechaHoraSistema() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        lblFechaHoraSistema.setText(LocalDateTime.now().format(formatter));
    }
    
    // IMPORTANTE: Detener el timer al cerrar la ventana
    @Override
    public void dispose() {
        if (timerFechaHora != null) {
            timerFechaHora.stop();
        }
        super.dispose();
    }
    
    private void cargarDatos() {
        try {
            modeloTabla.setRowCount(0);
            
            // OBTENER SOLO ARTÍCULOS EN STOCK DE SEGURIDAD
            List<Articulo> articulosFaltantes = articuloService.obtenerArticulosFaltantes();
            
            int totalCriticos = 0;
            int muyCriticos = 0;
            
            for (Articulo a : articulosFaltantes) {
                double diferencia = a.getStockActual() - a.getStockSeguridad();
                String estadoCritico;
                
                if (diferencia < 0) {
                    estadoCritico = "MUY CRÍTICO";
                    muyCriticos++;
                } else {
                    estadoCritico = "CRÍTICO";
                }
                totalCriticos++;
                
                Object[] fila = {
                    a.getCodArticulo(),
                    a.getDescripcionArticulo(),
                    a.getStockActual(),
                    String.format("%.0f", a.getStockSeguridad()),
                    (int) diferencia,
                    a.getModeloInventario().getNombreMetodo(),
                    estadoCritico
                };
                modeloTabla.addRow(fila);
            }
            
            // Actualizar contadores
            actualizarLabelPorNombre(this, "lblTotal", "Total de artículos críticos: " + totalCriticos);
            actualizarLabelPorNombre(this, "lblMuyCriticos", "Muy críticos: " + muyCriticos);
            
            if (articulosFaltantes.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "¡Excelente! No hay productos en stock de seguridad en este momento",
                    "Sin Productos Críticos",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Mostrar alerta si hay productos muy críticos
                if (muyCriticos > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "⚠️ ATENCIÓN: Hay " + muyCriticos + " productos MUY CRÍTICOS\n" +
                        "(con stock por debajo del nivel de seguridad)",
                        "Alerta Crítica",
                        JOptionPane.WARNING_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método auxiliar para actualizar labels por nombre
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
    
    // Método para abrir el reporte de productos a reponer
    private void abrirReporteProductosReponer() {
        try {
            // Buscar el JDesktopPane padre
            Container parent = this.getParent();
            while (parent != null && !(parent instanceof JDesktopPane)) {
                parent = parent.getParent();
            }
            
            if (parent instanceof JDesktopPane) {
                JDesktopPane desktop = (JDesktopPane) parent;
                
                // Verificar si ya está abierto
                for (JInternalFrame frame : desktop.getAllFrames()) {
                    if (frame instanceof ReporteProductosReponerFrame) {
                        try {
                            frame.setSelected(true);
                            frame.toFront();
                            return;
                        } catch (Exception ex) {
                            // Ignorar
                        }
                    }
                }
                
                // Si no está abierto, crear nuevo
                ReporteProductosReponerFrame reporteFrame = new ReporteProductosReponerFrame();
                desktop.add(reporteFrame);
                reporteFrame.setVisible(true);
                try {
                    reporteFrame.setSelected(true);
                } catch (Exception ex) {
                    // Ignorar
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "No se pudo abrir el reporte automáticamente. " +
                "Por favor, ábralo manualmente desde el menú Reportes.");
        }
    }
    
    private void exportarReporte() {
        // Implementación simplificada
        JOptionPane.showMessageDialog(this, 
            "Funcionalidad de exportación no implementada en este prototipo");
    }
}