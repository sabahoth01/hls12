package ru.itmo.hls1.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.itmo.hls1.model.entity.Sport;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.itmo.hls1.model.dto.SportDTO;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.*;

import java.util.List;


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class SportRepositoryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SportRepository sportRepository;

    @BeforeEach
    public void setUp() {
        sportRepository.deleteAll();
    }

    @Test
    public void getAllSports_success() throws Exception {
        Sport sport1 = new Sport(1L, "Football");
        Sport sport2 = new Sport(2L, "Basketball");
        sportRepository.saveAll(List.of(sport1, sport2));

        mockMvc.perform(get("/sports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].sportId", is(1)))
                .andExpect(jsonPath("$[0].sportType", is("Football")))
                .andExpect(jsonPath("$[1].sportId", is(2)))
                .andExpect(jsonPath("$[1].sportType", is("Basketball")));
    }

    @Test
    public void getSportById_success() throws Exception {
        Sport sport = new Sport(1L, "Football");
        sportRepository.save(sport);

        mockMvc.perform(get("/sports/1"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.sportId", is(1)))
                .andExpect((ResultMatcher) jsonPath("$.sportType", is("Football")));
    }

    @Test
    public void createSport_success() throws Exception {
        SportDTO sportDTO = new SportDTO(null, "Football");

        mockMvc.perform(post("/sports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sportDTO)))
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.sportId", is(1)))
                .andExpect((ResultMatcher) jsonPath("$.sportType", is("Football")));
    }

    @Test
    public void updateSport_success() throws Exception {
        Sport sport = new Sport(1L, "Football");
        sportRepository.save(sport);

        SportDTO updatedSportDTO = new SportDTO(1L, "Basketball");

        mockMvc.perform(put("/sports/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSportDTO)))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.sportId", is(1)))
                .andExpect((ResultMatcher) jsonPath("$.sportType", is("Basketball")));
    }

    @Test
    public void deleteSport_success() throws Exception {
        Sport sport = new Sport(1L, "Football");
        sportRepository.save(sport);

        mockMvc.perform(delete("/sports/1"))
                .andExpect(status().isOk());
    }
}
