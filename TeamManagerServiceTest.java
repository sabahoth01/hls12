package ru.itmo.hls1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.Sets;
import ru.itmo.hls1.controllers.exceptions.not_found.NotFoundException;
import ru.itmo.hls1.controllers.exceptions.not_found.TeamManagerNotFoundException;
import ru.itmo.hls1.model.dto.TeamManagerDTO;
import ru.itmo.hls1.model.entity.Role;
import ru.itmo.hls1.model.entity.TeamManager;
import ru.itmo.hls1.model.entity.User;
import ru.itmo.hls1.repository.TeamManagerRepository;
import ru.itmo.hls1.repository.UserRepository;
import ru.itmo.hls1.sevice.TeamManagerService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Testcontainers
public class TeamManagerServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @Autowired
    private TeamManagerService service;

    @Autowired
    private TeamManagerRepository repository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createTeamManager_success() {
        // Arrange
        User user = new User(
                1L,
                "test_username",
                "test_password",
                Sets.newHashSet(Role.TEAM_MANAGER)
        );
        userRepository.save(user);

        TeamManagerDTO teamManagerDTO = new TeamManagerDTO(
                1L,
                1L,
                "test_first_name",
                "test_last_name",
                "+12345678901",
                "test_email@example.com"
        );

        // Act
        TeamManagerDTO result = service.create(teamManagerDTO);

        // Assert
        assertThat(result.getId()).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("test_first_name");
        assertThat(result.getLastName()).isEqualTo("test_last_name");
        assertThat(result.getPhone()).isEqualTo("+12345678901");
        assertThat(result.getEmail()).isEqualTo("test_email@example.com");
    }

    @Test
    public void updateTeamManager_success() {
        // Arrange
        User user = new User(
                1L,
                "test_username",
                "test_password",
                Sets.newHashSet(Role.TEAM_MANAGER)
        );
        userRepository.save(user);

        TeamManager teamManager = new TeamManager(
                1L,
                "test_first_name",
                "test_last_name",
                "+12345678901",
                "test_email@example.com",
                user,
                null
        );
        repository.save(teamManager);

        TeamManagerDTO updated = new TeamManagerDTO(
                1L,
                1L,
                "updated_first_name",
                "updated_last_name",
                "+12345678902",
                "updated_email@example.com"
        );

        // Act
        TeamManagerDTO result = service.update(1L, updated);
        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("updated_first_name");
        assertThat(result.getLastName()).isEqualTo("updated_last_name");
        assertThat(result.getPhone()).isEqualTo("+12345678902");
        assertThat(result.getEmail()).isEqualTo("updated_email@example.com");
    }

    @Test
    public void deleteTeamManager_teamManagerFound_deletesTeamManager() {
        // Arrange
        User user = new User(
                1L,
                "test_username",
                "test_password",
                Sets.newHashSet(Role.TEAM_MANAGER)

        );
        userRepository.save(user);
        TeamManager teamManager = new TeamManager(
                1L,
                "test_first_name",
                "test_last_name",
                "+12345678901",
                "test_email@example.com",
                user,
                null

        );
        repository.save(teamManager);
        // Act
        service.delete(1L);
        // Assert
        assertThat(repository.findById(1L)).isEmpty();
    }

//    @Test
//    public void getNotFoundIdException_idIsNegative_throwsIllegalArgumentException() {
//        // Arrange
//        long id = -1L;
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () -> service.getNotFoundIdException(id));
//    }


//    @Test
//    public void getNotFoundIdException_idIsPositive_returnsTeamManagerNotFoundException() {
//        // Arrange
//        long id = 1L;
//        // Act
//        NotFoundException result = service.getNotFoundIdException(id);
//        // Assert
//        assertThat(result).isInstanceOf(TeamManagerNotFoundException.class);
//        assertThat(((TeamManagerNotFoundException) result).getId()).isEqualTo(id);
//
//    }

}

