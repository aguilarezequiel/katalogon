package Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "modelos_inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModeloInventario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String nombreMetodo;
    
    @Column(length = 200)
    private String descripcion;
    
    // Relaciones
    @OneToMany(mappedBy = "modeloInventario")
    private List<Articulo> articulos;
    
    // Constantes para tipos de modelo
    public static final String LOTE_FIJO = "LOTE_FIJO";
    public static final String INTERVALO_FIJO = "INTERVALO_FIJO";

    @Override
    public String toString() {
        return nombreMetodo != null ? nombreMetodo : "Modelo " + id;
    }
}