package pe.edu.upeu.sysalmacen.servicio;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.sysalmacen.excepciones.CustomResponse;
import pe.edu.upeu.sysalmacen.excepciones.ModelNotFoundException;
import pe.edu.upeu.sysalmacen.modelo.Marca;
import pe.edu.upeu.sysalmacen.repositorio.IMarcaRepository;
import pe.edu.upeu.sysalmacen.servicio.impl.MarcaServiceImp;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MarcaServiceTests {
    @Mock
    private IMarcaRepository repo;

    @InjectMocks
    private MarcaServiceImp marcaService;

    Marca marca;

    @BeforeEach
    public void setUp() {
        marca = Marca.builder()
                .idMarca(1L)
                .nombre("Puma")
                .build();
    }

    @Order(1)
    @DisplayName("GuardarMarca")
    @Test
    public void testSaveMarca() {
        //given
        given(repo.save(marca)).willReturn(marca);
        //when
        Marca ppx=marcaService.save(marca);
        //then
        assertThat(ppx.getNombre()).isNotNull();
        assertThat(ppx.getNombre()).isEqualTo(marca.getNombre());
    }

    @Order(2)
    @DisplayName("Listar Marca")
    @Test
    public void testListMarca() {
        //given
        Marca p = Marca.builder()
                .idMarca(2L)
                .nombre("Adidas")
                .build();
        given(repo.findAll()).willReturn(List.of(marca, p));
        //when
        List<Marca> pl = marcaService.findAll();
        for (Marca pr : pl) {
            System.out.println(pr.getNombre());
        }
        //then
        assertThat(pl).hasSize(2);
        assertThat(pl.get(0)).isEqualTo(marca);
        assertThat(pl.size()).isEqualTo(2);
    }

    @Order(3)
    @DisplayName("Actualizar Marca")
    @Test
    public void testUpdateMarca() {
        //given
        given(repo.save(marca)).willReturn(marca);
        given(repo.findById(1L)).willReturn(Optional.of(marca));
        //when
        marca.setNombre("Nike");
        Marca pa=marcaService.update(marca.getIdMarca(),marca);
        //then
        System.out.println(pa.getNombre());
        assertThat(pa.getNombre()).isEqualTo("Nike");
    }
    @Order(4)
    @DisplayName("Eliminar Marca")
    @Test
    public void testDeletePeriodo() {
        //given
        given(repo.findById(1L)).willReturn(Optional.of(marca));
        //when
        CustomResponse pd=marcaService.delete(1L);
        //then
        System.out.println(pd.getMessage());
        assertThat(pd.getMessage()).isEqualTo("true");
    }

    @Test
    void testDeleteByIdNonExistent() {
        //given
        Long idInexistente = 99L;
        given(repo.findById(idInexistente)).willReturn(Optional.empty());
        //when and then
        assertThatThrownBy(() ->
                        marcaService.delete(idInexistente))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessageContaining("ID NOT FOUND: "+idInexistente);
    }

}
