package Service;

import DAO.OrdenCompraDAO;
import DAO.ArticuloDAO;
import DAO.impl.OrdenCompraDAOImpl;
import DAO.impl.ArticuloDAOImpl;
import DAO.impl.GenericDAOImpl;
import Entities.*;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompraService {
    
    private OrdenCompraDAO ordenCompraDAO;
    private ArticuloDAO articuloDAO;
    
    public OrdenCompraService() {
        this.ordenCompraDAO = new OrdenCompraDAOImpl();
        this.articuloDAO = new ArticuloDAOImpl();
    }
    
    public OrdenCompra crearOrdenCompra(OrdenCompra ordenCompra) throws Exception {
        // Validaciones
        if (ordenCompra.getArticulo() == null) {
            throw new Exception("Debe seleccionar un artículo");
        }
        
        if (ordenCompra.getProveedor() == null) {
            throw new Exception("Debe seleccionar un proveedor");
        }
        
        if (ordenCompra.getCantidad() == null || ordenCompra.getCantidad() <= 0) {
            throw new Exception("La cantidad debe ser mayor a 0");
        }
        
        // Verificar si existe otra orden activa
        if (ordenCompraDAO.existeOrdenActivaParaArticulo(ordenCompra.getArticulo())) {
            throw new Exception("Ya existe una orden de compra activa para este artículo");
        }
        
        // Configurar valores iniciales
        ordenCompra.setFechaCreacion(LocalDateTime.now());
        
        // Crear con estado inicial PENDIENTE
        EstadoOrdenCompra estadoPendiente = obtenerEstadoPorNombre(EstadoOrdenCompra.PENDIENTE);
        OrdenCompraEstado estadoInicial = new OrdenCompraEstado();
        estadoInicial.setOrdenCompra(ordenCompra);
        estadoInicial.setEstado(estadoPendiente);
        estadoInicial.setFechaHoraInicio(LocalDateTime.now());
        
        List<OrdenCompraEstado> estados = new ArrayList<>();
        estados.add(estadoInicial);
        ordenCompra.setEstadosHistorico(estados);
        
        return ordenCompraDAO.save(ordenCompra);
    }
    
    public OrdenCompra actualizarOrdenCompra(OrdenCompra ordenCompra) throws Exception {
        OrdenCompra existente = ordenCompraDAO.findById(ordenCompra.getCodOC());
        if (existente == null) {
            throw new Exception("La orden de compra no existe");
        }
        
        // Solo se puede modificar si está en estado PENDIENTE
        if (!existente.puedeModificarse()) {
            throw new Exception("Solo se pueden modificar órdenes en estado PENDIENTE");
        }
        
        return ordenCompraDAO.update(ordenCompra);
    }
    
    public void enviarOrdenCompra(Integer codOC) throws Exception {
        OrdenCompra ordenCompra = ordenCompraDAO.findById(codOC);
        if (ordenCompra == null) {
            throw new Exception("La orden de compra no existe");
        }
        
        if (!ordenCompra.puedeEnviarse()) {
            throw new Exception("La orden solo puede enviarse si está en estado PENDIENTE");
        }
        
        cambiarEstado(ordenCompra, EstadoOrdenCompra.ENVIADA);
        ordenCompra.setFechaEnvio(LocalDateTime.now());
        ordenCompraDAO.update(ordenCompra);
    }
    
    public void finalizarOrdenCompra(Integer codOC) throws Exception {
        OrdenCompra ordenCompra = ordenCompraDAO.findById(codOC);
        if (ordenCompra == null) {
            throw new Exception("La orden de compra no existe");
        }
        
        if (!ordenCompra.puedeFinalizarse()) {
            throw new Exception("La orden solo puede finalizarse si está en estado ENVIADA");
        }
        
        // Actualizar inventario
        Articulo articulo = ordenCompra.getArticulo();
        articulo.setStockActual(articulo.getStockActual() + ordenCompra.getCantidad());
        
        // **NUEVO**: Actualizar fecha de última compra
        articulo.setFechaUltimaCompra(LocalDateTime.now());
        
        // Verificar si con la OC la cantidad no supera el Punto de Pedido
        if (articulo.getModeloInventario().getNombreMetodo().equals(ModeloInventario.LOTE_FIJO) 
            && articulo.getStockActual() < articulo.getPuntoPedido()) {
            throw new Exception("ADVERTENCIA: El stock actual aún no supera el punto de pedido");
        }
        
        articuloDAO.update(articulo);
        
        cambiarEstado(ordenCompra, EstadoOrdenCompra.FINALIZADA);
        ordenCompra.setFechaFinalizacion(LocalDateTime.now());
        ordenCompraDAO.update(ordenCompra);
    }
    
    public void cancelarOrdenCompra(Integer codOC) throws Exception {
        OrdenCompra ordenCompra = ordenCompraDAO.findById(codOC);
        if (ordenCompra == null) {
            throw new Exception("La orden de compra no existe");
        }
        
        if (!ordenCompra.puedeCancelarse()) {
            throw new Exception("Solo se pueden cancelar órdenes en estado PENDIENTE");
        }
        
        cambiarEstado(ordenCompra, EstadoOrdenCompra.CANCELADA);
        ordenCompraDAO.update(ordenCompra);
    }
    
    public List<OrdenCompra> obtenerTodas() {
        return ordenCompraDAO.findAll();
    }
    
    public List<OrdenCompra> obtenerPorEstado(String estado) {
        return ordenCompraDAO.findByEstado(estado);
    }
    
    public List<OrdenCompra> obtenerPorArticulo(Articulo articulo) {
        return ordenCompraDAO.findByArticulo(articulo);
    }
    
    public List<OrdenCompra> obtenerPorProveedor(Proveedor proveedor) {
        return ordenCompraDAO.findByProveedor(proveedor);
    }
    
    public OrdenCompra obtenerPorId(Integer id) {
        return ordenCompraDAO.findById(id);
    }
    
    private void cambiarEstado(OrdenCompra ordenCompra, String nuevoEstado) throws Exception {
        // Cerrar estado actual
        OrdenCompraEstado estadoActual = ordenCompra.getEstadoActual();
        if (estadoActual != null) {
            estadoActual.setFechaHoraFin(LocalDateTime.now());
        }
        
        // Crear nuevo estado
        EstadoOrdenCompra estado = obtenerEstadoPorNombre(nuevoEstado);
        OrdenCompraEstado nuevoEstadoOC = new OrdenCompraEstado();
        nuevoEstadoOC.setOrdenCompra(ordenCompra);
        nuevoEstadoOC.setEstado(estado);
        nuevoEstadoOC.setFechaHoraInicio(LocalDateTime.now());
        
        ordenCompra.getEstadosHistorico().add(nuevoEstadoOC);
    }
    
    private EstadoOrdenCompra obtenerEstadoPorNombre(String nombre) throws Exception {
        EntityManager em = GenericDAOImpl.emf.createEntityManager();
        try {
            EstadoOrdenCompra estado = em.createQuery(
                "SELECT e FROM EstadoOrdenCompra e WHERE e.nombreEstadoOrdenCompra = :nombre",
                EstadoOrdenCompra.class
            ).setParameter("nombre", nombre).getSingleResult();
            return estado;
        } catch (Exception e) {
            throw new Exception("Estado de orden de compra no encontrado: " + nombre);
        } finally {
            em.close();
        }
    }
}