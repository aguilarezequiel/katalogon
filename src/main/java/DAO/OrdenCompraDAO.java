package DAO;

import Entities.OrdenCompra;
import Entities.Articulo;
import Entities.Proveedor;
import java.util.List;

public interface OrdenCompraDAO extends GenericDAO<OrdenCompra> {
    List<OrdenCompra> findByArticulo(Articulo articulo);
    List<OrdenCompra> findByProveedor(Proveedor proveedor);
    List<OrdenCompra> findByEstado(String estado);
    List<OrdenCompra> findPendientesOEnviadas(Articulo articulo);
    boolean existeOrdenActivaParaArticulo(Articulo articulo);
}