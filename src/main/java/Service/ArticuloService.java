package Service;

import DAO.ArticuloDAO;
import DAO.OrdenCompraDAO;
import DAO.ProveedorDAO;
import DAO.impl.ArticuloDAOImpl;
import DAO.impl.OrdenCompraDAOImpl;
import DAO.impl.ProveedorDAOImpl;
import DAO.impl.GenericDAOImpl;
import Entities.Articulo;
import Entities.ModeloInventario;
import Entities.Proveedor;
import Entities.ArticuloProveedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

public class ArticuloService {
    
    private ArticuloDAO articuloDAO;
    private OrdenCompraDAO ordenCompraDAO;
    private ProveedorDAO proveedorDAO;
    
    public ArticuloService() {
        this.articuloDAO = new ArticuloDAOImpl();
        this.ordenCompraDAO = new OrdenCompraDAOImpl();
        this.proveedorDAO = new ProveedorDAOImpl();
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
        
        if (articulo.getStockSeguridad() == null || articulo.getStockSeguridad() < 0) {
            throw new Exception("El stock de seguridad debe ser mayor o igual a 0");
        }
        
        if (articulo.getModeloInventario() == null) {
            throw new Exception("Debe seleccionar un modelo de inventario");
        }
        
        // Obtener o crear el modelo de inventario en la base de datos
        ModeloInventario modeloPersistente = obtenerOCrearModeloInventario(
            articulo.getModeloInventario().getNombreMetodo()
        );
        articulo.setModeloInventario(modeloPersistente);
        
        // IMPORTANTE: No asignar proveedor predeterminado en creación
        // Se manejará después del guardado para evitar problemas con entidades transitorias
        articulo.setProveedorPredeterminado(null);
        
        // Calcular valores según modelo
        recalcularValoresModelo(articulo);
        
        // Guardar el artículo
        return articuloDAO.save(articulo);
    }
    
    public Articulo actualizarArticulo(Articulo articulo) throws Exception {
        Articulo existente = articuloDAO.findById(articulo.getCodArticulo());
        if (existente == null) {
            throw new Exception("El artículo no existe");
        }
        
        // Asegurar que el modelo de inventario esté persistido
        if (articulo.getModeloInventario() != null) {
            ModeloInventario modeloPersistente = obtenerOCrearModeloInventario(
                articulo.getModeloInventario().getNombreMetodo()
            );
            articulo.setModeloInventario(modeloPersistente);
        }
        
        // Validar proveedor predeterminado si se especifica
        if (articulo.getProveedorPredeterminado() != null) {
            validarProveedorPredeterminado(articulo);
        }
        
        // Recalcular valores si cambiaron las variables
        recalcularValoresModelo(articulo);
        
        return articuloDAO.update(articulo);
    }
    
    private void validarProveedorPredeterminado(Articulo articulo) throws Exception {
        if (articulo.getListaProveedores() == null || articulo.getListaProveedores().isEmpty()) {
            throw new Exception("No se puede asignar un proveedor predeterminado. " +
                "El artículo no tiene proveedores asociados.");
        }
        
        boolean proveedorEncontrado = articulo.getListaProveedores().stream()
            .anyMatch(ap -> ap.getProveedor().getCodProveedor()
                .equals(articulo.getProveedorPredeterminado().getCodProveedor()) && ap.getActivo());
        
        if (!proveedorEncontrado) {
            throw new Exception("El proveedor seleccionado como predeterminado no provee este artículo. " +
                "Debe seleccionar un proveedor que esté asociado al artículo.");
        }
    }
    
    public ArticuloProveedor crearAsociacionArticuloProveedor(Articulo articulo, Proveedor proveedor, 
            Double precioUnitario, Integer demoraEntrega, Double costoPedido) throws Exception {
        
        // Validaciones
        if (articulo == null || articulo.getCodArticulo() == null) {
            throw new Exception("El artículo debe estar guardado antes de crear asociaciones");
        }
        
        if (proveedor == null || proveedor.getCodProveedor() == null) {
            throw new Exception("El proveedor debe estar guardado antes de crear asociaciones");
        }
        
        if (precioUnitario == null || precioUnitario <= 0) {
            throw new Exception("El precio unitario debe ser mayor a 0");
        }
        
        if (demoraEntrega == null || demoraEntrega < 0) {
            throw new Exception("La demora de entrega debe ser mayor o igual a 0");
        }
        
        if (costoPedido == null || costoPedido <= 0) {
            throw new Exception("El costo de pedido debe ser mayor a 0");
        }
        
        EntityManager em = GenericDAOImpl.emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            // Recargar las entidades en el contexto actual
            Articulo articuloManaged = em.find(Articulo.class, articulo.getCodArticulo());
            Proveedor proveedorManaged = em.find(Proveedor.class, proveedor.getCodProveedor());
            
            if (articuloManaged == null) {
                throw new Exception("Artículo no encontrado");
            }
            if (proveedorManaged == null) {
                throw new Exception("Proveedor no encontrado");
            }
            
            // Verificar si ya existe la asociación
            TypedQuery<Long> queryExiste = em.createQuery(
                "SELECT COUNT(ap) FROM ArticuloProveedor ap " +
                "WHERE ap.articulo = :articulo AND ap.proveedor = :proveedor AND ap.activo = true",
                Long.class
            );
            queryExiste.setParameter("articulo", articuloManaged);
            queryExiste.setParameter("proveedor", proveedorManaged);
            
            if (queryExiste.getSingleResult() > 0) {
                throw new Exception("Ya existe una asociación activa entre este artículo y proveedor");
            }
            
            // Crear la asociación
            ArticuloProveedor ap = new ArticuloProveedor();
            ap.setArticulo(articuloManaged);
            ap.setProveedor(proveedorManaged);
            ap.setPrecioUnitario(precioUnitario);
            ap.setDemoraEntrega(demoraEntrega);
            ap.setCostoPedido(costoPedido);
            ap.setActivo(true);
            
            em.persist(ap);
            em.getTransaction().commit();
            
            // Recargar el artículo para actualizar su lista de proveedores
            em.refresh(articuloManaged);
            
            return ap;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Error al crear la asociación artículo-proveedor: " + e.getMessage());
        } finally {
            em.close();
        }
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
    
    // CORRECCIÓN: Método obtenerPorId que carga todas las relaciones
    public Articulo obtenerPorId(Integer id) {
        EntityManager em = GenericDAOImpl.emf.createEntityManager();
        try {
            // Query simplificada sin múltiples colecciones
            TypedQuery<Articulo> query = em.createQuery(
                "SELECT DISTINCT a FROM Articulo a " +
                "JOIN FETCH a.modeloInventario " +
                "LEFT JOIN FETCH a.proveedorPredeterminado " +
                "LEFT JOIN FETCH a.listaProveedores ap " +
                "LEFT JOIN FETCH ap.proveedor " +
                "WHERE a.codArticulo = :id AND a.activo = true",
                Articulo.class
            );
            query.setParameter("id", id);
            List<Articulo> resultados = query.getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } finally {
            em.close();
        }
    }
    
    public void recalcularArticulo(Integer codArticulo) throws Exception {
        Articulo articulo = articuloDAO.findById(codArticulo);
        if (articulo == null) {
            throw new Exception("El artículo no existe");
        }
        
        recalcularValoresModelo(articulo);
        articuloDAO.update(articulo);
    }
    
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
        try {
            if (articulo.getModeloInventario() != null) {
                String modelo = articulo.getModeloInventario().getNombreMetodo();
                
                if (ModeloInventario.LOTE_FIJO.equals(modelo)) {
                    articulo.calcularLoteFijo();
                } else if (ModeloInventario.INTERVALO_FIJO.equals(modelo)) {
                    articulo.calcularTiempoFijo();
                }
                
                // Siempre calcular CGI
                articulo.calcularCGI();
            }
        } catch (Exception e) {
            System.out.println("Error al recalcular valores del modelo: " + e.getMessage());
            // Asignar valores por defecto en caso de error
            articulo.setLoteOptimo(0.0);
            articulo.setPuntoPedido(articulo.getStockSeguridad() != null ? articulo.getStockSeguridad() : 0.0);
            articulo.setCgi(0.0);
        }
    }
}