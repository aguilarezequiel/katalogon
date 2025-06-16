package Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "venta_articulo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaArticulo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;
    
    @ManyToOne
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;
    
    @Column(nullable = false)
    private Integer cantidadVentaArticulo;
    
    @Column(nullable = false)
    private Double precioVenta;
}