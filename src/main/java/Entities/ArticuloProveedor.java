package Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "articulo_proveedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloProveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;
    
    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;
    
    @Column(nullable = false)
    private Integer demoraEntrega;
    
    @Column(nullable = false)
    private Double precioUnitario;
    
    private LocalDateTime fechaHoraBaja;
    
    @Column(nullable = false)
    private Double costoPedido;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    // Relaciones
    @OneToMany(mappedBy = "articuloProveedor")
    private List<OCArticuloProveedor> ordenesCompra;
    
    // MÉTODO toString() para evitar LazyInitializationException
    @Override
    public String toString() {
        return (articulo != null ? articulo.getDescripcionArticulo() : "Artículo") + 
               " - " + (proveedor != null ? proveedor.getNombreProveedor() : "Proveedor");
    }
}