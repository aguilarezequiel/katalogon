package Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ordenes_compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codOC;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    
    private LocalDateTime fechaEnvio;
    
    private LocalDateTime fechaFinalizacion;
    
    // Relaciones - CAMBIO A EAGER PARA EVITAR LAZY INITIALIZATION
    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrdenCompraEstado> estadosHistorico;
    
    @OneToMany(mappedBy = "ordenCompra", fetch = FetchType.LAZY)
    private List<OCArticuloProveedor> detalleArticulos;
    
    // Estado actual
    @Transient
    public OrdenCompraEstado getEstadoActual() {
        if (estadosHistorico == null || estadosHistorico.isEmpty()) {
            return null;
        }
        return estadosHistorico.stream()
            .filter(e -> e.getFechaHoraFin() == null)
            .max((e1, e2) -> e1.getFechaHoraInicio().compareTo(e2.getFechaHoraInicio()))
            .orElse(null);
    }
    
    public boolean puedeModificarse() {
        OrdenCompraEstado estadoActual = getEstadoActual();
        return estadoActual != null && 
               estadoActual.getEstado().getNombreEstadoOrdenCompra().equals("PENDIENTE");
    }
    
    public boolean puedeCancelarse() {
        return puedeModificarse();
    }
    
    public boolean puedeEnviarse() {
        OrdenCompraEstado estadoActual = getEstadoActual();
        return estadoActual != null && 
               estadoActual.getEstado().getNombreEstadoOrdenCompra().equals("PENDIENTE");
    }
    
    public boolean puedeFinalizarse() {
        OrdenCompraEstado estadoActual = getEstadoActual();
        return estadoActual != null && 
               estadoActual.getEstado().getNombreEstadoOrdenCompra().equals("ENVIADA");
    }
}