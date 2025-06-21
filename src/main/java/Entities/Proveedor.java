package Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "proveedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codProveedor;
    
    @Column(nullable = false, length = 100)
    private String nombreProveedor;
    
    private LocalDateTime fechaHoraBaja;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    // Relaciones - CAMBIO A LAZY PARA EVITAR PROBLEMAS DE RENDIMIENTO
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ArticuloProveedor> articulosProveedor;
    
    @OneToMany(mappedBy = "proveedorPredeterminado", fetch = FetchType.LAZY)
    private List<Articulo> articulosPredeterminados;
    
    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY)
    private List<OrdenCompra> ordenesCompra;
    
    // Validaciones
    public boolean puedeSerEliminado() {
        // No puede ser eliminado si es proveedor predeterminado
        if (articulosPredeterminados != null && !articulosPredeterminados.isEmpty()) {
            return false;
        }
        
        // No puede ser eliminado si tiene órdenes pendientes o enviadas
        if (ordenesCompra != null) {
            return !ordenesCompra.stream()
                .anyMatch(oc -> oc.getEstadoActual() != null && 
                    (oc.getEstadoActual().getEstado().getNombreEstadoOrdenCompra().equals("PENDIENTE") ||
                     oc.getEstadoActual().getEstado().getNombreEstadoOrdenCompra().equals("ENVIADA")));
        }
        
        return true;
    }

    @Override
    public String toString() {
        return nombreProveedor != null ? nombreProveedor : "Proveedor " + codProveedor;
    }
}