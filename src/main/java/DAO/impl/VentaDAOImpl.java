package DAO.impl;

import DAO.VentaDAO;
import Entities.Venta;
import Entities.Articulo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

public class VentaDAOImpl extends GenericDAOImpl<Venta> implements VentaDAO {
    
    public VentaDAOImpl() {
        super(Venta.class);
    }
    
    @Override
    public List<Venta> findByFecha(LocalDateTime desde, LocalDateTime hasta) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Venta> query = em.createQuery(
                "SELECT v FROM Venta v " +
                "WHERE v.fechaHoraVenta BETWEEN :desde AND :hasta " +
                "ORDER BY v.fechaHoraVenta DESC",
                Venta.class
            );
            query.setParameter("desde", desde);
            query.setParameter("hasta", hasta);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Venta> findByArticulo(Articulo articulo) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Venta> query = em.createQuery(
                "SELECT DISTINCT v FROM Venta v " +
                "JOIN v.detalleArticulos va " +
                "WHERE va.articulo = :articulo " +
                "ORDER BY v.fechaHoraVenta DESC",
                Venta.class
            );
            query.setParameter("articulo", articulo);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}