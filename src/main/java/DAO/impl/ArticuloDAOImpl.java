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
                "JOIN FETCH a.listaProveedores ap " +
                "JOIN FETCH a.modeloInventario " +
                "LEFT JOIN FETCH a.proveedorPredeterminado " +
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
            // Query mejorada para incluir tanto modelo lote fijo como tiempo fijo
            TypedQuery<Articulo> query = em.createQuery(
                "SELECT DISTINCT a FROM Articulo a " +
                "JOIN FETCH a.modeloInventario " +
                "LEFT JOIN FETCH a.proveedorPredeterminado " +
                "LEFT JOIN FETCH a.listaProveedores ap " +
                "LEFT JOIN FETCH ap.proveedor " +
                "WHERE a.activo = true " +
                "AND (" +
                    // Modelo Lote Fijo: stock actual <= punto pedido y sin orden activa
                    "(a.modeloInventario.nombreMetodo = 'LOTE_FIJO' " +
                    "AND a.stockActual <= a.puntoPedido " +
                    "AND NOT EXISTS (" +
                        "SELECT oc2 FROM OrdenCompra oc2 " +
                        "JOIN oc2.estadosHistorico oce " +
                        "WHERE oc2.articulo = a " +
                        "AND oce.estado.nombreEstadoOrdenCompra IN ('PENDIENTE', 'ENVIADA') " +
                        "AND oce.fechaHoraFin IS NULL" +
                    ")) " +
                    "OR " +
                    // Modelo Tiempo Fijo: artículos que tienen intervalo definido Y sin orden activa
                    "(a.modeloInventario.nombreMetodo = 'INTERVALO_FIJO' " +
                    "AND a.tiempoIntervalo IS NOT NULL " +
                    "AND a.tiempoIntervalo > 0 " +
                    "AND NOT EXISTS (" +
                        "SELECT oc3 FROM OrdenCompra oc3 " +
                        "JOIN oc3.estadosHistorico oce2 " +
                        "WHERE oc3.articulo = a " +
                        "AND oce2.estado.nombreEstadoOrdenCompra IN ('PENDIENTE', 'ENVIADA') " +
                        "AND oce2.fechaHoraFin IS NULL" +
                    "))" +
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
                "JOIN FETCH a.modeloInventario " +
                "LEFT JOIN FETCH a.proveedorPredeterminado " +
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
                "JOIN FETCH a.modeloInventario " +
                "LEFT JOIN FETCH a.proveedorPredeterminado " +
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
    
    @Override
    public Articulo findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Articulo> query = em.createQuery(
                "SELECT a FROM Articulo a " +
                "JOIN FETCH a.modeloInventario " +
                "LEFT JOIN FETCH a.proveedorPredeterminado " +
                "LEFT JOIN FETCH a.listaProveedores ap " +
                "LEFT JOIN FETCH ap.proveedor " +
                "WHERE a.codArticulo = :id",
                Articulo.class
            );
            query.setParameter("id", id);
            List<Articulo> resultados = query.getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Articulo> findAllActive() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Articulo> query = em.createQuery(
                "SELECT DISTINCT a FROM Articulo a " +
                "JOIN FETCH a.modeloInventario " +
                "LEFT JOIN FETCH a.proveedorPredeterminado " +
                "LEFT JOIN FETCH a.listaProveedores ap " +
                "LEFT JOIN FETCH ap.proveedor " +
                "WHERE a.activo = true " +
                "ORDER BY a.descripcionArticulo",
                Articulo.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}