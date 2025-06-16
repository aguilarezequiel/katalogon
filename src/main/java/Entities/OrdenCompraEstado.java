package Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "orden_compra_estado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraEstado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "orden_compra_id", nullable = false)
    private OrdenCompra ordenCompra;
    
    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoOrdenCompra estado;
    
    @Column(nullable = false)
    private LocalDateTime fechaHoraInicio;
    
    private LocalDateTime fechaHoraFin;
}