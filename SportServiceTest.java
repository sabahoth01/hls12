package ru.itmo.hls1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hls1.controllers.exceptions.not_found.SportNotFoundException;
import ru.itmo.hls1.model.dto.SportDTO;
import ru.itmo.hls1.model.entity.Sport;
import ru.itmo.hls1.repository.SportRepository;
import ru.itmo.hls1.sevice.SportService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
public class SportServiceTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SportRepository sportRepository;

    private SportService sportService;

    @BeforeEach
    public void setUp() {
        sportService = new SportService(sportRepository);
    }

    @Test
    public void updateSport_shouldUpdateSport() {
        // given
        Sport sport = new Sport(null, "Football");
        sportRepository.save(sport);
        entityManager.flush();

        SportDTO updatedDto = new SportDTO(sport.getSportId(), "Basketball");

        // when
        SportDTO result = sportService.updateSport(sport.getSportId(), updatedDto);

        // then
        assertThat(result.getSportId()).isEqualTo(sport.getSportId());
        assertThat(result.getSportType()).isEqualTo("Basketball");
    }

    @Test
    public void updateSport_shouldThrowException_whenSportNotFound() {
        // given
        SportDTO updatedDto = new SportDTO(1L, "Basketball");

        // when
        assertThatThrownBy(() -> sportService.updateSport(1L, updatedDto))

                // then
                .isInstanceOf(SportNotFoundException.class)
                .hasMessage("id = 1");
    }
}
