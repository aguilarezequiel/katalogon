package DAO;

import Entities.Venta;
import Entities.Articulo;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaDAO extends GenericDAO<Venta> {
    List<Venta> findByFecha(LocalDateTime desde, LocalDateTime hasta);
    List<Venta> findByArticulo(Articulo articulo);
}