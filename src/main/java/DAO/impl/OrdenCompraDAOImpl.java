package DAO.impl;

import DAO.OrdenCompraDAO;
import Entities.OrdenCompra;
import Entities.Articulo;
import Entities.Proveedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class OrdenCompraDAOImpl extends GenericDAOImpl<OrdenCompra> implements OrdenCompraDAO {
    
    public OrdenCompraDAOImpl() {
        super(OrdenCompra.class);
    }
    
    @Override
    public List<OrdenCompra> findByArticulo(Articulo articulo) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<OrdenCompra> query = em.createQuery(
                "SELECT oc FROM OrdenCompra oc " +
                "WHERE oc.articulo = :articulo " +
                "ORDER BY oc.fechaCreacion DESC",
                OrdenCompra.class
            );
            query.setParameter("articulo", articulo);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<OrdenCompra> findByProveedor(Proveedor proveedor) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<OrdenCompra> query = em.createQuery(
                "SELECT oc FROM OrdenCompra oc " +
                "WHERE oc.proveedor = :proveedor " +
                "ORDER BY oc.fechaCreacion DESC",
                OrdenCompra.class
            );
            query.setParameter("proveedor", proveedor);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<OrdenCompra> findByEstado(String estado) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<OrdenCompra> query = em.createQuery(
                "SELECT DISTINCT oc FROM OrdenCompra oc " +
                "JOIN oc.estadosHistorico oce " +
                "WHERE oce.estado.nombreEstadoOrdenCompra = :estado " +
                "AND oce.fechaHoraFin IS NULL " +
                "ORDER BY oc.fechaCreacion DESC",
                OrdenCompra.class
            );
            query.setParameter("estado", estado);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<OrdenCompra> findPendientesOEnviadas(Articulo articulo) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<OrdenCompra> query = em.createQuery(
                "SELECT DISTINCT oc FROM OrdenCompra oc " +
                "JOIN oc.estadosHistorico oce " +
                "WHERE oc.articulo = :articulo " +
                "AND oce.estado.nombreEstadoOrdenCompra IN ('PENDIENTE', 'ENVIADA') " +
                "AND oce.fechaHoraFin IS NULL",
                OrdenCompra.class
            );
            query.setParameter("articulo", articulo);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean existeOrdenActivaParaArticulo(Articulo articulo) {
        return !findPendientesOEnviadas(articulo).isEmpty();
    }
}