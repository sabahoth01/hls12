package ru.itmo.hls1.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.itmo.hls1.controllers.exceptions.BookingTimeIncorrectException;
import ru.itmo.hls1.repository.BookingRepository;
import ru.itmo.hls1.repository.PlayerRepository;
import ru.itmo.hls1.repository.PlaygroundRepository;
import ru.itmo.hls1.repository.TeamRepository;
import ru.itmo.hls1.sevice.BookingService;
import ru.itmo.hls1.model.dto.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Testcontainers
public class BookingServiceTest {

    @Container
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    static {
        postgreSQLContainer.start();
    }

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlaygroundRepository playgroundRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeAll
    public static void setup() {
        // Initialize your repositories with the PostgreSQL container's JDBC URL, username, and password
        // You can use Spring's TestEntityManager or JdbcTemplate for initialization
    }

    @BeforeEach
    public void setUp() {
        // Initialize the BookingService with the repositories
        bookingService = new BookingService(bookingRepository, playgroundRepository, teamRepository, playerRepository);
    }

    @Test
    @DisplayName("Test create booking with valid input")
    public void testCreateBookingValidInput() {
        // Prepare a valid BookingDTO
        BookingDTO bookingDTO = new BookingDTO(
                null,
                1L,
                null,
                1L,
                LocalDate.now().plusDays(1),
                LocalTime.now().plus(1, ChronoUnit.HOURS),
                LocalTime.now().plus(2, ChronoUnit.HOURS)
        );

        // Call the create method
        BookingDTO createdBookingDTO = bookingService.create(bookingDTO);

        // Assert the result
        Assertions.assertNotNull(createdBookingDTO.getId());
        // Add more assertions as needed
    }

    @Test
    @DisplayName("Test create booking with invalid input")
    public void testCreateBookingInvalidInput() {
        // Prepare an invalid BookingDTO with an end time before the start time
        BookingDTO bookingDTO = new BookingDTO(
                null,
                1L,
                null,
                1L,
                LocalDate.now().plusDays(1),
                LocalTime.now().plus(2, ChronoUnit.HOURS),
                LocalTime.now().plus(1, ChronoUnit.HOURS)
        );

        // Call the create method and expect an exception
        Assertions.assertThrows(BookingTimeIncorrectException.class, () -> bookingService.create(bookingDTO));
    }



}
