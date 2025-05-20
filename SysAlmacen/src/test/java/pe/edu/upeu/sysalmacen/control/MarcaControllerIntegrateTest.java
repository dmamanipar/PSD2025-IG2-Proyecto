package pe.edu.upeu.sysalmacen.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import pe.edu.upeu.sysalmacen.dtos.UsuarioDTO;
import pe.edu.upeu.sysalmacen.modelo.Marca;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment =SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace =AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class MarcaControllerIntegrateTest {
    @Autowired
    private ObjectMapper objectMapper;
    @LocalServerPort
    private int port;
    private String token;
    private String idCreado;

    @BeforeEach
    public void setUp() {
        RestAssured.port = this.port;
        UsuarioDTO.UsuarioCrearDto udto = new
                UsuarioDTO.UsuarioCrearDto("eliasmp@upeu.edu.pe",
                "Da123456*".toCharArray(), "ADMIN", "Activo");
        try {
            token = given()
                    .contentType(ContentType.JSON)
                    .body(new UsuarioDTO.CredencialesDto("eliasmp@upeu.edu.pe", "Da123456*".toCharArray())) //.toCharArray()
                    .when().post("/users/login")
                    .andReturn().jsonPath().getString("token");
        }catch (Exception e){
            if (token==null) {
                token = given()
                        .contentType(ContentType.JSON)
                        .body(udto) //.toCharArray()
                        .when().post("/users/register")
                        .andReturn().jsonPath().getString("token");
            }
            System.out.println("Ver:" + token);
        }
        idCreado = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/marcas/buscarmaxid")
                .then()
                .statusCode(200)
                .extract().body().asString();
        System.out.println(idCreado);
    }

    @Order(2)
    @Test
    public void testListMarca() throws Exception {
                given()
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer "+token)
                .when()
                .get("/marcas")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON);
    }
    @Order(1)
    @Test
    public void testCrearMarca() throws Exception {
        Marca dto = new Marca(null, "NUEVA MARCA");
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(objectMapper.writeValueAsString(dto))
                .when()
                .post("/marcas")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("message", equalTo("true"));
    }
    @Order(3)
    @Test
    void testFindById() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/marcas/{id}", idCreado)
                .then()
                .statusCode(200)
                .body("idMarca", equalTo(Integer.parseInt(idCreado)));
    }
    @Order(4)
    @Test
    void testUpdate() {
        Marca updated = new Marca();
        updated.setNombre("Marca actualizada");
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(updated)
                .when()
                .put("/marcas/{id}", idCreado)
                .then()
                .statusCode(200)
                .body("idMarca", equalTo(Integer.parseInt(idCreado)))
                .body("nombre", equalTo("Marca actualizada"));
    }

    @Order(6)
    @Test
    void testDelete() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/marcas/{id}", idCreado)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("message", equalTo("true"));
    }
    @Order(5)
    @Test
    void testGetMarcaMaxId() {
        String idCreado = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/marcas/buscarmaxid")
                .then()
                .statusCode(200)
                .extract().body().asString();
        Assertions.assertNotNull(idCreado);
    }
}
