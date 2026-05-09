package darkkitchen;

import dao.InsumoDaoHibernate;
import dao.ProveedorDaoHibernate;
import model.Insumo;
import model.Proveedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import service.InsumoService;
import service.ProveedorService;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AsociarProveedorTest {

    private InsumoService insumoService;
    private ProveedorService proveedorService;
    private InsumoDaoHibernate insumoDao;
    private ProveedorDaoHibernate proveedorDao;

    @BeforeEach
    void setUp() {
        insumoService    = new InsumoService();
        proveedorService = new ProveedorService();
        insumoDao        = new InsumoDaoHibernate();
        proveedorDao     = new ProveedorDaoHibernate();
    }

    @Test
    void escenario1_asociarProveedorNuevo_guardaRelacion() {
        Proveedor p = proveedorService.registrarProveedor(
                new Proveedor("Prov Frutas", "0991000001", "frutas@test.com"));

        Insumo insumo = insumoDao.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Sin insumos de prueba"));

        Insumo resultado = insumoService.asociarProveedor(insumo.getId(), p.getId());

        assertNotNull(resultado.getProveedor());
        assertEquals(p.getId(), resultado.getProveedor().getId());
    }

    @Test
    void escenario2_asociarNuevoProveedorReemplazaAnterior() {
        Proveedor p1 = proveedorService.registrarProveedor(
                new Proveedor("Prov A", "0991000002", "a@test.com"));
        Proveedor p2 = proveedorService.registrarProveedor(
                new Proveedor("Prov B", "0991000003", "b@test.com"));

        Insumo insumo = insumoDao.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Sin insumos de prueba"));

        insumoService.asociarProveedor(insumo.getId(), p1.getId());
        Insumo resultado = insumoService.asociarProveedor(insumo.getId(), p2.getId());

        assertEquals(p2.getId(), resultado.getProveedor().getId());
        assertNotEquals(p1.getId(), resultado.getProveedor().getId());
    }

    @Test
    void escenario3_sinProveedoresRegistrados_lanzaIllegalStateException() {
        InsumoDaoHibernate daoLocal     = new InsumoDaoHibernate();
        ProveedorDaoHibernate provLocal = new ProveedorDaoHibernate();

        daoLocal.findAll().forEach(insumo -> {
            insumo.setProveedor(null);
            daoLocal.update(insumo);
        });

        provLocal.findAll().forEach(provLocal::delete);

        Insumo insumo = daoLocal.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Sin insumos de prueba"));

        InsumoService serviceLocal = new InsumoService();

        assertThrows(IllegalStateException.class,
                () -> serviceLocal.asociarProveedor(insumo.getId(), 999L));
    }

    @Test
    void asociarConInsumoInexistente_lanzaIllegalArgumentException() {
        Proveedor p = proveedorService.registrarProveedor(
                new Proveedor("Prov X", "0991000004", "x@test.com"));

        assertThrows(IllegalArgumentException.class,
                () -> insumoService.asociarProveedor(99999L, p.getId()));
    }
}
