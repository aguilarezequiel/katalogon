package UI;

import Service.ArticuloService;
import Entities.Articulo;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AjusteInventarioDialog extends JDialog {
    
    private ArticuloService articuloService;
    
    private JComboBox<Articulo> cmbArticulo;
    private JTextField txtStockActual;
    private JSpinner spnNuevoStock;
    
    public AjusteInventarioDialog(Frame parent, boolean modal) {
        super(parent, "Ajuste de Inventario", modal);
        articuloService = new ArticuloService();
        initComponents();
        cargarArticulos();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(400, 250);
        
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Artículo
        gbc.gridx = 0; gbc.gridy = 0;
        panelCentral.add(new JLabel("Artículo:"), gbc);
        
        gbc.gridx = 1;
        cmbArticulo = new JComboBox<>();
        cmbArticulo.setPreferredSize(new Dimension(250, 25));
        cmbArticulo.addActionListener(e -> actualizarStockActual());
        panelCentral.add(cmbArticulo, gbc);
        
        // Stock Actual
        gbc.gridx = 0; gbc.gridy = 1;
        panelCentral.add(new JLabel("Stock Actual:"), gbc);
        
        gbc.gridx = 1;
        txtStockActual = new JTextField();
        txtStockActual.setEditable(false);
        txtStockActual.setBackground(Color.LIGHT_GRAY);
        panelCentral.add(txtStockActual, gbc);
        
        // Nuevo Stock
        gbc.gridx = 0; gbc.gridy = 2;
        panelCentral.add(new JLabel("Nuevo Stock:"), gbc);
        
        gbc.gridx = 1;
        spnNuevoStock = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        panelCentral.add(spnNuevoStock, gbc);
        
        // Nota
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel lblNota = new JLabel("<html><i>Nota: Este ajuste no genera otras acciones en el sistema</i></html>");
        lblNota.setForeground(Color.GRAY);
        panelCentral.add(lblNota, gbc);
        
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnAjustar = new JButton("Ajustar");
        btnAjustar.addActionListener(e -> ajustarInventario());
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(btnAjustar);
        panelBotones.add(btnCancelar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarArticulos() {
        try {
            cmbArticulo.removeAllItems();
            List<Articulo> articulos = articuloService.obtenerTodos();
            for (Articulo a : articulos) {
                cmbArticulo.addItem(a);
            }
            
            if (cmbArticulo.getItemCount() > 0) {
                actualizarStockActual();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar artículos: " + e.getMessage());
        }
    }
    
    private void actualizarStockActual() {
        Articulo articulo = (Articulo) cmbArticulo.getSelectedItem();
        if (articulo != null) {
            txtStockActual.setText(String.valueOf(articulo.getStockActual()));
            spnNuevoStock.setValue(articulo.getStockActual());
        }
    }
    
    private void ajustarInventario() {
        Articulo articulo = (Articulo) cmbArticulo.getSelectedItem();
        if (articulo == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un artículo");
            return;
        }
        
        int nuevoStock = (Integer) spnNuevoStock.getValue();
        int stockActual = articulo.getStockActual();
        
        if (nuevoStock == stockActual) {
            JOptionPane.showMessageDialog(this, "El nuevo stock es igual al actual");
            return;
        }
        
        String mensaje = String.format(
            "¿Confirma el ajuste de inventario?\n\n" +
            "Artículo: %s\n" +
            "Stock Actual: %d\n" +
            "Nuevo Stock: %d\n" +
            "Diferencia: %+d",
            articulo.getDescripcionArticulo(),
            stockActual,
            nuevoStock,
            (nuevoStock - stockActual)
        );
        
        int respuesta = JOptionPane.showConfirmDialog(this, mensaje, 
            "Confirmar Ajuste", JOptionPane.YES_NO_OPTION);
            
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                articuloService.ajustarInventario(articulo.getCodArticulo(), nuevoStock);
                JOptionPane.showMessageDialog(this, "Inventario ajustado exitosamente");
                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al ajustar: " + e.getMessage());
            }
        }
    }
}