package ru.itmo.hls1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.itmo.hls1.config.security.utils.JwtUtils;
import ru.itmo.hls1.model.dto.UserDTO;
import ru.itmo.hls1.sevice.AuthService;
import ru.itmo.hls1.sevice.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class AuthServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Autowired
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @BeforeEach
    public void setUp() {
        when(passwordEncoder.encode(Mockito.anyString())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    @Sql(scripts = "/scripts/create_user.sql")
    public void signUp_success() {
        UserDTO userDTO = new UserDTO(null, "testUser", "testPassword");
        ResponseEntity<?> response = authService.signUp(userDTO);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @Sql(scripts = "/scripts/create_user.sql")
    public void signIn_success() {
        UserDTO userDTO = new UserDTO(null, "testUser", "testPassword");
        ResponseEntity<?> response = authService.signIn(userDTO);
        assertEquals(200, response.getStatusCodeValue());
    }
}
