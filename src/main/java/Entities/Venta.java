package Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codVenta;
    
    @Column(nullable = false)
    private LocalDateTime fechaHoraVenta;
    
    // Relaciones - CAMBIO A EAGER PARA EVITAR LAZY INITIALIZATION
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<VentaArticulo> detalleArticulos;
    
    // Métodos de negocio
    public Double getTotal() {
        if (detalleArticulos == null) return 0.0;
        
        return detalleArticulos.stream()
            .mapToDouble(va -> va.getCantidadVentaArticulo() * va.getPrecioVenta())
            .sum();
    }
}