package DAO;

import Entities.Proveedor;
import Entities.Articulo;
import java.util.List;

public interface ProveedorDAO extends GenericDAO<Proveedor> {
    List<Proveedor> findByNombre(String nombre);
    boolean esProveedorPredeterminado(Proveedor proveedor);
    boolean tieneOrdenCompraActiva(Proveedor proveedor);
    List<Articulo> findArticulosByProveedor(Proveedor proveedor);
}