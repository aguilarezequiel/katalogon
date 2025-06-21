package Service;

import DAO.VentaDAO;
import DAO.ArticuloDAO;
import DAO.impl.VentaDAOImpl;
import DAO.impl.ArticuloDAOImpl;
import Entities.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentaService {
    
    private VentaDAO ventaDAO;
    private ArticuloDAO articuloDAO;
    
    public VentaService() {
        this.ventaDAO = new VentaDAOImpl();
        this.articuloDAO = new ArticuloDAOImpl();
    }
    
    public Venta crearVenta(Venta venta) throws Exception {
        // Validaciones
        if (venta.getDetalleArticulos() == null || venta.getDetalleArticulos().isEmpty()) {
            throw new Exception("La venta debe tener al menos un artículo");
        }
        
        venta.setFechaHoraVenta(LocalDateTime.now());
        
        // Lista para artículos que alcanzan punto de pedido
        List<String> articulosEnPuntoPedido = new ArrayList<>();
        
        // Procesar cada artículo de la venta
        for (VentaArticulo detalle : venta.getDetalleArticulos()) {
            Articulo articulo = detalle.getArticulo();
            
            // Validar stock disponible
            if (articulo.getStockActual() < detalle.getCantidadVentaArticulo()) {
                throw new Exception("Stock insuficiente para el artículo: " + articulo.getDescripcionArticulo());
            }
            
            // Actualizar stock
            int stockAnterior = articulo.getStockActual();
            articulo.setStockActual(articulo.getStockActual() - detalle.getCantidadVentaArticulo());
            
            // Verificar si alcanza punto de pedido (solo notificar, NO generar orden automática)
            if (articulo.getModeloInventario().getNombreMetodo().equals(ModeloInventario.LOTE_FIJO)) {
                // Verificar si ANTES no estaba en punto de pedido y AHORA sí
                boolean estabaEnPuntoPedido = stockAnterior <= articulo.getPuntoPedido();
                boolean ahoraEnPuntoPedido = articulo.getStockActual() <= articulo.getPuntoPedido();
                
                if (!estabaEnPuntoPedido && ahoraEnPuntoPedido) {
                    articulosEnPuntoPedido.add(articulo.getDescripcionArticulo());
                }
            }
            
            articuloDAO.update(articulo);
            detalle.setVenta(venta);
        }
        
        // Guardar la venta
        Venta ventaGuardada = ventaDAO.save(venta);
        
        // Mostrar advertencia si hay artículos en punto de pedido (sin generar órdenes automáticas)
        if (!articulosEnPuntoPedido.isEmpty()) {
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("⚠️ ADVERTENCIA: Los siguientes artículos han alcanzado su Punto de Pedido:\n\n");
            
            for (String descripcion : articulosEnPuntoPedido) {
                mensaje.append("• ").append(descripcion).append("\n");
            }
            
            mensaje.append("\n📋 Se recomienda revisar el reporte 'Productos a Reponer' para gestionar las órdenes de compra manualmente.");
            
            // Esta excepción especial será capturada en la UI para mostrar la advertencia
            throw new AdvertenciaPuntoPedidoException(mensaje.toString());
        }
        
        return ventaGuardada;
    }

    public static class AdvertenciaPuntoPedidoException extends Exception {
        public AdvertenciaPuntoPedidoException(String message) {
            super(message);
        }
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