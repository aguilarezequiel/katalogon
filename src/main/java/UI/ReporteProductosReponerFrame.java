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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReporteProductosReponerFrame extends JInternalFrame {
    
    private ArticuloService articuloService;
    private OrdenCompraService ordenCompraService;
    
    private JTable tablaArticulos;
    private DefaultTableModel modeloTabla;
    
    // NUEVO: Label para mostrar fecha/hora actual del sistema
    private JLabel lblFechaHoraSistema;
    private Timer timerFechaHora;
    
    public ReporteProductosReponerFrame() {
        super("Reporte - Productos a Reponer", true, true, true, true);
        articuloService = new ArticuloService();
        ordenCompraService = new OrdenCompraService();
        initComponents();
        iniciarRelojSistema();
        cargarDatos();
    }
    
    private void initComponents() {
        setSize(1100, 600);
        setLayout(new BorderLayout());
        
        // Panel superior - Título y fecha del sistema
        JPanel panelSuperior = new JPanel(new BorderLayout());
        
        JPanel panelTitulo = new JPanel();
        JLabel lblTitulo = new JLabel("Productos que Requieren Reposición");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelTitulo.add(lblTitulo);
        panelSuperior.add(panelTitulo, BorderLayout.CENTER);
        
        // NUEVO: Panel para fecha/hora del sistema
        JPanel panelFechaSistema = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelFechaSistema.add(new JLabel("Fecha/Hora Sistema:"));
        lblFechaHoraSistema = new JLabel();
        lblFechaHoraSistema.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblFechaHoraSistema.setForeground(new Color(0, 100, 0));
        panelFechaSistema.add(lblFechaHoraSistema);
        panelSuperior.add(panelFechaSistema, BorderLayout.EAST);
        
        add(panelSuperior, BorderLayout.NORTH);
        
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
                        } else if (estado != null && estado.startsWith("REVISAR - Intervalo")) {
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
        
        JLabel lblTiempoFijo = new JLabel("■ Intervalo Fijo - Revisar");
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
        
        JButton btnGenerarAutoTF = new JButton("Generar Auto TF");
        btnGenerarAutoTF.setToolTipText("Generar órdenes automáticas para Tiempo Fijo");
        btnGenerarAutoTF.addActionListener(e -> generarOrdenesAutomaticasTiempoFijo());
        
        JButton btnDiagnostico = new JButton("Diagnóstico");
        btnDiagnostico.setToolTipText("Diagnóstico del artículo seleccionado");
        btnDiagnostico.addActionListener(e -> depurarArticuloSeleccionado());

        panelBotones.add(btnGenerarOC);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnDiagnostico);
        panelBotones.add(btnExportar);
        panelBotones.add(btnCerrar);
        panelBotones.add(btnGenerarAutoTF);
        panelInferior.add(panelBotones, BorderLayout.EAST);
        
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    // NUEVO: Método para inicializar el reloj del sistema
    private void iniciarRelojSistema() {
        // Actualizar inmediatamente
        actualizarFechaHoraSistema();
        
        // Timer que se actualiza cada segundo
        timerFechaHora = new Timer(1000, e -> actualizarFechaHoraSistema());
        timerFechaHora.start();
    }
    
    // NUEVO: Método para actualizar la fecha/hora mostrada
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
            
            // **CORRECCIÓN FUNDAMENTAL**: Usar TODOS los artículos activos, no solo los que devuelve el servicio
            List<Articulo> todosArticulos = articuloService.obtenerTodos();
            
            for (Articulo a : todosArticulos) {
                
                // **PASO 1**: Verificar si debe aparecer en el reporte
                if (!debeAparecerEnReporte(a)) {
                    continue;
                }
                
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
            
            if (modeloTabla.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No hay productos que requieran reposición en este momento");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * **MÉTODO CORREGIDO**: Determina si un artículo debe aparecer en el reporte
     * según la lógica correcta de cada modelo de inventario
     */
    private boolean debeAparecerEnReporte(Articulo articulo) {
        if (articulo.getModeloInventario() == null) return false;
        
        // PRIMERO verificar si tiene órdenes activas
        if (tieneOrdenActiva(articulo)) {
            return false; // No debe aparecer si ya tiene orden activa
        }
        
        String modelo = articulo.getModeloInventario().getNombreMetodo();
        
        if (ModeloInventario.LOTE_FIJO.equals(modelo)) {
            // **LOTE FIJO**: Debe haber alcanzado el punto de pedido Y tener configuración válida
            if (articulo.getPuntoPedido() != null && 
                articulo.getStockActual() <= articulo.getPuntoPedido()) {
                // Validar que tenga lote óptimo configurado
                return articulo.getLoteOptimo() != null && articulo.getLoteOptimo() > 0;
            }
            return false;
            
        } else if (ModeloInventario.INTERVALO_FIJO.equals(modelo)) {
            // **INTERVALO FIJO CORREGIDO**: Solo verificar si ha pasado el tiempo, SIN importar stock
            return hasPasadoIntervaloTiempoFijo(articulo);
        }
        
        return false;
    }
    
    /**
     * **MÉTODO CORREGIDO**: Verifica únicamente si ha pasado el intervalo de tiempo
     * SIN considerar el stock actual ni otros factores
     */
    private boolean hasPasadoIntervaloTiempoFijo(Articulo articulo) {
        // Verificar configuración básica
        if (articulo.getTiempoIntervaloMinutos() == null || articulo.getTiempoIntervaloMinutos() <= 0) {
            return false;
        }
        
        // Si nunca se compró, debe aparecer (puede pedir)
        if (articulo.getFechaUltimaCompra() == null) {
            return true;
        }
        
        // Verificar si ha pasado el intervalo (INDEPENDIENTE del stock)
        LocalDateTime ahora = LocalDateTime.now();
        long minutosTranscurridos = java.time.temporal.ChronoUnit.MINUTES.between(
            articulo.getFechaUltimaCompra(), ahora);
        
        // Debug mejorado
        System.out.println("DEBUG INTERVALO FIJO - " + articulo.getDescripcionArticulo() + ":");
        System.out.println("  - Fecha última compra: " + articulo.getFechaUltimaCompra());
        System.out.println("  - Intervalo configurado: " + articulo.getTiempoIntervaloMinutos() + " minutos");
        System.out.println("  - Minutos transcurridos: " + minutosTranscurridos);
        System.out.println("  - ¿Ha pasado intervalo?: " + (minutosTranscurridos >= articulo.getTiempoIntervaloMinutos()));
        
        return minutosTranscurridos >= articulo.getTiempoIntervaloMinutos();
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
            String intervalo = "Intervalo: " + (articulo.getTiempoIntervaloMinutos() != null ? 
                formatearTiempo(articulo.getTiempoIntervaloMinutos()) : "No definido");
            
            if (articulo.getFechaUltimaCompra() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                
                // **NUEVO**: Mostrar tiempo transcurrido también
                LocalDateTime ahora = LocalDateTime.now();
                long minutosTranscurridos = java.time.temporal.ChronoUnit.MINUTES.between(
                    articulo.getFechaUltimaCompra(), ahora);
                String tiempoTranscurrido = formatearTiempo((int) minutosTranscurridos);
                
                intervalo += " | Última compra: " + articulo.getFechaUltimaCompra().format(formatter) + 
                           " | Transcurrido: " + tiempoTranscurrido;
            } else {
                intervalo += " | Sin compras registradas";
            }
            
            return intervalo;
        }
        
        return "N/A";
    }
    
    private String formatearTiempo(int minutos) {
        if (minutos < 60) {
            return minutos + " min";
        } else if (minutos < 1440) { // menos de 1 día
            int horas = minutos / 60;
            int minutosRestantes = minutos % 60;
            return horas + "h" + (minutosRestantes > 0 ? " " + minutosRestantes + "m" : "");
        } else {
            int dias = minutos / 1440;
            int horasRestantes = (minutos % 1440) / 60;
            return dias + " días" + (horasRestantes > 0 ? " " + horasRestantes + "h" : "");
        }
    }

    private void generarOrdenesAutomaticasTiempoFijo() {
        try {
            List<Articulo> todosArticulos = articuloService.obtenerTodos();
            int ordenesGeneradas = 0;
            StringBuilder log = new StringBuilder("LOG de generación automática:\n\n");
            
            for (Articulo a : todosArticulos) {
                log.append("Evaluando artículo: ").append(a.getDescripcionArticulo()).append("\n");
                
                // Verificar que sea modelo INTERVALO_FIJO
                if (!ModeloInventario.INTERVALO_FIJO.equals(a.getModeloInventario().getNombreMetodo())) {
                    log.append("  - No es modelo INTERVALO_FIJO, omitido\n\n");
                    continue;
                }
                
                // Verificar proveedor predeterminado
                if (a.getProveedorPredeterminado() == null) {
                    log.append("  - Sin proveedor predeterminado, omitido\n\n");
                    continue;
                }
                
                // Verificar si tiene órdenes activas
                if (tieneOrdenActiva(a)) {
                    log.append("  - Ya tiene orden activa, omitido\n\n");
                    continue;
                }
                
                // **CORRECCIÓN**: Verificar si ha pasado el intervalo (SIN importar stock)
                if (!hasPasadoIntervaloTiempoFijo(a)) {
                    log.append("  - No ha pasado el intervalo, omitido\n\n");
                    continue;
                }
                
                // Calcular cantidad a pedir
                Integer cantidadAPedir = a.calcularCantidadAPedirTiempoFijo();
                log.append("  - Cantidad calculada: ").append(cantidadAPedir).append("\n");
                
                if (cantidadAPedir == null || cantidadAPedir <= 0) {
                    log.append("  - Cantidad inválida, omitido\n\n");
                    continue;
                }
                
                // Crear orden de compra
                try {
                    OrdenCompra orden = new OrdenCompra();
                    orden.setArticulo(a);
                    orden.setProveedor(a.getProveedorPredeterminado());
                    orden.setCantidad(cantidadAPedir);
                    
                    ordenCompraService.crearOrdenCompra(orden);
                    ordenesGeneradas++;
                    
                    log.append("  - ✓ Orden creada exitosamente (ID: ").append(orden.getCodOC()).append(")\n\n");
                    
                } catch (Exception e) {
                    log.append("  - ✗ Error al crear orden: ").append(e.getMessage()).append("\n\n");
                }
            }
            
            // Mostrar resultado
            String mensaje = "Se generaron " + ordenesGeneradas + " órdenes de compra automáticamente";
            if (ordenesGeneradas == 0) {
                mensaje = "No se encontraron artículos que requieran órdenes automáticas en este momento";
            }
            
            // Mostrar log detallado en caso de debug
            System.out.println(log.toString());
            
            JOptionPane.showMessageDialog(this, mensaje);
            
            if (ordenesGeneradas > 0) {
                cargarDatos(); // Actualizar la lista
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al generar órdenes automáticas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private Integer calcularCantidadAPedir(Articulo articulo) {
        String modelo = articulo.getModeloInventario().getNombreMetodo();
        
        if (ModeloInventario.LOTE_FIJO.equals(modelo)) {
            // Para lote fijo, usar el lote óptimo si el artículo está por debajo del punto de pedido
            if (articulo.getPuntoPedido() != null && 
                articulo.getStockActual() <= articulo.getPuntoPedido()) {
                return articulo.getLoteOptimo() != null ? 
                    articulo.getLoteOptimo().intValue() : 0;
            }
            return 0;
            
        } else if (ModeloInventario.INTERVALO_FIJO.equals(modelo)) {
            // **INTERVALO FIJO CORREGIDO**: Calcular cantidad SIEMPRE que haya pasado el intervalo
            Integer cantidadCalculada = articulo.calcularCantidadAPedirTiempoFijo();
            return cantidadCalculada != null ? cantidadCalculada : 0;
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
            if (articulo.getFechaUltimaCompra() == null) {
                return "REVISAR - Sin Compras Registradas";
            } else {
                // Mostrar tiempo transcurrido desde última compra
                LocalDateTime ahora = LocalDateTime.now();
                long minutosTranscurridos = java.time.temporal.ChronoUnit.MINUTES.between(
                    articulo.getFechaUltimaCompra(), ahora);
                
                String tiempoTranscurrido = formatearTiempo((int) minutosTranscurridos);
                return "REVISAR - Intervalo Cumplido (" + tiempoTranscurrido + ")";
            }
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
        
        // Información adicional para modelo de tiempo fijo
        String infoIntervalo = "";
        if (ModeloInventario.INTERVALO_FIJO.equals(modelo) && articulo.getTiempoIntervaloMinutos() != null) {
            infoIntervalo = "\nIntervalo configurado: " + formatearTiempo(articulo.getTiempoIntervaloMinutos());
            
            if (articulo.getFechaUltimaCompra() != null) {
                LocalDateTime ahora = LocalDateTime.now();
                long minutosTranscurridos = java.time.temporal.ChronoUnit.MINUTES.between(
                    articulo.getFechaUltimaCompra(), ahora);
                infoIntervalo += "\nTiempo transcurrido: " + formatearTiempo((int) minutosTranscurridos);
            }
        }
        
        String mensaje = String.format(
            "¿Desea generar una orden de compra para:\n\n" +
            "Artículo: %s\n" +
            "Modelo: %s%s\n" +
            "Cantidad (%s): %d unidades\n" +
            "Proveedor: %s",
            articulo.getDescripcionArticulo(),
            modelo,
            infoIntervalo,
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
    
    // MÉTODO DE DEPURACIÓN - Para diagnosticar problemas
    private void depurarArticuloSeleccionado() {
        int filaSeleccionada = tablaArticulos.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un artículo para depurar");
            return;
        }
        
        Integer idArticulo = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
        Articulo articulo = articuloService.obtenerPorId(idArticulo);
        
        if (articulo != null) {
            // Imprimir diagnóstico completo en consola
            System.out.println("=== DIAGNÓSTICO ARTÍCULO: " + articulo.getDescripcionArticulo() + " ===");
            System.out.println("ID: " + articulo.getCodArticulo());
            System.out.println("Modelo: " + articulo.getModeloInventario().getNombreMetodo());
            System.out.println("Stock Actual: " + articulo.getStockActual());
            System.out.println("Stock Seguridad: " + articulo.getStockSeguridad());
            System.out.println("Demanda Anual: " + articulo.getDemanda());
            
            // Mostrar información en diálogo
            StringBuilder info = new StringBuilder();
            info.append("DIAGNÓSTICO DEL ARTÍCULO\n\n");
            info.append("Descripción: ").append(articulo.getDescripcionArticulo()).append("\n");
            info.append("Modelo: ").append(articulo.getModeloInventario().getNombreMetodo()).append("\n");
            info.append("Stock Actual: ").append(articulo.getStockActual()).append("\n");
            info.append("Stock Seguridad: ").append(articulo.getStockSeguridad()).append("\n");
            info.append("Demanda Anual: ").append(articulo.getDemanda()).append("\n\n");
            
            if (ModeloInventario.INTERVALO_FIJO.equals(articulo.getModeloInventario().getNombreMetodo())) {
                info.append("=== CONFIGURACIÓN INTERVALO FIJO ===\n");
                info.append("Intervalo configurado: ").append(articulo.formatearTiempoIntervalo()).append("\n");
                info.append("Intervalo en minutos: ").append(articulo.getTiempoIntervaloMinutos()).append("\n");
                info.append("Última compra: ").append(
                    articulo.getFechaUltimaCompra() != null ? 
                    articulo.getFechaUltimaCompra().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : 
                    "Nunca").append("\n");
                
                if (articulo.getFechaUltimaCompra() != null) {
                    long minutosTranscurridos = java.time.temporal.ChronoUnit.MINUTES.between(
                        articulo.getFechaUltimaCompra(), LocalDateTime.now());
                    info.append("Tiempo transcurrido: ").append(formatearTiempo((int) minutosTranscurridos)).append("\n");
                    info.append("¿Ha pasado intervalo?: ").append(
                        minutosTranscurridos >= articulo.getTiempoIntervaloMinutos() ? "SÍ" : "NO").append("\n");
                }
                
                info.append("\nProveedor predeterminado: ").append(
                    articulo.getProveedorPredeterminado() != null ? 
                    articulo.getProveedorPredeterminado().getNombreProveedor() : "NO ASIGNADO").append("\n");
                
                Integer cantidad = articulo.calcularCantidadAPedirTiempoFijo();
                info.append("\n🎯 CANTIDAD CALCULADA: ").append(cantidad).append(" unidades\n");
                
                if (cantidad == 0) {
                    info.append("\n⚠️ PROBLEMA DETECTADO: La cantidad es 0\n");
                    info.append("Posibles causas:\n");
                    info.append("• Demanda no configurada o = 0\n");
                    info.append("• Proveedor predeterminado no asignado\n");
                    info.append("• Datos del proveedor incompletos\n");
                    info.append("• Stock actual muy alto vs demanda\n");
                }
                
            } else if (ModeloInventario.LOTE_FIJO.equals(articulo.getModeloInventario().getNombreMetodo())) {
                info.append("=== CONFIGURACIÓN LOTE FIJO ===\n");
                info.append("Lote Óptimo: ").append(articulo.getLoteOptimo()).append("\n");
                info.append("Punto Pedido: ").append(articulo.getPuntoPedido()).append("\n");
                info.append("¿Stock <= Punto Pedido?: ").append(
                    articulo.getStockActual() <= articulo.getPuntoPedido() ? "SÍ" : "NO").append("\n");
            }
            
            info.append("\nCGI: ").append(articulo.getCgi()).append("\n");
            info.append("¿Tiene órdenes activas?: ").append(tieneOrdenActiva(articulo) ? "SÍ" : "NO").append("\n");
            
            JTextArea textArea = new JTextArea(info.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 500));
            
            JOptionPane.showMessageDialog(this, scrollPane, 
                "Diagnóstico: " + articulo.getDescripcionArticulo(), 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}