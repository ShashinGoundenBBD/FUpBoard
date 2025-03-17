package za.co.bbd.grad.fupboard.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Scanner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MainTest {

    @Mock
    private InviteService inviteService;

    @Mock
    private ProjectService projectService;

    @Mock
    private FUpService fUpService;

    @Mock
    private LeaderboardService leaderboardService;

    @Mock
    private VoteService voteService;

    @InjectMocks
    private Main main;

    private InputStream originalSystemIn;

    @BeforeEach
    void setUp() {
        originalSystemIn = System.in;
    }

    @Test
    void testRunApp_WithMockedAuthentication() throws IOException, URISyntaxException, InterruptedException {
        // Mock authentication token
        String mockAuthToken = "mock-jwt-token";

        // Mock Authentication before running the app
        try (var mockedAuth = Mockito.mockStatic(Authentication.class)) {
            mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

            // Simulate user input: Exit immediately
            String simulatedInput = "0\n";
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

            // Call authentication explicitly
            String authToken = Authentication.performOAuth2Login();

            // Run the application
            Scanner scanner = new Scanner(System.in);
            Main.runApp(scanner);

            // ✅ Verify authentication was actually called
            mockedAuth.verify(Authentication::performOAuth2Login);
        }

        System.setIn(originalSystemIn); // Reset System.in
    }

    @Test
    void testCreateNewProject() throws IOException, URISyntaxException, InterruptedException {
        // Mock authentication token
        String mockAuthToken = "mock-jwt-token";

        // Mock Authentication before running the app
        try (var mockedAuth = Mockito.mockStatic(Authentication.class)) {
            mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

            // Simulated user input (option "2" for create project, then project name, then exit)
            String simulatedInput = "2\nTest Project\n0\n";
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

            // Call authentication explicitly
            String authToken = Authentication.performOAuth2Login();

            // Run the application
            Scanner scanner = new Scanner(System.in);
            Main.runApp(scanner);

            // ✅ Verify `createNewProject()` was called once
            verify(projectService, times(1)).createNewProject(any(Scanner.class), eq(authToken));
        }

        System.setIn(originalSystemIn); // Reset System.in
    }

    @Test
    void testViewMyProjects() throws IOException, URISyntaxException, InterruptedException {
        // Mock authentication token
        String mockAuthToken = "mock-jwt-token";

        // Mock Authentication before running the app
        try (var mockedAuth = Mockito.mockStatic(Authentication.class)) {
            mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

            // Simulated user input (option "3" to view projects, then exit)
            String simulatedInput = "3\n0\n";
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

            // Call authentication explicitly
            String authToken = Authentication.performOAuth2Login();

            // Run the application
            Scanner scanner = new Scanner(System.in);
            Main.runApp(scanner);

            // ✅ Ensure `viewMyProjects()` is called
            verify(projectService, times(1)).viewMyProjects(eq(authToken));
        }

        System.setIn(originalSystemIn); // Reset System.in
    }
}
