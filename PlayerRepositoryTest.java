package ru.itmo.hls1.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.itmo.hls1.controllers.PlayerController;
import java.util.List;
import static org.mockito.Mockito.when;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hls1.model.dto.*;
import ru.itmo.hls1.model.entity.Player;
import ru.itmo.hls1.sevice.*;


@Testcontainers
@Configuration
@WebMvcTest(PlayerController.class)
public class PlayerRepositoryTest {
    
    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");


    @Bean
    public static PostgreSQLContainer postgreSQLContainer() {
        return postgreSQLContainer;

    }

    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    @Test
    public void testGetAllPlayers() throws Exception {
        List<PlayerDTO> playerDTOs = List.of(
                new PlayerDTO(1L, 1L, "John", "Doe", 25, 180f, 80f, Player.Gender.M),
                new PlayerDTO(2L, 2L, "Jane", "Doe", 30, 170f, 70f, Player.Gender.F)
        );

        when(playerService.findAll(0, 5)).thenReturn(playerDTOs);

        mockMvc.perform(MockMvcRequestBuilders.get("/players"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2));
    }

    
}
