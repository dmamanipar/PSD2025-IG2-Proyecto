package pe.edu.upeu.sysalmacen.control;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pe.edu.upeu.sysalmacen.dtos.MarcaDTO;
import pe.edu.upeu.sysalmacen.excepciones.CustomResponse;
import pe.edu.upeu.sysalmacen.mappers.MarcaMapper;
import pe.edu.upeu.sysalmacen.modelo.Marca;
import pe.edu.upeu.sysalmacen.servicio.IMarcaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@ExtendWith(MockitoExtension.class)
public class MarcaControllerTests {
    @Mock
    private IMarcaService marcaService;
    @Mock
    private MarcaMapper marcaMapper;
    @InjectMocks
    private MarcaController marcaController;
    private Marca marca;
    private MarcaDTO marcaDTO;
    private static final Logger logger = Logger.getLogger(MarcaControllerTests.class.getName());
    List<Marca> marcas;


    @BeforeEach
    void setUp() {
        marca = Marca.builder()
                .idMarca(1L)
                .nombre("Marca A")
                .build();
        marcaDTO = MarcaDTO.builder()
                .idMarca(1L)
                .nombre("Marca A")
                .build();
        marcas=List.of(marca);
    }

    @Test
    public void testFindAll_ReturnsListOfMarcaDTO_WithHttpStatusOK() {
        //given
        BDDMockito.given(marcaService.findAll()).willReturn(marcas);

        BDDMockito.given(marcaMapper.toDTOs(marcas)).willReturn(List.of(marcaDTO)
        );
        //when
        ResponseEntity<List<MarcaDTO>> lp=marcaController.findAll();
        //then
        Assertions.assertEquals(HttpStatus.OK, lp.getStatusCode());
        Assertions.assertNotNull(lp.getBody());
        Assertions.assertEquals(1, lp.getBody().size());
        Assertions.assertEquals(List.of(marcaDTO), lp.getBody());
        for (MarcaDTO p : lp.getBody()) {
            logger.info(String.format("MarcaDTO{id=%d, nombre='%s'}",
                    p.getIdMarca(), p.getNombre()));
        }
        BDDMockito.then(marcaService).should().findAll();
        BDDMockito.then(marcaMapper).should().toDTOs(marcas);
    }

    @Test
    void testFindById_ReturnsMarcaDTO_WithHttpStatusOK() {
        Long id = 1L;
        BDDMockito.given(marcaService.findById(id)).willReturn(marca);
        BDDMockito.given(marcaMapper.toDTO(marca)).willReturn(marcaDTO);
        ResponseEntity<MarcaDTO> response = marcaController.findById(id);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(marcaDTO, response.getBody());
        BDDMockito.then(marcaService).should().findById(id);
        BDDMockito.then(marcaMapper).should().toDTO(marca);
    }
    @Test
    void testSave_ReturnsCreatedStatusAndLocationHeader() {
        BDDMockito.given(marcaMapper.toEntity(marcaDTO)).willReturn(marca);
        BDDMockito.given(marcaService.save(marca)).willReturn(marca);
        ResponseEntity<CustomResponse> response =
                marcaController.save(marcaDTO);
        Assertions.assertEquals(200, response.getStatusCodeValue());

        Assertions.assertTrue(response.getBody().getMessage().equals("true"));
        Assertions.assertTrue(response.getBody().getDetails().contains("1"));
    }
    @Test
    void testUpdate_ReturnsUpdatedMarcaDTO_WithHttpStatusOK() {
        Long id = 1L;
        marcaDTO.setIdMarca(id);
        BDDMockito.given(marcaMapper.toEntity(marcaDTO)).willReturn(marca);
        BDDMockito.given(marcaService.update(id, marca)).willReturn(marca);
        BDDMockito.given(marcaMapper.toDTO(marca)).willReturn(marcaDTO);
        ResponseEntity<MarcaDTO> response = marcaController.update(id,
                marcaDTO);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(marcaDTO, response.getBody());
        BDDMockito.then(marcaMapper).should().toEntity(marcaDTO);
        BDDMockito.then(marcaService).should().update(id, marca);
        BDDMockito.then(marcaMapper).should().toDTO(marca);
    }
    @Test
    void testDelete_ReturnsCustomResponse_WithHttpStatusOK() {
        Long id = 1L;
        CustomResponse customResponse = new
                CustomResponse(200, LocalDateTime.now(),
                "true", "Todo Ok");
        BDDMockito.given(marcaService.delete(id)).willReturn(customResponse);
        ResponseEntity<CustomResponse> response = marcaController.delete(id);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(customResponse, response.getBody());
        BDDMockito.then(marcaService).should().delete(id);
    }
}
