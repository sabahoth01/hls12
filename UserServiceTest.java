package ru.itmo.hls1.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.hls1.model.dto.RoleDTO;
import ru.itmo.hls1.model.dto.UserDTO;
import ru.itmo.hls1.model.entity.Role;
import ru.itmo.hls1.model.entity.User;
import ru.itmo.hls1.repository.PlayerRepository;
import ru.itmo.hls1.repository.UserRepository;
import ru.itmo.hls1.sevice.SportService;
import ru.itmo.hls1.sevice.UserService;
import org.junit.jupiter.api.*;
import org.testcontainers.utility.DockerImageName;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
public class UserServiceTest {

    @Container
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    static {
        postgreSQLContainer.start();
    }

    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerRepository playerRepository;


    @BeforeEach
    public void setUp() {
        // Initialize your UserService instance for each test
        //userService = new UserService(userRepository);

    }

    @AfterAll
    public static void cleanUp() {
        postgreSQLContainer.stop();
    }

    @Test
    public void testCreateUser() {
        UserDTO userDTO = new UserDTO(null, "test_user", "test_password");
        UserDTO createdUserDTO = userService.create(userDTO);
        assertNotNull(createdUserDTO.getUserId());
        assertEquals(userDTO.getLogin(), createdUserDTO.getLogin());
    }

    @Test

    void testAddRole() {
        UserDTO userDTO = new UserDTO(1L, "test_user", "test_password");
        User user = mapper.dtoToEntity(userDTO);
        userRepository.save(user);
        RoleDTO roleDTO = new RoleDTO(Role.PLAYER);
        // Act
        userService.addRole(userDTO.getUserId(), roleDTO);
        // Assert
        Optional<User> optionalUser = userRepository.findById(userDTO.getUserId());
        assertTrue(optionalUser.isPresent());
        User updatedUser = optionalUser.get();
        assertTrue(updatedUser.getRoles().contains(Role.PLAYER));

    }

    @Test
    void testRemoveRole() {
        // Arrange
        UserDTO userDTO = new UserDTO(1L, "test_user", "test_password");
        User user = mapper.dtoToEntity(userDTO);
        userRepository.save(user);
        RoleDTO roleDTO = new RoleDTO(Role.PLAYER);
        // Act
        userService.addRole(userDTO.getUserId(), roleDTO);
        userService.removeRole(userDTO.getUserId(), roleDTO);
        // Assert
        Optional<User> optionalUser = userRepository.findById(userDTO.getUserId());
        assertTrue(optionalUser.isPresent());
        User updatedUser = optionalUser.get();
        assertFalse(updatedUser.getRoles().contains(Role.PLAYER));
    }


}
