package Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_gestion_inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionGestionInventario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private Integer loteOptimo;
    
    @Column(nullable = false)
    private Integer puntoPedido;
    
    @Column(nullable = false)
    private LocalDateTime fechaHoraBajaConfiguracion;
    
    @Column(nullable = false)
    private Integer tiempoIntervalo;
    
    @Column(nullable = false)
    private LocalDateTime fechoHoraAltaConfiguracion;
    
    @Column(nullable = false)
    private Boolean activo = true;
}