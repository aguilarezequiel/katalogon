package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {
    
    private JDesktopPane desktopPane;
    private JMenuBar menuBar;
    
    public MainFrame() {
        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setTitle("Katalogon - Sistema de Gestión de Inventarios");
    }
    
    private void initComponents() {
        // Desktop Pane para ventanas internas
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(new Color(240, 240, 240));
        setContentPane(desktopPane);
        
        // Crear menú
        createMenuBar();
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(itemSalir);
        
        // Menú Maestros
        JMenu menuMaestros = new JMenu("Maestros");
        
        JMenuItem itemArticulos = new JMenuItem("Artículos");
        itemArticulos.addActionListener(e -> abrirVentanaArticulos());
        menuMaestros.add(itemArticulos);
        
        JMenuItem itemProveedores = new JMenuItem("Proveedores");
        itemProveedores.addActionListener(e -> abrirVentanaProveedores());
        menuMaestros.add(itemProveedores);
        
        // Menú Movimientos
        JMenu menuMovimientos = new JMenu("Movimientos");
        
        JMenuItem itemOrdenCompra = new JMenuItem("Órdenes de Compra");
        itemOrdenCompra.addActionListener(e -> abrirVentanaOrdenesCompra());
        menuMovimientos.add(itemOrdenCompra);
        
        JMenuItem itemVentas = new JMenuItem("Ventas");
        itemVentas.addActionListener(e -> abrirVentanaVentas());
        menuMovimientos.add(itemVentas);
        
        JMenuItem itemAjusteInventario = new JMenuItem("Ajuste de Inventario");
        itemAjusteInventario.addActionListener(e -> abrirAjusteInventario());
        menuMovimientos.add(itemAjusteInventario);
        
        // Menú Reportes
        JMenu menuReportes = new JMenu("Reportes");
        
        JMenuItem itemProductosReponer = new JMenuItem("Productos a Reponer");
        itemProductosReponer.addActionListener(e -> abrirProductosAReponer());
        menuReportes.add(itemProductosReponer);
        
        // NUEVO: Agregar reporte de productos faltantes
        JMenuItem itemProductosFaltantes = new JMenuItem("Productos Faltantes");
        itemProductosFaltantes.addActionListener(e -> abrirProductosFaltantes());
        menuReportes.add(itemProductosFaltantes);
        
        // Separador
        menuReportes.addSeparator();
        
        JMenuItem itemProveedoresPorArticulo = new JMenuItem("Proveedores por Artículo");
        itemProveedoresPorArticulo.addActionListener(e -> abrirProveedoresPorArticulo());
        menuReportes.add(itemProveedoresPorArticulo);
        
        JMenuItem itemArticulosPorProveedor = new JMenuItem("Artículos por Proveedor");
        itemArticulosPorProveedor.addActionListener(e -> abrirArticulosPorProveedor());
        menuReportes.add(itemArticulosPorProveedor);
        
        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
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
    
    private void abrirProveedoresPorArticulo() {
        ReporteProveedoresPorArticuloFrame frame = new ReporteProveedoresPorArticuloFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void abrirArticulosPorProveedor() {
        ReporteArticulosPorProveedorFrame frame = new ReporteArticulosPorProveedorFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void abrirVentanaArticulos() {
        ArticulosFrame frame = new ArticulosFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void abrirVentanaProveedores() {
        ProveedoresFrame frame = new ProveedoresFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void abrirVentanaOrdenesCompra() {
        OrdenesCompraFrame frame = new OrdenesCompraFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void abrirVentanaVentas() {
        VentasFrame frame = new VentasFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void abrirAjusteInventario() {
        AjusteInventarioDialog dialog = new AjusteInventarioDialog(this, true);
        dialog.setVisible(true);
    }
    
    private void abrirProductosAReponer() {
        ReporteProductosReponerFrame frame = new ReporteProductosReponerFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // NUEVO: Método para abrir el reporte de productos faltantes
    private void abrirProductosFaltantes() {
        ReporteProductosFaltantesFrame frame = new ReporteProductosFaltantesFrame();
        desktopPane.add(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this, 
            "Katalogon - Sistema de Gestión de Inventarios\n" +
            "Versión 1.0\n\n" +
            "Desarrollado para Investigación Operativa\n" +
            "Universidad Tecnológica Nacional\n\n" +
            "Modelos de Inventario implementados:\n" +
            "• Lote Fijo (EOQ)\n" +
            "• Intervalo Fijo\n\n" +
            "Reportes disponibles:\n" +
            "• Productos a Reponer\n" +
            "• Productos Faltantes (Stock de Seguridad)\n" +
            "• Proveedores por Artículo\n" +
            "• Artículos por Proveedor",
            "Acerca de Katalogon",
            JOptionPane.INFORMATION_MESSAGE);
    }
}