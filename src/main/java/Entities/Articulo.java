package Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JOptionPane;

@Entity
@Table(name = "articulos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Articulo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codArticulo;
    
    @Column(nullable = false, length = 100)
    private String descripcionArticulo;
    
    @Column(nullable = false)
    private Integer stockActual;
    
    @Column(nullable = false)
    private Double stockSeguridad;
    
    @Column(nullable = false)
    private Double demanda;
    
    @Column(nullable = false)
    private Double costoAlmacenamiento;
    
    private LocalDateTime fechaHoraBaja;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    // Campos calculados para modelos de inventario
    private Double loteOptimo;
    private Double puntoPedido;
    
    // CORREGIDO: Mapeo correcto para la columna tiempo_intervalo_minutos
    @Column(name = "tiempoIntervaloMinutos")
    private Integer tiempoIntervaloMinutos; // Para modelo de tiempo fijo (en minutos)
    
    // CGI (Costo de Gestión de Inventario) - calculado
    private Double cgi;
    
    // Relaciones - CAMBIO A EAGER PARA EVITAR LAZY INITIALIZATION
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_predeterminado_id")
    private Proveedor proveedorPredeterminado;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "modelo_inventario_id", nullable = false)
    private ModeloInventario modeloInventario;
    
    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ArticuloProveedor> listaProveedores;
    
    @OneToMany(mappedBy = "articulo", fetch = FetchType.EAGER)
    private List<VentaArticulo> ventas;
    
    @OneToMany(mappedBy = "articulo", fetch = FetchType.EAGER)
    private List<OrdenCompra> ordenesCompra;
    
    @Column(nullable = true)
    private LocalDateTime fechaUltimaCompra;
    
    public LocalDateTime getFechaUltimaCompra() {
        return fechaUltimaCompra;
    }

    public void setFechaUltimaCompra(LocalDateTime fechaUltimaCompra) {
        this.fechaUltimaCompra = fechaUltimaCompra;
    }   

    // MÉTODOS AUXILIARES PARA COMPATIBILIDAD
    /**
     * @deprecated Usar getTiempoIntervaloMinutos() en su lugar
     */
    @Deprecated
    public Integer getTiempoIntervalo() {
        return tiempoIntervaloMinutos != null ? tiempoIntervaloMinutos / (24 * 60) : null;
    }
    
    /**
     * @deprecated Usar setTiempoIntervaloMinutos() en su lugar
     */
    @Deprecated
    public void setTiempoIntervalo(Integer dias) {
        this.tiempoIntervaloMinutos = dias != null ? dias * 24 * 60 : null;
    }

    // Métodos de negocio
    public void calcularLoteFijo() {
        if (proveedorPredeterminado != null && demanda != null && demanda > 0) {
            // Buscar datos del proveedor predeterminado
            ArticuloProveedor apPred = obtenerDatosProveedorPredeterminado();
            if (apPred != null && apPred.getCostoPedido() > 0 && costoAlmacenamiento != null && costoAlmacenamiento > 0) {
                // Q* = sqrt(2 * D * S / H)
                this.loteOptimo = Math.sqrt((2 * demanda * apPred.getCostoPedido()) / costoAlmacenamiento);
                
                // Punto de pedido = d * L + SS
                this.puntoPedido = (demanda / 365 * apPred.getDemoraEntrega()) + 
                    (stockSeguridad != null ? stockSeguridad : 0.0);
            } else {
                // Si no hay proveedor predeterminado configurado, poner valores por defecto
                this.loteOptimo = 0.0;
                this.puntoPedido = stockSeguridad != null ? stockSeguridad : 0.0;
            }
        } else {
            this.loteOptimo = 0.0;
            this.puntoPedido = stockSeguridad != null ? stockSeguridad : 0.0;
        }
    }
    
    public void calcularTiempoFijo() {
        // Para modelo de tiempo fijo, el intervalo se define manualmente
        // El stock de seguridad sigue siendo definido por el usuario
        // No calculamos inventario máximo automáticamente
        if (tiempoIntervaloMinutos == null || tiempoIntervaloMinutos <= 0) {
            this.tiempoIntervaloMinutos = 30 * 24 * 60; // 30 días por defecto en minutos
        }
    }
    
    public Integer calcularCantidadAPedirTiempoFijo() {
        if (!ModeloInventario.INTERVALO_FIJO.equals(modeloInventario.getNombreMetodo())) {
            System.out.println("DEBUG: Artículo no es INTERVALO_FIJO: " + modeloInventario.getNombreMetodo());
            return 0;
        }
        
        if (demanda == null || demanda <= 0) {
            System.out.println("DEBUG calcularCantidadAPedirTiempoFijo - Demanda inválida: " + demanda);
            return 0;
        }
        
        if (tiempoIntervaloMinutos == null || tiempoIntervaloMinutos <= 0) {
            System.out.println("DEBUG calcularCantidadAPedirTiempoFijo - Intervalo inválido: " + tiempoIntervaloMinutos);
            return 0;
        }
        
        if (proveedorPredeterminado == null) {
            System.out.println("DEBUG calcularCantidadAPedirTiempoFijo - Sin proveedor predeterminado");
            return 0;
        }
        
        ArticuloProveedor apPred = obtenerDatosProveedorPredeterminado();
        if (apPred == null) {
            System.out.println("DEBUG: No se encontraron datos del proveedor predeterminado");
            return 0;
        }
        
        try {
            // Convertir minutos a días para los cálculos
            double intervaloDias = tiempoIntervaloMinutos / (24.0 * 60.0);
            
            // **FÓRMULA CORREGIDA PARA TIEMPO FIJO**:
            // Cantidad = Demanda durante el período + Demanda durante tiempo de entrega + Stock seguridad - Stock actual
            
            double demandaDiaria = demanda / 365.0;
            double demandaPeriodo = demandaDiaria * intervaloDias;
            double demandaEntrega = demandaDiaria * apPred.getDemoraEntrega();
            double stockSeguridad = this.stockSeguridad != null ? this.stockSeguridad : 0.0;
            
            // **CORRECCIÓN CRÍTICA**: La cantidad necesaria debe considerar el inventario objetivo
            // Inventario objetivo = Demanda durante (intervalo + tiempo entrega) + Stock seguridad
            double inventarioObjetivo = demandaPeriodo + demandaEntrega + stockSeguridad;
            double cantidadNecesaria = inventarioObjetivo - stockActual;
            
            // **VALIDACIÓN**: Si el resultado es negativo o muy pequeño, aplicar una cantidad mínima
            int resultado;
            if (cantidadNecesaria <= 0) {
                // Si tenemos suficiente stock, pero ha pasado el intervalo, podríamos pedir una cantidad mínima
                // O usar solo la demanda del período
                resultado = Math.max(1, (int) Math.ceil(demandaPeriodo));
            } else {
                resultado = (int) Math.ceil(cantidadNecesaria);
            }
            
            System.out.println("DEBUG calcularCantidadAPedirTiempoFijo - " + descripcionArticulo + ":");
            System.out.println("  - Demanda anual: " + demanda);
            System.out.println("  - Demanda diaria: " + String.format("%.2f", demandaDiaria));
            System.out.println("  - Intervalo días: " + String.format("%.2f", intervaloDias));
            System.out.println("  - Demanda período: " + String.format("%.2f", demandaPeriodo));
            System.out.println("  - Demanda entrega: " + String.format("%.2f", demandaEntrega));
            System.out.println("  - Stock seguridad: " + stockSeguridad);
            System.out.println("  - Stock actual: " + stockActual);
            System.out.println("  - Inventario objetivo: " + String.format("%.2f", inventarioObjetivo));
            System.out.println("  - Cantidad necesaria: " + String.format("%.2f", cantidadNecesaria));
            System.out.println("  - Resultado final: " + resultado);
            
            return resultado;
            
        } catch (Exception e) {
            System.out.println("Error en calcularCantidadAPedirTiempoFijo: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
   

    public void calcularCGI() {
        try {
            if (demanda != null && demanda > 0) {
                String modelo = modeloInventario != null ? modeloInventario.getNombreMetodo() : "";
                
                if (ModeloInventario.LOTE_FIJO.equals(modelo)) {
                    calcularCGILoteFijo();
                } else if (ModeloInventario.INTERVALO_FIJO.equals(modelo)) {
                    calcularCGITiempoFijo();
                } else {
                    this.cgi = 0.0;
                }
            } else {
                this.cgi = 0.0;
            }
        } catch (Exception e) {
            System.out.println("Error calculando CGI: " + e.getMessage());
            this.cgi = 0.0;
        }
    }
    
    private void calcularCGILoteFijo() {
        if (loteOptimo != null && loteOptimo > 0 && costoAlmacenamiento != null) {
            ArticuloProveedor apPred = obtenerDatosProveedorPredeterminado();
            if (apPred != null && apPred.getCostoPedido() != null && apPred.getPrecioUnitario() != null) {
                double costoOrdenar = (demanda / loteOptimo) * apPred.getCostoPedido();
                double costoMantener = (loteOptimo / 2) * costoAlmacenamiento;
                double costoAdquisicion = demanda * apPred.getPrecioUnitario();
                this.cgi = costoOrdenar + costoMantener + costoAdquisicion;
            } else {
                // CGI básico sin proveedor
                this.cgi = demanda * costoAlmacenamiento;
            }
        } else {
            this.cgi = 0.0;
        }
    }
    
    private void calcularCGITiempoFijo() {
        // Para tiempo fijo, calcular CGI basado en el intervalo
        ArticuloProveedor apPred = obtenerDatosProveedorPredeterminado();
        if (apPred != null && apPred.getPrecioUnitario() != null && costoAlmacenamiento != null) {
            // MODIFICADO: Convertir minutos a días para el cálculo
            double intervaloDias = tiempoIntervaloMinutos != null ? tiempoIntervaloMinutos / (24.0 * 60.0) : 30;
            
            // CGI simplificado para tiempo fijo
            double costoPedidoAnual = apPred.getCostoPedido() * (365.0 / intervaloDias);
            double costoMantenimiento = stockSeguridad * costoAlmacenamiento;
            double costoAdquisicion = demanda * apPred.getPrecioUnitario();
            this.cgi = costoPedidoAnual + costoMantenimiento + costoAdquisicion;
        } else {
            // CGI básico
            this.cgi = demanda != null ? demanda * (costoAlmacenamiento != null ? costoAlmacenamiento : 1.0) : 0.0;
        }
    }
    
    private ArticuloProveedor obtenerDatosProveedorPredeterminado() {
        if (proveedorPredeterminado != null && listaProveedores != null) {
            return listaProveedores.stream()
                .filter(ap -> ap.getProveedor() != null && 
                         ap.getProveedor().getCodProveedor() != null &&
                         ap.getProveedor().getCodProveedor().equals(proveedorPredeterminado.getCodProveedor()) 
                         && ap.getActivo())
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    public boolean necesitaReposicion() {
        if (modeloInventario == null) return false;
        
        if (ModeloInventario.LOTE_FIJO.equals(modeloInventario.getNombreMetodo())) {
            return stockActual != null && puntoPedido != null && 
                stockActual <= puntoPedido && !tieneOrdenPendienteOEnviada();
                
        } else if (ModeloInventario.INTERVALO_FIJO.equals(modeloInventario.getNombreMetodo())) {
            
            // Verificar configuración básica
            if (tiempoIntervaloMinutos == null || tiempoIntervaloMinutos <= 0) {
                return false;
            }
            
            // Si nunca se compró, necesita reposición
            if (fechaUltimaCompra == null) {
                return !tieneOrdenPendienteOEnviada();
            }
            
            // Verificar si ha pasado el intervalo
            LocalDateTime ahora = LocalDateTime.now();
            long minutosTranscurridos = java.time.temporal.ChronoUnit.MINUTES.between(
                fechaUltimaCompra, ahora);
                
            boolean hasPasadoIntervalo = minutosTranscurridos >= tiempoIntervaloMinutos;
            boolean sinOrdenActiva = !tieneOrdenPendienteOEnviada();
            
            return hasPasadoIntervalo && sinOrdenActiva;
        }
        return false;
    }
    
    public boolean estaEnStockSeguridad() {
        if (stockActual == null || stockSeguridad == null) return false;
        return stockActual <= stockSeguridad;
    }
    
    private boolean tieneOrdenPendienteOEnviada() {
        if (ordenesCompra == null) return false;
        
        return ordenesCompra.stream()
            .anyMatch(oc -> oc.getEstadoActual() != null && 
                (oc.getEstadoActual().getEstado().getNombreEstadoOrdenCompra().equals("PENDIENTE") ||
                 oc.getEstadoActual().getEstado().getNombreEstadoOrdenCompra().equals("ENVIADA")));
    }
    
    // MÉTODOS AUXILIARES PARA FORMATEO DE TIEMPO
    public String formatearTiempoIntervalo() {
        if (tiempoIntervaloMinutos == null) return "No definido";
        
        if (tiempoIntervaloMinutos < 60) {
            return tiempoIntervaloMinutos + " minutos";
        } else if (tiempoIntervaloMinutos < 1440) { // menos de 1 día
            int horas = tiempoIntervaloMinutos / 60;
            int minutos = tiempoIntervaloMinutos % 60;
            return horas + "h " + (minutos > 0 ? minutos + "m" : "");
        } else {
            int dias = tiempoIntervaloMinutos / 1440;
            int horasRestantes = (tiempoIntervaloMinutos % 1440) / 60;
            return dias + " días" + (horasRestantes > 0 ? " " + horasRestantes + "h" : "");
        }
    }
    
    @Override
    public String toString() {
        return descripcionArticulo != null ? descripcionArticulo : "Artículo " + codArticulo;
    }
}