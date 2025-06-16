package UI;

import Entities.Venta;
import Entities.VentaArticulo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class DetalleVentaDialog extends JDialog {
    
    private Venta venta;
    
    public DetalleVentaDialog(Frame parent, Venta venta) {
        super(parent, "Detalle de Venta", true);
        this.venta = venta;
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(600, 400);
        
        // Panel superior - Información de la venta
        JPanel panelInfo = new JPanel(new GridLayout(3, 2, 10, 5));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        panelInfo.add(new JLabel("ID Venta:"));
        panelInfo.add(new JLabel(String.valueOf(venta.getCodVenta())));
        
        panelInfo.add(new JLabel("Fecha/Hora:"));
        panelInfo.add(new JLabel(venta.getFechaHoraVenta().format(formatter)));
        
        panelInfo.add(new JLabel("Total:"));
        JLabel lblTotal = new JLabel(String.format("$ %.2f", venta.getTotal()));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfo.add(lblTotal);
        
        add(panelInfo, BorderLayout.NORTH);
        
        // Panel central - Detalle de artículos
        String[] columnas = {"Artículo", "Cantidad", "Precio Unit.", "Subtotal"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (VentaArticulo detalle : venta.getDetalleArticulos()) {
            double subtotal = detalle.getCantidadVentaArticulo() * detalle.getPrecioVenta();
            Object[] fila = {
                detalle.getArticulo().getDescripcionArticulo(),
                detalle.getCantidadVentaArticulo(),
                String.format("$ %.2f", detalle.getPrecioVenta()),
                String.format("$ %.2f", subtotal)
            };
            modelo.addRow(fila);
        }
        
        JTable tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior - Botón cerrar
        JPanel panelBoton = new JPanel();
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        panelBoton.add(btnCerrar);
        add(panelBoton, BorderLayout.SOUTH);
    }
}