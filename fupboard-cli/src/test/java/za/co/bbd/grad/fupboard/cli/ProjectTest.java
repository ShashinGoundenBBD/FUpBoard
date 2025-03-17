package za.co.bbd.grad.fupboard.cli;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainTest {

    private InputStream originalSystemIn;

    @BeforeEach
    void setUp() {
        originalSystemIn = System.in; // Store original System.in
    }

    @Test
    void testUserAcceptInvite() throws Exception {
        // Simulate user entering "1" and then "n" (no) to exit
        String simulatedInput = "1\nn\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        Scanner scanner = new Scanner(System.in);
        InviteService mockInviteService = mock(InviteService.class);
        Authentication mockAuth = mock(Authentication.class);
        
        // Mock authentication success
        when(mockAuth.performOAuth2Login()).thenReturn("mock-token");

        Main.runApp(scanner);

        // Verify that the correct method was called
        verify(mockInviteService, times(1)).acceptOrDeclineInvite(any(), anyString());

        System.setIn(originalSystemIn); // Restore System.in
    }

    @Test
    void testUserCreatesProject() throws Exception {
        // Simulate user entering "2" and then "n" to exit
        String simulatedInput = "2\nn\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        Scanner scanner = new Scanner(System.in);
        ProjectService mockProjectService = mock(ProjectService.class);
        Authentication mockAuth = mock(Authentication.class);

        // Mock authentication success
        when(mockAuth.performOAuth2Login()).thenReturn("mock-token");

        Main.runApp(scanner);

        // Verify that the correct method was called
        verify(mockProjectService, times(1)).createNewProject(any(), anyString());

        System.setIn(originalSystemIn); // Restore System.in
    }

    @Test
    void testInvalidInputHandled() throws Exception {
        // Simulate invalid input followed by valid input
        String simulatedInput = "abc\n3\nn\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        Scanner scanner = new Scanner(System.in);
        ProjectService mockProjectService = mock(ProjectService.class);
        Authentication mockAuth = mock(Authentication.class);

        // Mock authentication success
        when(mockAuth.performOAuth2Login()).thenReturn("mock-token");

        Main.runApp(scanner);

        // Verify that invalid input was ignored and correct method was called
        verify(mockProjectService, times(1)).viewMyProjects(anyString());

        System.setIn(originalSystemIn); // Restore System.in
    }
}
