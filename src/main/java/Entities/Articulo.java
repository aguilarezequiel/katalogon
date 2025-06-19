package Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

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
    private Integer tiempoIntervalo; // Para modelo de tiempo fijo (en días)
    
    // CGI (Costo de Gestión de Inventario) - calculado
    private Double cgi;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "proveedor_predeterminado_id")
    private Proveedor proveedorPredeterminado;
    
    @ManyToOne
    @JoinColumn(name = "modelo_inventario_id", nullable = false)
    private ModeloInventario modeloInventario;
    
    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ArticuloProveedor> listaProveedores;
    
    @OneToMany(mappedBy = "articulo")
    private List<VentaArticulo> ventas;
    
    @OneToMany(mappedBy = "articulo")
    private List<OrdenCompra> ordenesCompra;
    
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
            }
        }
    }
    
    public void calcularTiempoFijo() {
        if (proveedorPredeterminado != null && demanda != null && demanda > 0) {
            ArticuloProveedor apPred = obtenerDatosProveedorPredeterminado();
            if (apPred != null) {
                // Para modelo de tiempo fijo, el intervalo se define manualmente
                // El stock de seguridad sigue siendo definido por el usuario
                // No calculamos inventario máximo automáticamente
            }
        }
    }
    
    public Integer calcularCantidadAPedirTiempoFijo() {
        if (modeloInventario != null && ModeloInventario.INTERVALO_FIJO.equals(modeloInventario.getNombreMetodo())) {
            if (demanda != null && tiempoIntervalo != null) {
                ArticuloProveedor apPred = obtenerDatosProveedorPredeterminado();
                if (apPred != null) {
                    // Demanda durante el período + demanda durante tiempo de entrega + stock seguridad - stock actual
                    double demandaPeriodo = (demanda / 365) * tiempoIntervalo;
                    double demandaEntrega = (demanda / 365) * apPred.getDemoraEntrega();
                    double cantidadNecesaria = demandaPeriodo + demandaEntrega + stockSeguridad - stockActual;
                    return Math.max(0, (int) Math.ceil(cantidadNecesaria));
                }
            }
        }
        return 0;
    }
    
    public void calcularCGI() {
        if (proveedorPredeterminado != null && loteOptimo != null && loteOptimo > 0 && 
            demanda != null && costoAlmacenamiento != null) {
            
            ArticuloProveedor apPred = obtenerDatosProveedorPredeterminado();
            if (apPred != null && apPred.getCostoPedido() != null && apPred.getPrecioUnitario() != null) {
                double costoOrdenar = (demanda / loteOptimo) * apPred.getCostoPedido();
                double costoMantener = (loteOptimo / 2) * costoAlmacenamiento;
                double costoAdquisicion = demanda * apPred.getPrecioUnitario();
                this.cgi = costoOrdenar + costoMantener + costoAdquisicion;
            }
        }
    }
    
    private ArticuloProveedor obtenerDatosProveedorPredeterminado() {
        if (proveedorPredeterminado != null && listaProveedores != null) {
            return listaProveedores.stream()
                .filter(ap -> ap.getProveedor().getCodProveedor().equals(proveedorPredeterminado.getCodProveedor()) 
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
            // Para tiempo fijo, verificar si es momento de revisar inventario
            // Esto se debe verificar externamente basado en el tiempoIntervalo
            return false; // Se maneja en el servicio
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
    
    @Override
    public String toString() {
        return descripcionArticulo != null ? descripcionArticulo : "Artículo " + codArticulo;
    }
}