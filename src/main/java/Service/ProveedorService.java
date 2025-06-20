package Service;

import DAO.ProveedorDAO;
import DAO.ArticuloDAO;
import DAO.impl.ProveedorDAOImpl;
import DAO.impl.ArticuloDAOImpl;
import DAO.impl.GenericDAOImpl;
import Entities.Proveedor;
import Entities.Articulo;
import Entities.ArticuloProveedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

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
        
        // Guardar usando transacción manual para manejar las asociaciones
        EntityManager em = GenericDAOImpl.emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            // Establecer valores por defecto
            proveedor.setActivo(true);
            
            // CREAR UNA NUEVA INSTANCIA sin las asociaciones para evitar problemas
            Proveedor nuevoProveedor = new Proveedor();
            nuevoProveedor.setNombreProveedor(proveedor.getNombreProveedor());
            nuevoProveedor.setActivo(true);
            
            // Guardar el proveedor primero SIN las asociaciones
            em.persist(nuevoProveedor);
            em.flush(); // Forzar el INSERT para obtener el ID
            
            // Procesar las asociaciones con artículos DESPUÉS de persistir el proveedor
            List<ArticuloProveedor> asociacionesGuardadas = new ArrayList<>();
            for (ArticuloProveedor ap : proveedor.getArticulosProveedor()) {
                // CRÍTICO: Recargar el artículo en el contexto actual usando su ID
                Articulo articuloManaged = em.find(Articulo.class, ap.getArticulo().getCodArticulo());
                if (articuloManaged == null) {
                    throw new Exception("Artículo no encontrado: " + ap.getArticulo().getCodArticulo());
                }
                
                // Verificar si ya existe la asociación
                TypedQuery<Long> queryExiste = em.createQuery(
                    "SELECT COUNT(ap) FROM ArticuloProveedor ap " +
                    "WHERE ap.articulo = :articulo AND ap.proveedor = :proveedor AND ap.activo = true",
                    Long.class
                );
                queryExiste.setParameter("articulo", articuloManaged);
                queryExiste.setParameter("proveedor", nuevoProveedor);
                
                if (queryExiste.getSingleResult() > 0) {
                    throw new Exception("Ya existe una asociación activa entre el artículo " + 
                        articuloManaged.getDescripcionArticulo() + " y este proveedor");
                }
                
                // Crear nueva asociación con entidades MANAGED
                ArticuloProveedor nuevaAsociacion = new ArticuloProveedor();
                nuevaAsociacion.setArticulo(articuloManaged);  // Entidad MANAGED
                nuevaAsociacion.setProveedor(nuevoProveedor);  // Entidad MANAGED
                nuevaAsociacion.setPrecioUnitario(ap.getPrecioUnitario());
                nuevaAsociacion.setDemoraEntrega(ap.getDemoraEntrega());
                nuevaAsociacion.setCostoPedido(ap.getCostoPedido());
                nuevaAsociacion.setActivo(true);
                
                em.persist(nuevaAsociacion);
                asociacionesGuardadas.add(nuevaAsociacion);
            }
            
            // Actualizar la lista en el proveedor
            nuevoProveedor.setArticulosProveedor(asociacionesGuardadas);
            
            em.getTransaction().commit();
            return nuevoProveedor;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Error al crear proveedor: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    public Proveedor actualizarProveedor(Proveedor proveedor) throws Exception {
        Proveedor existente = proveedorDAO.findById(proveedor.getCodProveedor());
        if (existente == null) {
            throw new Exception("El proveedor no existe");
        }
        
        // Validaciones básicas
        if (proveedor.getNombreProveedor() == null || proveedor.getNombreProveedor().trim().isEmpty()) {
            throw new Exception("El nombre del proveedor es requerido");
        }
        
        EntityManager em = GenericDAOImpl.emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            // Actualizar datos básicos del proveedor
            Proveedor proveedorManaged = em.find(Proveedor.class, proveedor.getCodProveedor());
            proveedorManaged.setNombreProveedor(proveedor.getNombreProveedor());
            
            // Si hay nuevas asociaciones, procesarlas
            if (proveedor.getArticulosProveedor() != null && !proveedor.getArticulosProveedor().isEmpty()) {
                for (ArticuloProveedor ap : proveedor.getArticulosProveedor()) {
                    if (ap.getId() == null) { // Nueva asociación
                        Articulo articulo = em.find(Articulo.class, ap.getArticulo().getCodArticulo());
                        if (articulo != null) {
                            ArticuloProveedor nuevaAsociacion = new ArticuloProveedor();
                            nuevaAsociacion.setArticulo(articulo);
                            nuevaAsociacion.setProveedor(proveedorManaged);
                            nuevaAsociacion.setPrecioUnitario(ap.getPrecioUnitario());
                            nuevaAsociacion.setDemoraEntrega(ap.getDemoraEntrega());
                            nuevaAsociacion.setCostoPedido(ap.getCostoPedido());
                            nuevaAsociacion.setActivo(true);
                            
                            em.persist(nuevaAsociacion);
                        }
                    }
                }
            }
            
            em.getTransaction().commit();
            
            // Recargar el proveedor con todas sus asociaciones
            return proveedorDAO.findById(proveedor.getCodProveedor());
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Error al actualizar proveedor: " + e.getMessage());
        } finally {
            em.close();
        }
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
        
        EntityManager em = GenericDAOImpl.emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            Proveedor proveedorManaged = em.find(Proveedor.class, proveedorId);
            Articulo articuloManaged = em.find(Articulo.class, articuloProveedor.getArticulo().getCodArticulo());
            
            if (proveedorManaged == null || articuloManaged == null) {
                throw new Exception("Proveedor o artículo no encontrado");
            }
            
            // Crear la nueva asociación
            ArticuloProveedor nuevaAsociacion = new ArticuloProveedor();
            nuevaAsociacion.setArticulo(articuloManaged);
            nuevaAsociacion.setProveedor(proveedorManaged);
            nuevaAsociacion.setPrecioUnitario(articuloProveedor.getPrecioUnitario());
            nuevaAsociacion.setDemoraEntrega(articuloProveedor.getDemoraEntrega());
            nuevaAsociacion.setCostoPedido(articuloProveedor.getCostoPedido());
            nuevaAsociacion.setActivo(true);
            
            em.persist(nuevaAsociacion);
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Error al asociar artículo: " + e.getMessage());
        } finally {
            em.close();
        }
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