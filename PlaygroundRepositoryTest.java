package ru.itmo.hls1.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class PlaygroundRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:12")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    @Sql(scripts = "/getAllPlaygrounds.sql")
    public void getAllPlaygrounds_success() throws Exception {
        mvc.perform(get("/playgrounds?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].playgroundId").value(1))
                .andExpect(jsonPath("$.content[0].location").value("Location 1"))
                .andExpect(jsonPath("$.content[0].playgroundName").value("Playground 1"))
                .andExpect(jsonPath("$.content[0].latitude").value(1.0))
                .andExpect(jsonPath("$.content[0].longitude").value(1.0))
                .andExpect(jsonPath("$.content[0].sportsId.length()").value(1))
                .andExpect(jsonPath("$.content[0].sportsId[0]").value(1))
                .andExpect(jsonPath("$.content[0].playgroundAvailability.id").value(1))
                .andExpect(jsonPath("$.content[0].playgroundAvailability.isAvailable").value(true))
                .andExpect(jsonPath("$.content[0].playgroundAvailability.availableFrom").value("00:00"))
                .andExpect(jsonPath("$.content[0].playgroundAvailability.availableTo").value("00:00"))
                .andExpect(jsonPath("$.content[0].playgroundAvailability.capacity").value(10));
    }

    @Test
    //@Sql(scripts = "/getPlaygroundById.sql")
    public void getPlaygroundById_success() throws Exception {
        mvc.perform(get("/playgrounds/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playgroundId").value(1))
                .andExpect(jsonPath("$.location").value("Location 1"))
                .andExpect(jsonPath("$.playgroundName").value("Playground 1"))
                .andExpect(jsonPath("$.latitude").value(1.0))
                .andExpect(jsonPath("$.longitude").value(1.0))
                .andExpect(jsonPath("$.sportsId.length()").value(1))
                .andExpect(jsonPath("$.sportsId[0]").value(1))
                .andExpect(jsonPath("$.playgroundAvailability.id").value(1))
                .andExpect(jsonPath("$.playgroundAvailability.isAvailable").value(true))
                .andExpect(jsonPath("$.playgroundAvailability.availableFrom").value("00:00"))
                .andExpect(jsonPath("$.playgroundAvailability.availableTo").value("00:00"))
                .andExpect(jsonPath("$.playgroundAvailability.capacity").value(10));
    }

    @Test
    public void createPlayground_success() throws Exception {
        String json = "{\"location\":\"Location 1\",\"playgroundName\":\"Playground 1\",\"latitude\":1.0,\"longitude\":1.0,\"sportsId\":[1],\"playgroundAvailability\":{\"isAvailable\":true,\"availableFrom\":\"00:00\",\"availableTo\":\"00:00\",\"capacity\":10}}";
        mvc.perform(post("/playgrounds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playgroundId").value(1))
                .andExpect(jsonPath("$.location").value("Location 1"))
                .andExpect(jsonPath("$.playgroundName").value("Playground 1"))
                .andExpect(jsonPath("$.latitude").value(1.0))
                .andExpect(jsonPath("$.longitude").value(1.0))
                .andExpect(jsonPath("$.sportsId.length()").value(1))
                .andExpect(jsonPath("$.sportsId[0]").value(1))
                .andExpect(jsonPath("$.playgroundAvailability.id").value(1))
                .andExpect(jsonPath("$.playgroundAvailability.isAvailable").value(true))
                .andExpect(jsonPath("$.playgroundAvailability.availableFrom").value("00:00"))
                .andExpect(jsonPath("$.playgroundAvailability.availableTo").value("00:00"))
                .andExpect(jsonPath("$.playgroundAvailability.capacity").value(10));
    }

    @Test
    //@Sql(scripts = "/getAllPlaygrounds.sql")
    public void deletePlayground_success() throws Exception {
        mvc.perform(delete("/playgrounds/1"))
                .andExpect(status().isOk());

    }


    @Test
    //@Sql(scripts = {"/getAllPlaygrounds.sql", "/getPlaygroundById.sql"})
    public void updatePlayground_success() throws Exception {
        String json = "{\"location\":\"Location 2\",\"playgroundName\":\"Playground 2\",\"latitude\":2.0,\"longitude\":2.0,\"sportsId\":[2],\"playgroundAvailability\":{\"isAvailable\":false,\"availableFrom\":\"01:00\",\"availableTo\":\"02:00\",\"capacity\":20}}";
        mvc.perform(put("/playgrounds/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playgroundId").value(1))
                .andExpect(jsonPath("$.location").value("Location 2"))
                .andExpect(jsonPath("$.playgroundName").value("Playground 2"))
                .andExpect(jsonPath("$.latitude").value(2.0))
                .andExpect(jsonPath("$.longitude").value(2.0))
                .andExpect(jsonPath("$.sportsId.length()").value(1))
                .andExpect(jsonPath("$.sportsId[0]").value(2))
                .andExpect(jsonPath("$.playgroundAvailability.id").value(1))
                .andExpect(jsonPath("$.playgroundAvailability.isAvailable").value(false))
                .andExpect(jsonPath("$.playgroundAvailability.availableFrom").value("01:00"))
                .andExpect(jsonPath("$.playgroundAvailability.availableTo").value("02:00"))
                .andExpect(jsonPath("$.playgroundAvailability.capacity").value(20));

    }


}
