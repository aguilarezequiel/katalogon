package Service;

import DAO.ArticuloDAO;
import DAO.OrdenCompraDAO;
import DAO.impl.ArticuloDAOImpl;
import DAO.impl.OrdenCompraDAOImpl;
import DAO.impl.GenericDAOImpl;
import Entities.Articulo;
import Entities.ModeloInventario;
import Entities.Proveedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

public class ArticuloService {
    
    private ArticuloDAO articuloDAO;
    private OrdenCompraDAO ordenCompraDAO;
    
    public ArticuloService() {
        this.articuloDAO = new ArticuloDAOImpl();
        this.ordenCompraDAO = new OrdenCompraDAOImpl();
    }
    
    public Articulo crearArticulo(Articulo articulo) throws Exception {
        // Validaciones
        if (articulo.getDescripcionArticulo() == null || articulo.getDescripcionArticulo().trim().isEmpty()) {
            throw new Exception("La descripción del artículo es requerida");
        }
        
        if (articulo.getDemanda() == null || articulo.getDemanda() <= 0) {
            throw new Exception("La demanda debe ser mayor a 0");
        }
        
        if (articulo.getCostoAlmacenamiento() == null || articulo.getCostoAlmacenamiento() <= 0) {
            throw new Exception("El costo de almacenamiento debe ser mayor a 0");
        }
        
        if (articulo.getCostoPedido() == null || articulo.getCostoPedido() <= 0) {
            throw new Exception("El costo de pedido debe ser mayor a 0");
        }
        
        if (articulo.getCostoCompra() == null || articulo.getCostoCompra() <= 0) {
            throw new Exception("El costo de compra debe ser mayor a 0");
        }
        
        if (articulo.getModeloInventario() == null) {
            throw new Exception("Debe seleccionar un modelo de inventario");
        }
        
        // NUEVO: Obtener o crear el modelo de inventario en la base de datos
        ModeloInventario modeloPersistente = obtenerOCrearModeloInventario(
            articulo.getModeloInventario().getNombreMetodo()
        );
        articulo.setModeloInventario(modeloPersistente);
        
        // Calcular valores según modelo
        recalcularValoresModelo(articulo);
        
        return articuloDAO.save(articulo);
    }
    
    public Articulo actualizarArticulo(Articulo articulo) throws Exception {
        Articulo existente = articuloDAO.findById(articulo.getCodArticulo());
        if (existente == null) {
            throw new Exception("El artículo no existe");
        }
        
        // NUEVO: Asegurar que el modelo de inventario esté persistido
        if (articulo.getModeloInventario() != null) {
            ModeloInventario modeloPersistente = obtenerOCrearModeloInventario(
                articulo.getModeloInventario().getNombreMetodo()
            );
            articulo.setModeloInventario(modeloPersistente);
        }
        
        // Recalcular valores si cambiaron las variables
        recalcularValoresModelo(articulo);
        
        return articuloDAO.update(articulo);
    }
    
    public void eliminarArticulo(Integer codArticulo) throws Exception {
        Articulo articulo = articuloDAO.findById(codArticulo);
        if (articulo == null) {
            throw new Exception("El artículo no existe");
        }
        
        // Validar que no tenga orden de compra pendiente o enviada
        if (articuloDAO.tieneOrdenCompraActiva(articulo)) {
            throw new Exception("No se puede eliminar el artículo porque tiene órdenes de compra activas");
        }
        
        // Validar que no tenga stock
        if (articuloDAO.tieneStock(articulo)) {
            throw new Exception("No se puede eliminar el artículo porque tiene unidades en stock");
        }
        
        // Baja lógica
        articulo.setActivo(false);
        articulo.setFechaHoraBaja(LocalDateTime.now());
        articuloDAO.update(articulo);
    }
    
    public void ajustarInventario(Integer codArticulo, Integer nuevaCantidad) throws Exception {
        Articulo articulo = articuloDAO.findById(codArticulo);
        if (articulo == null) {
            throw new Exception("El artículo no existe");
        }
        
        if (nuevaCantidad < 0) {
            throw new Exception("La cantidad no puede ser negativa");
        }
        
        articulo.setStockActual(nuevaCantidad);
        articuloDAO.update(articulo);
    }
    
    public List<Articulo> obtenerTodos() {
        return articuloDAO.findAllActive();
    }
    
    public List<Articulo> obtenerArticulosAReponer() {
        return articuloDAO.findArticulosAReponer();
    }
    
    public List<Articulo> obtenerArticulosFaltantes() {
        return articuloDAO.findArticulosFaltantes();
    }
    
    public List<Articulo> buscarPorDescripcion(String descripcion) {
        return articuloDAO.findByDescripcion(descripcion);
    }
    
    public List<Articulo> obtenerPorProveedor(Proveedor proveedor) {
        return articuloDAO.findByProveedor(proveedor);
    }
    
    public Articulo obtenerPorId(Integer id) {
        return articuloDAO.findById(id);
    }
    
    // MÉTODO NUEVO: Obtener o crear modelo de inventario
    private ModeloInventario obtenerOCrearModeloInventario(String nombre) throws Exception {
        EntityManager em = GenericDAOImpl.emf.createEntityManager();
        try {
            // Buscar si ya existe
            TypedQuery<ModeloInventario> query = em.createQuery(
                "SELECT m FROM ModeloInventario m WHERE m.nombreMetodo = :nombre",
                ModeloInventario.class
            );
            query.setParameter("nombre", nombre);
            List<ModeloInventario> resultados = query.getResultList();
            
            if (!resultados.isEmpty()) {
                return resultados.get(0);
            }
            
            // Si no existe, crearlo
            em.getTransaction().begin();
            ModeloInventario nuevo = new ModeloInventario();
            nuevo.setNombreMetodo(nombre);
            nuevo.setDescripcion(getDescripcionModelo(nombre));
            em.persist(nuevo);
            em.getTransaction().commit();
            return nuevo;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Error al obtener/crear modelo de inventario: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    // MÉTODO NUEVO: Obtener descripción del modelo
    private String getDescripcionModelo(String nombre) {
        switch (nombre) {
            case ModeloInventario.LOTE_FIJO:
                return "Modelo de inventario con lote de pedido fijo";
            case ModeloInventario.INTERVALO_FIJO:
                return "Modelo de inventario con intervalo de tiempo fijo";
            default:
                return "Modelo de inventario";
        }
    }
    
    private void recalcularValoresModelo(Articulo articulo) {
        if (articulo.getModeloInventario().getNombreMetodo().equals(ModeloInventario.LOTE_FIJO)) {
            articulo.calcularLoteFijo();
        } else if (articulo.getModeloInventario().getNombreMetodo().equals(ModeloInventario.INTERVALO_FIJO)) {
            articulo.calcularIntervaloFijo();
        }
        articulo.calcularCGI();
    }
}