package ru.itmo.hls1.repository;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import ru.itmo.hls1.model.dto.RoleDTO;
import ru.itmo.hls1.model.dto.UserDTO;
import ru.itmo.hls1.model.entity.Role;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRepositoryTest {

    @ClassRule
    public static DockerRule<PostgreSQLContainer> postgreSQLContainer = DockerRule.builder()
            .imageName(DockerImageName.parse("postgres:latest"))
            .env("POSTGRES_USER", "test")
            .env("POSTGRES_PASSWORD", "test")
            .env("POSTGRES_DB", "test")
            .build();

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAllUsers() throws Exception {
        // Arrange
        UserDTO user1 = new UserDTO(1L, "user1", "password1");
        UserDTO user2 = new UserDTO(2L, "user2", "password2");
        List<UserDTO> expectedUsers = Arrays.asList(user1, user2);

        // Act & Assert
        mockMvc.perform(get("/users/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].userId", containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$[*].login", containsInAnyOrder("user1", "user2")))
                .andExpect(jsonPath("$[*].password", containsInAnyOrder("password1", "password2")));
    }

    @Test
    public void getUserById() throws Exception {
        // Arrange
        UserDTO expectedUser = new UserDTO(1L, "user1", "password1");

        // Act & Assert
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.userId", is(1)))
                .andExpect((ResultMatcher) jsonPath("$.login", is("user1")))
                .andExpect((ResultMatcher) jsonPath("$.password", is("password1")));
    }

    @Test
    public void createUser() throws Exception {
        // Arrange
        UserDTO user = new UserDTO(null, "user3", "password3");

        // Act & Assert
        mockMvc.perform(post("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(3)))
                .andExpect(jsonPath("$.login", is("user3")))
                .andExpect(jsonPath("$.password", is("password3")));
    }

    @Test
    public void addRoleToUser() throws Exception {
        // Arrange
        RoleDTO role = new RoleDTO(Role.SUPERVISOR);

        // Act & Assert
        mockMvc.perform(put("/users/1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk());
    }

    @Test
    public void removeRoleFromUser() throws Exception {
        // Arrange
        RoleDTO role = new RoleDTO(Role.SUPERVISOR);

        // Act & Assert
        mockMvc.perform(delete("/users/1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk());
    }
}

//
//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import java.util.*;
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.hasSize;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.PostgreSQLContainer;
//import ru.itmo.hls1.model.entity.*;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@AutoConfigureTestEntityManager
//@Testcontainers
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class UserRepositoryTest {
//    @LocalServerPort
//    private Integer port;
//
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
//            "postgres:latest"
//    );
//
//    @BeforeAll
//    static void beforeAll() {
//        postgres.start();
//    }
//
//    @AfterAll
//    static void afterAll() {
//        postgres.stop();
//    }
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.username", postgres::getUsername);
//        registry.add("spring.datasource.password", postgres::getPassword);
//    }
//
//    @Autowired
//    UserRepository userRepository;
//
//    @BeforeEach
//    void setUp() {
//        RestAssured.baseURI = "http://localhost:" + port;
//        userRepository.deleteAll();
//    }
//
//    @Test
//    void shouldGetAllUsers() {
//        List<User> users = List.of(
//                new User(null, "Anna1", "12345", Set.of(Role.PLAYER)),
//                new User(null, "Anna2", "12345", Set.of(Role.SUPERVISOR)),
//                new User(null, "Anna3", "12345", Set.of(Role.TEAM_MANAGER)),
//                new User(null, "Anna4", "12345", Set.of(Role.PLAYER)),
//                new User(null, "Anna5", "12345", Set.of(Role.PLAYER)),
//                new User(null, "tazi1", "12345", Set.of(Role.PLAYER)),
//                new User(null, "tazi2", "12345", null),
//                new User(null, "tazi3", "12345", Set.of(Role.PLAYER)),
//                new User(null, "tazi4", "12345", Set.of(Role.PLAYER)),
//                new User(null, "tazi5", "12345", Set.of(Role.SUPERVISOR)),
//                new User(null, "daniil1", "12345", null),
//                new User(null, "daniil2", "12345", Set.of(Role.PLAYER)),
//                new User(null, "daniil3", "12345", Set.of(Role.EDITOR)),
//                new User(null, "daniil4", "12345", Set.of(Role.PLAYER)),
//                new User(null, "daniil5", "12345", null)
//        );
//        userRepository.saveAll(users);
//
//        given()
//                .contentType(ContentType.JSON)
//                .when()
//                .get("/")
//                .then()
//                .statusCode(200)
//                .body(".", hasSize(15));
//
//    }
//
//}
