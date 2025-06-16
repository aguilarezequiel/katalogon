package UI;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    
    private JMenuBar menuBar;
    private JDesktopPane desktopPane;
    
    public MainFrame() {
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Katalogon - Sistema de Gestión de Inventarios");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        
        // Crear desktop pane para MDI
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(new Color(240, 240, 240));
        setContentPane(desktopPane);
        
        // Crear menu bar
        createMenuBar();
        
        // Agregar panel de estado
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.add(new JLabel("Sistema listo"));
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setMnemonic('A');
        
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        itemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(itemSalir);
        
        // Menú Maestros
        JMenu menuMaestros = new JMenu("Maestros");
        menuMaestros.setMnemonic('M');
        
        JMenuItem itemArticulos = new JMenuItem("Artículos");
        itemArticulos.setAccelerator(KeyStroke.getKeyStroke("ctrl A"));
        itemArticulos.addActionListener(e -> abrirArticulos());
        menuMaestros.add(itemArticulos);
        
        JMenuItem itemProveedores = new JMenuItem("Proveedores");
        itemProveedores.setAccelerator(KeyStroke.getKeyStroke("ctrl P"));
        itemProveedores.addActionListener(e -> abrirProveedores());
        menuMaestros.add(itemProveedores);
        
        // Menú Movimientos
        JMenu menuMovimientos = new JMenu("Movimientos");
        menuMovimientos.setMnemonic('O');
        
        JMenuItem itemVentas = new JMenuItem("Ventas");
        itemVentas.setAccelerator(KeyStroke.getKeyStroke("ctrl V"));
        itemVentas.addActionListener(e -> abrirVentas());
        menuMovimientos.add(itemVentas);
        
        JMenuItem itemOrdenesCompra = new JMenuItem("Órdenes de Compra");
        itemOrdenesCompra.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        itemOrdenesCompra.addActionListener(e -> abrirOrdenesCompra());
        menuMovimientos.add(itemOrdenesCompra);
        
        menuMovimientos.addSeparator();
        
        JMenuItem itemAjusteInventario = new JMenuItem("Ajuste de Inventario");
        itemAjusteInventario.addActionListener(e -> abrirAjusteInventario());
        menuMovimientos.add(itemAjusteInventario);
        
        // Menú Reportes
        JMenu menuReportes = new JMenu("Reportes");
        menuReportes.setMnemonic('R');
        
        JMenuItem itemProductosReponer = new JMenuItem("Productos a Reponer");
        itemProductosReponer.addActionListener(e -> abrirReporteProductosReponer());
        menuReportes.add(itemProductosReponer);
        
        JMenuItem itemProductosFaltantes = new JMenuItem("Productos Faltantes");
        itemProductosFaltantes.addActionListener(e -> abrirReporteProductosFaltantes());
        menuReportes.add(itemProductosFaltantes);
        
        JMenuItem itemProveedoresPorArticulo = new JMenuItem("Proveedores por Artículo");
        itemProveedoresPorArticulo.addActionListener(e -> abrirReporteProveedoresPorArticulo());
        menuReportes.add(itemProveedoresPorArticulo);
        
        JMenuItem itemArticulosPorProveedor = new JMenuItem("Artículos por Proveedor");
        itemArticulosPorProveedor.addActionListener(e -> abrirReporteArticulosPorProveedor());
        menuReportes.add(itemArticulosPorProveedor);
        
        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setMnemonic('y');
        
        JMenuItem itemAcerca = new JMenuItem("Acerca de...");
        itemAcerca.addActionListener(e -> mostrarAcercaDe());
        menuAyuda.add(itemAcerca);
        
        // Agregar menús a la barra
        menuBar.add(menuArchivo);
        menuBar.add(menuMaestros);
        menuBar.add(menuMovimientos);
        menuBar.add(menuReportes);
        menuBar.add(menuAyuda);
        
        setJMenuBar(menuBar);
    }
    
    private void abrirArticulos() {
        ArticulosFrame frame = new ArticulosFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
    private void abrirProveedores() {
        ProveedoresFrame frame = new ProveedoresFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
    private void abrirVentas() {
        VentasFrame frame = new VentasFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
    private void abrirOrdenesCompra() {
        OrdenesCompraFrame frame = new OrdenesCompraFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
    private void abrirAjusteInventario() {
        AjusteInventarioDialog dialog = new AjusteInventarioDialog(this, true);
        dialog.setVisible(true);
    }
    
    private void abrirReporteProductosReponer() {
        ReporteProductosReponerFrame frame = new ReporteProductosReponerFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
    private void abrirReporteProductosFaltantes() {
        ReporteProductosFaltantesFrame frame = new ReporteProductosFaltantesFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
    private void abrirReporteProveedoresPorArticulo() {
        ReporteProveedoresPorArticuloFrame frame = new ReporteProveedoresPorArticuloFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
    private void abrirReporteArticulosPorProveedor() {
        ReporteArticulosPorProveedorFrame frame = new ReporteArticulosPorProveedorFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this, 
            "Katalogon - Sistema de Gestión de Inventarios\n" +
            "Versión 1.0\n\n" +
            "Desarrollado para Investigación Operativa 2025",
            "Acerca de", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}