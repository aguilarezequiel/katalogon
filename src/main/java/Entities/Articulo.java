package Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

import Entities.*;

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
    private Double inventarioMaximo;
    
    @Column(nullable = false)
    private Double costoMantenimiento;
    
    private LocalDateTime fechaHoraBaja;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    // Campos para modelos de inventario
    @Column(nullable = false)
    private Double loteOptimo;
    
    @Column(nullable = false)
    private Double puntoPedido;
    
    @Column(nullable = false)
    private Double costoAlmacenamiento;
    
    @Column(nullable = false)
    private Double costoPedido;
    
    @Column(nullable = false)
    private Double costoCompra;
    
    // CGI (Costo de Gestión de Inventario)
    @Column(nullable = false)
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
        if (demanda != null && demanda > 0 && costoPedido != null && costoPedido > 0 && 
            costoAlmacenamiento != null && costoAlmacenamiento > 0) {
            // Q* = sqrt(2 * D * S / H)
            this.loteOptimo = Math.sqrt((2 * demanda * costoPedido) / costoAlmacenamiento);
            
            // Punto de pedido = d * L + SS (asumiendo demora de 0 por ahora)
            this.puntoPedido = stockSeguridad != null ? stockSeguridad : 0.0;
        }
    }
    
    public void calcularIntervaloFijo() {
        // Inventario máximo = d * (t + L) + SS
        // Por ahora simplificado
        if (demanda != null && demanda > 0) {
            double ss = stockSeguridad != null ? stockSeguridad : 0.0;
            this.inventarioMaximo = demanda * 30 + ss; // Asumiendo período de 30 días
        }
    }
    
    public void calcularCGI() {
        if (loteOptimo != null && loteOptimo > 0 && demanda != null && costoPedido != null &&
            costoAlmacenamiento != null && costoCompra != null) {
            double costoOrdenar = (demanda / loteOptimo) * costoPedido;
            double costoMantener = (loteOptimo / 2) * costoAlmacenamiento;
            double costoAdquisicion = demanda * costoCompra;
            this.cgi = costoOrdenar + costoMantener + costoAdquisicion;
        }
    }
    
    public boolean necesitaReposicion() {
        if (stockActual == null || puntoPedido == null) return false;
        return stockActual <= puntoPedido && !tieneOrdenPendienteOEnviada();
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