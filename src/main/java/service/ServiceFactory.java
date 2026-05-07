package service;

import dao.*;

/**
 * Factory centralizada para crear instancias de servicios con sus dependencias.
 * Facilita la inyección de dependencias y mejora la testabilidad.
 */
public class ServiceFactory {

    // DAOs
    public static UsuarioDao createUsuarioDao() {
        return new UsuarioDaoHibernate();
    }

    public static PedidoDao createPedidoDao() {
        return new PedidoDaoHibernate();
    }

    public static MarcaDao createMarcaDao() {
        return new MarcaDaoHibernate();
    }

    public static InsumoDao createInsumoDao() {
        return new InsumoDaoHibernate();
    }

    public static ItemMenuDao createItemMenuDao() {
        return new ItemMenuDaoHibernate();
    }

    public static OrdenDeCompraDao createOrdenDeCompraDao() {
        return new OrdenDeCompraDaoHibernate();
    }

    // Servicios
    public static UsuarioService createUsuarioService() {
        return new UsuarioService(createUsuarioDao());
    }

    public static PedidoService createPedidoService() {
        return new PedidoService(createPedidoDao(), createMarcaDao());
    }

//    public static InsumoService createInsumoService() {
//        return new InsumoService(createInsumoDao(), createOrdenDeCompraDao());
//    }

    public static MenuService createMenuService() {
        return new MenuService(createInsumoDao(), createItemMenuDao(), createMarcaDao());
    }

    // ── Iteración 1 HU1: Gestión de Proveedores ───────────────────────────

    public static dao.ProveedorDao createProveedorDao() {
        return new dao.ProveedorDaoHibernate();
    }

    public static service.ProveedorService createProveedorService() {
        return new service.ProveedorService(createProveedorDao());
    }
}
