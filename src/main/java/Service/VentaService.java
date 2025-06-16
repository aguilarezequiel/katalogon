package Service;

import DAO.VentaDAO;
import DAO.ArticuloDAO;
import DAO.OrdenCompraDAO;
import DAO.impl.VentaDAOImpl;
import DAO.impl.ArticuloDAOImpl;
import DAO.impl.OrdenCompraDAOImpl;
import Entities.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentaService {
    
    private VentaDAO ventaDAO;
    private ArticuloDAO articuloDAO;
    private OrdenCompraDAO ordenCompraDAO;
    private OrdenCompraService ordenCompraService;
    
    public VentaService() {
        this.ventaDAO = new VentaDAOImpl();
        this.articuloDAO = new ArticuloDAOImpl();
        this.ordenCompraDAO = new OrdenCompraDAOImpl();
        this.ordenCompraService = new OrdenCompraService();
    }
    
    public Venta crearVenta(Venta venta) throws Exception {
        // Validaciones
        if (venta.getDetalleArticulos() == null || venta.getDetalleArticulos().isEmpty()) {
            throw new Exception("La venta debe tener al menos un artículo");
        }
        
        venta.setFechaHoraVenta(LocalDateTime.now());
        
        // Procesar cada artículo de la venta
        for (VentaArticulo detalle : venta.getDetalleArticulos()) {
            Articulo articulo = detalle.getArticulo();
            
            // Validar stock disponible
            if (articulo.getStockActual() < detalle.getCantidadVentaArticulo()) {
                throw new Exception("Stock insuficiente para el artículo: " + articulo.getDescripcionArticulo());
            }
            
            // Actualizar stock
            articulo.setStockActual(articulo.getStockActual() - detalle.getCantidadVentaArticulo());
            
            // Verificar si necesita reposición (Modelo Lote Fijo)
            if (articulo.getModeloInventario().getNombreMetodo().equals(ModeloInventario.LOTE_FIJO)) {
                if (articulo.getStockActual() <= articulo.getPuntoPedido() && 
                    !ordenCompraDAO.existeOrdenActivaParaArticulo(articulo)) {
                    
                    // Generar orden de compra automática
                    generarOrdenCompraAutomatica(articulo);
                }
            }
            
            articuloDAO.update(articulo);
            detalle.setVenta(venta);
        }
        
        return ventaDAO.save(venta);
    }
    
    private void generarOrdenCompraAutomatica(Articulo articulo) throws Exception {
        OrdenCompra ordenCompra = new OrdenCompra();
        ordenCompra.setArticulo(articulo);
        
        // Usar proveedor predeterminado si existe
        if (articulo.getProveedorPredeterminado() != null) {
            ordenCompra.setProveedor(articulo.getProveedorPredeterminado());
        } else {
            // Si no hay proveedor predeterminado, usar el primero disponible
            if (articulo.getListaProveedores() != null && !articulo.getListaProveedores().isEmpty()) {
                ordenCompra.setProveedor(articulo.getListaProveedores().get(0).getProveedor());
            } else {
                throw new Exception("No hay proveedores disponibles para el artículo: " + articulo.getDescripcionArticulo());
            }
        }
        
        // Usar lote óptimo como cantidad
        ordenCompra.setCantidad(articulo.getLoteOptimo().intValue());
        
        ordenCompraService.crearOrdenCompra(ordenCompra);
    }
    
    public List<Venta> obtenerTodas() {
        return ventaDAO.findAll();
    }
    
    public List<Venta> obtenerPorFecha(LocalDateTime desde, LocalDateTime hasta) {
        return ventaDAO.findByFecha(desde, hasta);
    }
    
    public List<Venta> obtenerPorArticulo(Articulo articulo) {
        return ventaDAO.findByArticulo(articulo);
    }
    
    public Venta obtenerPorId(Integer id) {
        return ventaDAO.findById(id);
    }
}