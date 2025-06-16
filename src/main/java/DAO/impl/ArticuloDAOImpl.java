package DAO.impl;

import DAO.ArticuloDAO;
import Entities.Articulo;
import Entities.Proveedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ArticuloDAOImpl extends GenericDAOImpl<Articulo> implements ArticuloDAO {
    
    public ArticuloDAOImpl() {
        super(Articulo.class);
    }
    
    @Override
    public List<Articulo> findByProveedor(Proveedor proveedor) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Articulo> query = em.createQuery(
                "SELECT DISTINCT a FROM Articulo a " +
                "JOIN a.listaProveedores ap " +
                "WHERE ap.proveedor = :proveedor AND a.activo = true",
                Articulo.class
            );
            query.setParameter("proveedor", proveedor);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Articulo> findArticulosAReponer() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Articulo> query = em.createQuery(
                "SELECT a FROM Articulo a " +
                "WHERE a.activo = true " +
                "AND a.stockActual <= a.puntoPedido " +
                "AND NOT EXISTS (" +
                "    SELECT oc FROM OrdenCompra oc " +
                "    JOIN oc.estadosHistorico oce " +
                "    WHERE oc.articulo = a " +
                "    AND oce.estado.nombreEstadoOrdenCompra IN ('PENDIENTE', 'ENVIADA')" +
                "    AND oce.fechaHoraFin IS NULL" +
                ")",
                Articulo.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Articulo> findArticulosFaltantes() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Articulo> query = em.createQuery(
                "SELECT a FROM Articulo a " +
                "WHERE a.activo = true " +
                "AND a.stockActual <= a.stockSeguridad",
                Articulo.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Articulo> findByDescripcion(String descripcion) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Articulo> query = em.createQuery(
                "SELECT a FROM Articulo a " +
                "WHERE a.activo = true " +
                "AND LOWER(a.descripcionArticulo) LIKE LOWER(:descripcion)",
                Articulo.class
            );
            query.setParameter("descripcion", "%" + descripcion + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean tieneOrdenCompraActiva(Articulo articulo) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(oc) FROM OrdenCompra oc " +
                "JOIN oc.estadosHistorico oce " +
                "WHERE oc.articulo = :articulo " +
                "AND oce.estado.nombreEstadoOrdenCompra IN ('PENDIENTE', 'ENVIADA') " +
                "AND oce.fechaHoraFin IS NULL",
                Long.class
            );
            query.setParameter("articulo", articulo);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean tieneStock(Articulo articulo) {
        return articulo.getStockActual() > 0;
    }
}