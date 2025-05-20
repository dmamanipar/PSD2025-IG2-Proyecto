package pe.edu.upeu.sysalmacen.control;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upeu.sysalmacen.dtos.UsuarioDTO;
import pe.edu.upeu.sysalmacen.modelo.Marca;
import java.util.logging.Level;
import java.util.logging.Logger;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) //Permite que el BeforeEach se ejecute una sola vez
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment =SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class MarcaControllerWebTestClientTest {
    @LocalServerPort
    private int port;
    @Autowired
    private WebTestClient webTestClient;
    private String token;
    Logger logger = Logger.getLogger(MarcaControllerWebTestClientTest.class.getName());
    Marca marca;
    Long idx;

    @BeforeEach
    public void setUp() {
        System.out.println("Puerto x:" + this.port);
        UsuarioDTO.UsuarioCrearDto udto = new UsuarioDTO.UsuarioCrearDto("eliasmp@upeu.edu.pe",
                "Da123456*".toCharArray(), "ADMIN", "Activo");
        try {
            var dd = webTestClient.post()
                    .uri("/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new
                            UsuarioDTO.CredencialesDto("eliasmp@upeu.edu.pe",
                            "Da123456*".toCharArray()))//.toCharArray()
                    .exchange()
                    .expectBody(String.class)
                    .returnResult()
                    .getResponseBody();
            JSONObject jsonObj = new JSONObject(dd);
            if (jsonObj.length() > 1) {
                token =jsonObj.getString("token")!=null?jsonObj.getString("token"):null;
            }
        } catch (JSONException e) {
            System.out.println("saliooooo:" + e.getMessage());
            if (token == null) {
                webTestClient.post()
                        .uri("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(udto)//.toCharArray()
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(String.class)
                        .value(tokenx -> {
                            try {
                                JSONObject jsonObjx = new JSONObject(tokenx);
                                if (jsonObjx.length() > 1) {
                                    token = jsonObjx.getString("token");
                                }
                            } catch (JSONException ex) { //JSONException|
                                logger.log(Level.SEVERE, null, ex);
                            }
                        });
            }
        }
    }


    @Test
    @Order(1)
    public void testListarMarca() {
        System.out.println("ddd:" + token);
        webTestClient.get().uri("http://localhost:" + this.port + "/marcas")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].nombre").isEqualTo("Puma")
                .jsonPath("$").isArray();
        //.jsonPath("$").value(Matchers.hasSize(5));
    }

    @Transactional
    @Test
    @Order(2)
    public void testGuardarMarca() {
        marca = Marca.builder().nombre("AdidasX").build();
        try {
            var datoBuscado = webTestClient.post().uri("http://localhost:" +
                            this.port + "/marcas")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(marca)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .returnResult()
                    .getResponseBody();
            JSONObject jsonObj = new JSONObject(datoBuscado);
            if (jsonObj.length() > 1) {
                idx = Long.parseLong(jsonObj.getString("id"));
            }
        } catch (JSONException e) {
            System.out.println("Error:" + e);
        }
        System.out.println("DATO:" + idx);
    }

    @Transactional
    @Test
    @Order(3)
    public void testActualizarMarca() {
        Marca marcax = Marca.builder().nombre("AdidasY").build();
        Long datoBuscado = webTestClient.get().uri("http://localhost:" + this.port + "/marcas/buscarmaxid")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();

        webTestClient.put().uri("http://localhost:" + this.port +
                        "/marcas/{id}", datoBuscado)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(marcax)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @Order(4)
    public void testBuscarMarca() {
        Long datoBuscado = webTestClient.get().uri("http://localhost:" +
                        this.port + "/marcas/buscarmaxid")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();
        webTestClient.get().uri("http://localhost:" + this.port +
                        "/marcas/{id}", datoBuscado)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.nombre").isEqualTo("AdidasY");
    }

    @Test
    @Order(5)
    public void testEliminarMarca() {
        Long datoBuscado = webTestClient.get().uri("http://localhost:" +
                        this.port + "/marcas/buscarmaxid")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .returnResult()
                .getResponseBody();
        Long id = datoBuscado;
        System.out.println("Elimnar: " + id);
        webTestClient.delete().uri("http://localhost:" + this.port +
                        "/marcas/{id}", id)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk();
    }
}