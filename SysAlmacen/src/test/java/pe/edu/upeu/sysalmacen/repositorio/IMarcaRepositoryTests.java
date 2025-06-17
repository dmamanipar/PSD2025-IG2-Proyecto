package pe.edu.upeu.sysalmacen.repositorio;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import pe.edu.upeu.sysalmacen.modelo.Marca;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
//@Rollback(false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@ActiveProfiles("test") //Para base de datos real de pruebas
public class IMarcaRepositoryTests {
    @Autowired
    private IMarcaRepository marcaRepository;
    private static Long marcaId;

    @BeforeEach
    public void setUp() {
        Marca marca = new Marca();
        marca.setNombre("Samsung");

        Marca guardada = marcaRepository.save(marca);
        marcaId = guardada.getIdMarca(); // Guardamos el ID para pruebas posteriores
    }

    @Test
    @Order(1)
    public void testGuardarMarca() {
        Marca nuevaMarca = new Marca();
        nuevaMarca.setNombre("LG");
        Marca guardada = marcaRepository.save(nuevaMarca);
        Assertions.assertNotNull(guardada.getIdMarca());
        assertEquals("LG", guardada.getNombre());
    }

    @Test
    @Order(2)
    public void testBuscarPorId() {
        Optional<Marca> marca = marcaRepository.findById(marcaId);
        Assertions.assertTrue(marca.isPresent());
        assertEquals("Samsung", marca.get().getNombre());
    }

    @Test
    @Order(3)
    public void testActualizarMarca() {
        Marca marca = marcaRepository.findById(marcaId).orElseThrow();
        marca.setNombre("Samsung Electronics");
        Marca actualizada = marcaRepository.save(marca);
        assertEquals("Samsung Electronics", actualizada.getNombre());
    }

    @Test
    @Order(4)
    public void testListarMarcas() {
        List<Marca> marcas = marcaRepository.findAll();
        assertFalse(marcas.isEmpty());
        System.out.println("Total marcas registradas: " + marcas.size());
        for (Marca m: marcas){
            System.out.println(m.getNombre()+"\t"+m.getIdMarca());
        }
    }

    @Test
    @Order(5)
    public void testEliminarMarca() {
        marcaRepository.deleteById(marcaId);
        Optional<Marca> eliminada = marcaRepository.findById(marcaId);
        assertFalse(eliminada.isPresent(), "La marca debería haber sido eliminada");
    }


}
