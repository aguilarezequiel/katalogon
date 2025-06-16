package DAO.impl;

import DAO.ProveedorDAO;
import Entities.Proveedor;
import Entities.Articulo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ProveedorDAOImpl extends GenericDAOImpl<Proveedor> implements ProveedorDAO {
    
    public ProveedorDAOImpl() {
        super(Proveedor.class);
    }
    
    @Override
    public List<Proveedor> findByNombre(String nombre) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Proveedor> query = em.createQuery(
                "SELECT p FROM Proveedor p " +
                "WHERE p.activo = true " +
                "AND LOWER(p.nombreProveedor) LIKE LOWER(:nombre)",
                Proveedor.class
            );
            query.setParameter("nombre", "%" + nombre + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean esProveedorPredeterminado(Proveedor proveedor) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM Articulo a " +
                "WHERE a.proveedorPredeterminado = :proveedor " +
                "AND a.activo = true",
                Long.class
            );
            query.setParameter("proveedor", proveedor);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean tieneOrdenCompraActiva(Proveedor proveedor) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(oc) FROM OrdenCompra oc " +
                "JOIN oc.estadosHistorico oce " +
                "WHERE oc.proveedor = :proveedor " +
                "AND oce.estado.nombreEstadoOrdenCompra IN ('PENDIENTE', 'ENVIADA') " +
                "AND oce.fechaHoraFin IS NULL",
                Long.class
            );
            query.setParameter("proveedor", proveedor);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Articulo> findArticulosByProveedor(Proveedor proveedor) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Articulo> query = em.createQuery(
                "SELECT DISTINCT ap.articulo FROM ArticuloProveedor ap " +
                "WHERE ap.proveedor = :proveedor " +
                "AND ap.activo = true " +
                "AND ap.articulo.activo = true",
                Articulo.class
            );
            query.setParameter("proveedor", proveedor);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}