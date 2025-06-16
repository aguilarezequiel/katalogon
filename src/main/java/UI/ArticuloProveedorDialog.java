package UI;

import Entities.Articulo;
import Entities.ArticuloProveedor;
import javax.swing.*;
import java.awt.*;

public class ArticuloProveedorDialog extends JDialog {
    
    private JTextField txtArticulo;
    private JSpinner spnPrecioUnitario;
    private JSpinner spnDemoraEntrega;
    private JSpinner spnCostoPedido;
    
    private ArticuloProveedor articuloProveedor;
    private boolean aceptado = false;
    
    public ArticuloProveedorDialog(Frame parent, Articulo articulo) {
        super(parent, "Configurar Artículo-Proveedor", true);
        this.articuloProveedor = new ArticuloProveedor();
        this.articuloProveedor.setArticulo(articulo);
        initComponents();
        txtArticulo.setText(articulo.getDescripcionArticulo());
    }
    
    public ArticuloProveedorDialog(Frame parent, ArticuloProveedor ap) {
        super(parent, "Configurar Artículo-Proveedor", true);
        this.articuloProveedor = ap;
        initComponents();
        cargarDatos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(400, 250);
        setLocationRelativeTo(getParent());
        
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Artículo
        gbc.gridx = 0; gbc.gridy = 0;
        panelCentral.add(new JLabel("Artículo:"), gbc);
        
        gbc.gridx = 1;
        txtArticulo = new JTextField(20);
        txtArticulo.setEditable(false);
        panelCentral.add(txtArticulo, gbc);
        
        // Precio Unitario
        gbc.gridx = 0; gbc.gridy = 1;
        panelCentral.add(new JLabel("Precio Unitario:"), gbc);
        
        gbc.gridx = 1;
        spnPrecioUnitario = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999999.0, 0.01));
        panelCentral.add(spnPrecioUnitario, gbc);
        
        // Demora Entrega
        gbc.gridx = 0; gbc.gridy = 2;
        panelCentral.add(new JLabel("Demora Entrega (días):"), gbc);
        
        gbc.gridx = 1;
        spnDemoraEntrega = new JSpinner(new SpinnerNumberModel(0, 0, 365, 1));
        panelCentral.add(spnDemoraEntrega, gbc);
        
        // Costo Pedido
        gbc.gridx = 0; gbc.gridy = 3;
        panelCentral.add(new JLabel("Costo Pedido:"), gbc);
        
        gbc.gridx = 1;
        spnCostoPedido = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999999.0, 0.01));
        panelCentral.add(spnCostoPedido, gbc);
        
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(e -> aceptar());
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> cancelar());
        
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarDatos() {
        txtArticulo.setText(articuloProveedor.getArticulo().getDescripcionArticulo());
        spnPrecioUnitario.setValue(articuloProveedor.getPrecioUnitario());
        spnDemoraEntrega.setValue(articuloProveedor.getDemoraEntrega());
        spnCostoPedido.setValue(articuloProveedor.getCostoPedido());
    }
    
    private void aceptar() {
        if ((Double) spnPrecioUnitario.getValue() <= 0) {
            JOptionPane.showMessageDialog(this, "El precio unitario debe ser mayor a 0");
            return;
        }
        
        if ((Double) spnCostoPedido.getValue() <= 0) {
            JOptionPane.showMessageDialog(this, "El costo de pedido debe ser mayor a 0");
            return;
        }
        
        articuloProveedor.setPrecioUnitario((Double) spnPrecioUnitario.getValue());
        articuloProveedor.setDemoraEntrega((Integer) spnDemoraEntrega.getValue());
        articuloProveedor.setCostoPedido((Double) spnCostoPedido.getValue());
        articuloProveedor.setActivo(true);
        
        aceptado = true;
        dispose();
    }
    
    private void cancelar() {
        aceptado = false;
        dispose();
    }
    
    public boolean isAceptado() {
        return aceptado;
    }
    
    public ArticuloProveedor getArticuloProveedor() {
        return articuloProveedor;
    }
}