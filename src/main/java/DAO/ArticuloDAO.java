package DAO;

import Entities.Articulo;
import Entities.Proveedor;
import java.util.List;

public interface ArticuloDAO extends GenericDAO<Articulo> {
    List<Articulo> findByProveedor(Proveedor proveedor);
    List<Articulo> findArticulosAReponer();
    List<Articulo> findArticulosFaltantes();
    List<Articulo> findByDescripcion(String descripcion);
    boolean tieneOrdenCompraActiva(Articulo articulo);
    boolean tieneStock(Articulo articulo);
}