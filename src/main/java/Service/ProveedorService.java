package Service;

import DAO.ProveedorDAO;
import DAO.ArticuloDAO;
import DAO.impl.ProveedorDAOImpl;
import DAO.impl.ArticuloDAOImpl;
import Entities.Proveedor;
import Entities.Articulo;
import Entities.ArticuloProveedor;
import java.time.LocalDateTime;
import java.util.List;

public class ProveedorService {
    
    private ProveedorDAO proveedorDAO;
    private ArticuloDAO articuloDAO;
    
    public ProveedorService() {
        this.proveedorDAO = new ProveedorDAOImpl();
        this.articuloDAO = new ArticuloDAOImpl();
    }
    
    public Proveedor crearProveedor(Proveedor proveedor) throws Exception {
        // Validaciones
        if (proveedor.getNombreProveedor() == null || proveedor.getNombreProveedor().trim().isEmpty()) {
            throw new Exception("El nombre del proveedor es requerido");
        }
        
        if (proveedor.getArticulosProveedor() == null || proveedor.getArticulosProveedor().isEmpty()) {
            throw new Exception("El proveedor debe estar asociado a al menos un artículo");
        }
        
        // Validar datos de artículos provistos
        for (ArticuloProveedor ap : proveedor.getArticulosProveedor()) {
            if (ap.getDemoraEntrega() == null || ap.getDemoraEntrega() < 0) {
                throw new Exception("La demora de entrega debe ser mayor o igual a 0");
            }
            if (ap.getPrecioUnitario() == null || ap.getPrecioUnitario() <= 0) {
                throw new Exception("El precio unitario debe ser mayor a 0");
            }
            if (ap.getCostoPedido() == null || ap.getCostoPedido() <= 0) {
                throw new Exception("El costo de pedido debe ser mayor a 0");
            }
        }
        
        return proveedorDAO.save(proveedor);
    }
    
    public Proveedor actualizarProveedor(Proveedor proveedor) throws Exception {
        Proveedor existente = proveedorDAO.findById(proveedor.getCodProveedor());
        if (existente == null) {
            throw new Exception("El proveedor no existe");
        }
        
        return proveedorDAO.update(proveedor);
    }
    
    public void eliminarProveedor(Integer codProveedor) throws Exception {
        Proveedor proveedor = proveedorDAO.findById(codProveedor);
        if (proveedor == null) {
            throw new Exception("El proveedor no existe");
        }
        
        // Validar que no sea proveedor predeterminado
        if (proveedorDAO.esProveedorPredeterminado(proveedor)) {
            throw new Exception("No se puede eliminar porque es proveedor predeterminado de uno o más artículos");
        }
        
        // Validar que no tenga órdenes activas
        if (proveedorDAO.tieneOrdenCompraActiva(proveedor)) {
            throw new Exception("No se puede eliminar porque tiene órdenes de compra pendientes o en curso");
        }
        
        // Baja lógica
        proveedor.setActivo(false);
        proveedor.setFechaHoraBaja(LocalDateTime.now());
        proveedorDAO.update(proveedor);
    }
    
    public void asociarArticulo(Integer proveedorId, ArticuloProveedor articuloProveedor) throws Exception {
        Proveedor proveedor = proveedorDAO.findById(proveedorId);
        if (proveedor == null) {
            throw new Exception("El proveedor no existe");
        }
        
        // Validar datos del artículo
        if (articuloProveedor.getArticulo() == null) {
            throw new Exception("Debe seleccionar un artículo");
        }
        
        if (articuloProveedor.getDemoraEntrega() == null || articuloProveedor.getDemoraEntrega() < 0) {
            throw new Exception("La demora de entrega debe ser mayor o igual a 0");
        }
        
        if (articuloProveedor.getPrecioUnitario() == null || articuloProveedor.getPrecioUnitario() <= 0) {
            throw new Exception("El precio unitario debe ser mayor a 0");
        }
        
        if (articuloProveedor.getCostoPedido() == null || articuloProveedor.getCostoPedido() <= 0) {
            throw new Exception("El costo de pedido debe ser mayor a 0");
        }
        
        articuloProveedor.setProveedor(proveedor);
        proveedor.getArticulosProveedor().add(articuloProveedor);
        
        proveedorDAO.update(proveedor);
    }
    
    public List<Proveedor> obtenerTodos() {
        return proveedorDAO.findAllActive();
    }
    
    public List<Proveedor> buscarPorNombre(String nombre) {
        return proveedorDAO.findByNombre(nombre);
    }
    
    public List<Articulo> obtenerArticulosPorProveedor(Proveedor proveedor) {
        return proveedorDAO.findArticulosByProveedor(proveedor);
    }
    
    public Proveedor obtenerPorId(Integer id) {
        return proveedorDAO.findById(id);
    }
}