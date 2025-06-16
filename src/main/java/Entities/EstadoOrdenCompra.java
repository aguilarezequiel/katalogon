package Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "estados_orden_compra")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoOrdenCompra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codEstadoOrdenCompra;
    
    @Column(nullable = false, unique = true, length = 50)
    private String nombreEstadoOrdenCompra;
    
    @Column(length = 200)
    private String descripcion;
    
    // Relaciones
    @OneToMany(mappedBy = "estado")
    private List<OrdenCompraEstado> ordenesCompra;
    
    // Constantes para estados
    public static final String PENDIENTE = "PENDIENTE";
    public static final String ENVIADA = "ENVIADA";
    public static final String FINALIZADA = "FINALIZADA";
    public static final String CANCELADA = "CANCELADA";
}