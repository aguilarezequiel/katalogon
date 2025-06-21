package DAO.impl;

import DAO.ProveedorDAO;
import Entities.Proveedor;
import Entities.Articulo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
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
                "SELECT DISTINCT a FROM Articulo a " +
                "WHERE a.id IN (" +
                    "SELECT ap.articulo.codArticulo FROM ArticuloProveedor ap " +
                    "WHERE ap.proveedor = :proveedor " +
                    "AND ap.activo = true " +
                    "AND ap.articulo.activo = true" +
                ")",
                Articulo.class
            );
            query.setParameter("proveedor", proveedor);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Proveedor findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            // CORRECCIÓN: Query simplificado sin múltiples JOIN FETCH
            TypedQuery<Proveedor> query = em.createQuery(
                "SELECT p FROM Proveedor p " +
                "WHERE p.codProveedor = :id",
                Proveedor.class
            );
            query.setParameter("id", id);
            List<Proveedor> resultados = query.getResultList();
            
            if (!resultados.isEmpty()) {
                Proveedor proveedor = resultados.get(0);
                
                // Cargar las asociaciones por separado para evitar el error de Hibernate
                if (proveedor.getArticulosProveedor() != null) {
                    // Forzar la carga lazy de las asociaciones
                    proveedor.getArticulosProveedor().size();
                }
                
                return proveedor;
            }
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Proveedor> findAllActive() {
        EntityManager em = getEntityManager();
        try {
            // CORRECCIÓN: Query simplificado sin JOIN FETCH problemático
            TypedQuery<Proveedor> query = em.createQuery(
                "SELECT p FROM Proveedor p " +
                "WHERE p.activo = true " +
                "ORDER BY p.nombreProveedor",
                Proveedor.class
            );
            List<Proveedor> proveedores = query.getResultList();
            
            // Cargar las asociaciones por separado si es necesario
            for (Proveedor p : proveedores) {
                if (p.getArticulosProveedor() != null) {
                    // Forzar la carga lazy
                    p.getArticulosProveedor().size();
                }
            }
            
            return proveedores;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Proveedor> findAll() {
        EntityManager em = getEntityManager();
        try {
            // CORRECCIÓN: Query simplificado sin JOIN FETCH problemático
            TypedQuery<Proveedor> query = em.createQuery(
                "SELECT p FROM Proveedor p " +
                "ORDER BY p.nombreProveedor",
                Proveedor.class
            );
            List<Proveedor> proveedores = query.getResultList();
            
            // Cargar las asociaciones por separado si es necesario
            for (Proveedor p : proveedores) {
                if (p.getArticulosProveedor() != null) {
                    // Forzar la carga lazy
                    p.getArticulosProveedor().size();
                }
            }
            
            return proveedores;
        } finally {
            em.close();
        }
    }
}