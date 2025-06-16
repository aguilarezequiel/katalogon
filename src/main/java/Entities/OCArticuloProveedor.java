package Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "oc_articulo_proveedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OCArticuloProveedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "orden_compra_id", nullable = false)
    private OrdenCompra ordenCompra;
    
    @ManyToOne
    @JoinColumn(name = "articulo_proveedor_id", nullable = false)
    private ArticuloProveedor articuloProveedor;
    
    @Column(nullable = false)
    private Integer cantidad;
}