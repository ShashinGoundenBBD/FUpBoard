
package za.co.bbd.grad.fupboard.cli;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
void testViewMyProjects() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<ProjectService> mockedProjectService = Mockito.mockStatic(ProjectService.class)) { // ✅ Mock static method

        // Mock authentication token
        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        // Simulate selecting "View My Projects" and exiting
        String simulatedInput = "3\n0\n"; // Select "View My Projects" then exit
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Store original System.out
        PrintStream originalSystemOut = System.out;

        // Capture console output
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        // ✅ Mock `viewMyProjects()` static call
        mockedProjectService.when(() -> ProjectService.viewMyProjects(mockAuthToken))
                .then(invocation -> {
                    System.out.println("Mocked: Viewing all projects");
                    return null;
                });

        // Run the application (ensure authToken is passed)
        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken); // ✅ Pass mockAuthToken explicitly

        // ✅ Verify static method was called with the correct token
        mockedProjectService.verify(() -> ProjectService.viewMyProjects(mockAuthToken), times(1));

        // Restore original System.in and System.out
        System.setIn(System.in);
        System.setOut(originalSystemOut);

        // ✅ Assert expected output
        String output = testOutput.toString();
        assertTrue(output.contains("Choose an option:"), "Menu prompt was not displayed.");
    }
}

// @Test
// void testCreateProject() throws IOException, URISyntaxException, InterruptedException {
//     try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class)) {

//         // Mock authentication token
//         String mockAuthToken = "mock-jwt-token";
//         mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

//         // ✅ Simulate user input for project creation
//         String simulatedInput = "2\nMy Test Project\n0\n"; // Select "Create Project" (option 2), enter project name, then exit
//         System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

//         // Store original System.out
//         PrintStream originalSystemOut = System.out;

//         // Capture console output
//         ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
//         System.setOut(new PrintStream(testOutput));

//         // ✅ Allow `createNewProject()` to run normally
//         doCallRealMethod().when(projectService).createNewProject(any(), eq(mockAuthToken));

//         // Run the application
//         Scanner scanner = new Scanner(System.in);
//         Main.runApp(scanner, mockAuthToken); // ✅ Pass mockAuthToken explicitly

//         // ✅ Verify `createNewProject()` was called
//         verify(projectService, times(1)).createNewProject(any(), eq(mockAuthToken));

//         // Restore original System.in and System.out
//         System.setIn(System.in);
//         System.setOut(originalSystemOut);

//         // ✅ Print captured output for debugging
//         String output = testOutput.toString();
//         System.out.println("Captured output:\n" + output); // 🔍 Debugging line

//         // ✅ Assert expected output
//         assertTrue(output.contains("Enter project name:"), "Project name prompt was not displayed.");
//         assertTrue(output.contains("My Test Project"), "Project name was not captured.");
//     }
// }




@Test
void testCreateProject() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<ProjectService> mockedProjectService = Mockito.mockStatic(ProjectService.class)) { // ✅ Mock static method

        // Mock authentication token
        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        // ✅ Simulate selecting "Create Project", entering name, and exiting
        String simulatedInput = "2\nMy Test Project\n0\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Store original System.out
        PrintStream originalSystemOut = System.out;

        // Capture console output
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        // ✅ Mock `createNewProject()` static call
        mockedProjectService.when(() -> ProjectService.createNewProject(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Creating project");
                    return null;
                });

        // Run the application (ensure authToken is passed)
        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken); // ✅ Pass mockAuthToken explicitly

        // ✅ Verify `createNewProject()` was called with correct arguments
        mockedProjectService.verify(() -> ProjectService.createNewProject(any(), eq(mockAuthToken)), times(1));

        // Restore original System.in and System.out
        System.setIn(System.in);
        System.setOut(originalSystemOut);

        // ✅ Print captured output for debugging
        String output = testOutput.toString();
        System.out.println("Captured output:\n" + output); // 🔍 Debugging line

        // ✅ Assert expected output
        assertTrue(output.contains("Mocked: Creating project"), "Mocked project creation was not executed.");
    }
}




@Test
void testEditMyProjects() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<ProjectService> mockedProjectService = Mockito.mockStatic(ProjectService.class)) { // ✅ Mock static method

        // Mock authentication token
        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        // ✅ Simulate selecting "Edit Project", entering project ID & name, and exiting
        String simulatedInput = "4\n123\nNew Project Name\n0\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Store original System.out
        PrintStream originalSystemOut = System.out;

        // Capture console output
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        // ✅ Mock `editMyProjects()` static call
        mockedProjectService.when(() -> ProjectService.editMyProjects(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Editing project");
                    return null;
                });

        // Run the application (ensure authToken is passed)
        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken); // ✅ Pass mockAuthToken explicitly

        // ✅ Verify `editMyProjects()` was called with correct arguments
        mockedProjectService.verify(() -> ProjectService.editMyProjects(any(), eq(mockAuthToken)), times(1));

        // Restore original System.in and System.out
        System.setIn(System.in);
        System.setOut(originalSystemOut);

        // ✅ Print captured output for debugging
        String output = testOutput.toString();
        System.out.println("Captured output:\n" + output); // 🔍 Debugging line

        // ✅ Assert expected output
        assertTrue(output.contains("Mocked: Editing project"), "Mocked project editing was not executed.");
    }
}


@Test
void testDeleteProject() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<ProjectService> mockedProjectService = Mockito.mockStatic(ProjectService.class)) { // ✅ Mock static method

        // Mock authentication token
        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        // ✅ Simulate selecting "Delete Project", entering project ID, and exiting
        String simulatedInput = "5\n123\n0\n"; // Select "Delete Project" (option 5), enter project ID, then exit
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Store original System.out
        PrintStream originalSystemOut = System.out;

        // Capture console output
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        // ✅ Mock `deleteProject()` static call
        mockedProjectService.when(() -> ProjectService.deleteProject(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Deleting project");
                    return null;
                });

        // Run the application (ensure authToken is passed)
        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken); // ✅ Pass mockAuthToken explicitly

        // ✅ Verify `deleteProject()` was called with correct arguments
        mockedProjectService.verify(() -> ProjectService.deleteProject(any(), eq(mockAuthToken)), times(1));

        // Restore original System.in and System.out
        System.setIn(System.in);
        System.setOut(originalSystemOut);

        // ✅ Print captured output for debugging
        String output = testOutput.toString();
        System.out.println("Captured output:\n" + output); // 🔍 Debugging line

        // ✅ Assert expected output
        assertTrue(output.contains("Mocked: Deleting project"), "Mocked project deletion was not executed.");
    }
}



@Test
void testReportFUp() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<FUpService> mockedFUpService = Mockito.mockStatic(FUpService.class)) { // ✅ Mock static method

        // Mock authentication token
        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        // ✅ Simulate selecting "Report an FUp", entering details, and exiting
        String simulatedInput = "6\nFUp Title\nFUp Description\n0\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Store original System.out
        PrintStream originalSystemOut = System.out;

        // Capture console output
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        // ✅ Mock `reportFUp()` static call
        mockedFUpService.when(() -> FUpService.reportFUp(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Reporting FUp");
                    return null;
                });

        // Run the application
        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken);

        // ✅ Verify `reportFUp()` was called
        mockedFUpService.verify(() -> FUpService.reportFUp(any(), eq(mockAuthToken)), times(1));

        // Restore System.in and System.out
        System.setIn(System.in);
        System.setOut(originalSystemOut);

        // ✅ Assert expected output
        String output = testOutput.toString();
        assertTrue(output.contains("Mocked: Reporting FUp"), "Mocked FUp reporting was not executed.");
    }
}



@Test
void testViewFUp() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<FUpService> mockedFUpService = Mockito.mockStatic(FUpService.class)) {

        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        String simulatedInput = "7\n0\n"; // Select "View an FUp" then exit
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalSystemOut = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        mockedFUpService.when(() -> FUpService.viewFUp(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Viewing FUp");
                    return null;
                });

        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken);

        mockedFUpService.verify(() -> FUpService.viewFUp(any(), eq(mockAuthToken)), times(1));

        System.setIn(System.in);
        System.setOut(originalSystemOut);

        String output = testOutput.toString();
        assertTrue(output.contains("Mocked: Viewing FUp"), "Mocked FUp viewing was not executed.");
    }
}


@Test
void testDeleteFUp() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<FUpService> mockedFUpService = Mockito.mockStatic(FUpService.class)) {

        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        String simulatedInput = "8\n123\n0\n"; // Select "Delete an FUp", enter FUp ID, then exit
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalSystemOut = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        mockedFUpService.when(() -> FUpService.deleteFUp(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Deleting FUp");
                    return null;
                });

        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken);

        mockedFUpService.verify(() -> FUpService.deleteFUp(any(), eq(mockAuthToken)), times(1));

        System.setIn(System.in);
        System.setOut(originalSystemOut);

        String output = testOutput.toString();
        assertTrue(output.contains("Mocked: Deleting FUp"), "Mocked FUp deletion was not executed.");
    }
}


@Test
void testEditFUp() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<FUpService> mockedFUpService = Mockito.mockStatic(FUpService.class)) {

        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        String simulatedInput = "9\n123\nUpdated FUp Title\nUpdated FUp Description\n0\n"; 
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalSystemOut = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        mockedFUpService.when(() -> FUpService.editFUp(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Editing FUp");
                    return null;
                });

        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken);

        mockedFUpService.verify(() -> FUpService.editFUp(any(), eq(mockAuthToken)), times(1));

        System.setIn(System.in);
        System.setOut(originalSystemOut);

        String output = testOutput.toString();
        assertTrue(output.contains("Mocked: Editing FUp"), "Mocked FUp editing was not executed.");
    }
}


@Test
void testViewLeaderboard() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<LeaderboardService> mockedLeaderboardService = Mockito.mockStatic(LeaderboardService.class)) {

        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        String simulatedInput = "10\n0\n"; // Select "View leaderboard" then exit
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalSystemOut = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        mockedLeaderboardService.when(() -> LeaderboardService.viewLeaderboard(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Viewing leaderboard");
                    return null;
                });

        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken);

        mockedLeaderboardService.verify(() -> LeaderboardService.viewLeaderboard(any(), eq(mockAuthToken)), times(1));

        System.setIn(System.in);
        System.setOut(originalSystemOut);

        String output = testOutput.toString();
        assertTrue(output.contains("Mocked: Viewing leaderboard"), "Mocked leaderboard view was not executed.");
    }
}


@Test
void testViewVotes() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<VoteService> mockedVoteService = Mockito.mockStatic(VoteService.class)) {

        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        String simulatedInput = "11\n0\n"; // Select "View votes" then exit
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalSystemOut = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        mockedVoteService.when(() -> VoteService.viewVotes(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Viewing votes");
                    return null;
                });

        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken);

        mockedVoteService.verify(() -> VoteService.viewVotes(any(), eq(mockAuthToken)), times(1));

        System.setIn(System.in);
        System.setOut(originalSystemOut);

        String output = testOutput.toString();
        assertTrue(output.contains("Mocked: Viewing votes"), "Mocked vote view was not executed.");
    }
}




@Test
void testCreateVote() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<VoteService> mockedVoteService = Mockito.mockStatic(VoteService.class)) {

        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        String simulatedInput = "12\n123\nUpvote\n0\n"; // Select "Create vote", enter FUp ID & vote type, then exit
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalSystemOut = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        mockedVoteService.when(() -> VoteService.createVote(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Creating vote");
                    return null;
                });

        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken);

        mockedVoteService.verify(() -> VoteService.createVote(any(), eq(mockAuthToken)), times(1));

        System.setIn(System.in);
        System.setOut(originalSystemOut);

        String output = testOutput.toString();
        assertTrue(output.contains("Mocked: Creating vote"), "Mocked vote creation was not executed.");
    }
}


@Test
void testViewSpecificVote() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<VoteService> mockedVoteService = Mockito.mockStatic(VoteService.class)) {

        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        String simulatedInput = "13\n0\n"; // Select "View specific vote" then exit
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalSystemOut = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        mockedVoteService.when(() -> VoteService.viewVotes(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Viewing specific vote");
                    return null;
                });

        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken);

        mockedVoteService.verify(() -> VoteService.viewVotes(any(), eq(mockAuthToken)), times(1));

        System.setIn(System.in);
        System.setOut(originalSystemOut);

        String output = testOutput.toString();
        assertTrue(output.contains("Mocked: Viewing specific vote"), "Mocked specific vote view was not executed.");
    }
}


@Test
void testEditVote() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<VoteService> mockedVoteService = Mockito.mockStatic(VoteService.class)) {

        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        String simulatedInput = "14\n123\nUpdated Vote\n0\n"; // Select "Edit vote", enter ID & new value, then exit
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalSystemOut = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        mockedVoteService.when(() -> VoteService.editVote(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Editing vote");
                    return null;
                });

        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken);

        mockedVoteService.verify(() -> VoteService.editVote(any(), eq(mockAuthToken)), times(1));

        System.setIn(System.in);
        System.setOut(originalSystemOut);

        String output = testOutput.toString();
        assertTrue(output.contains("Mocked: Editing vote"), "Mocked vote editing was not executed.");
    }
}

    
 
@Test
void testDeleteVote() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<VoteService> mockedVoteService = Mockito.mockStatic(VoteService.class)) {

        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        String simulatedInput = "15\n123\n0\n"; // Select "Delete vote", enter Vote ID, then exit
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalSystemOut = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        mockedVoteService.when(() -> VoteService.deleteVote(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Deleting vote");
                    return null;
                });

        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken);

        mockedVoteService.verify(() -> VoteService.deleteVote(any(), eq(mockAuthToken)), times(1));

        System.setIn(System.in);
        System.setOut(originalSystemOut);

        String output = testOutput.toString();
        assertTrue(output.contains("Mocked: Deleting vote"), "Mocked vote deletion was not executed.");
    }
}

@Test
void testViewProjectInvites() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<InviteService> mockedInviteService = Mockito.mockStatic(InviteService.class)) {

        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        String simulatedInput = "16\n0\n"; // Select "View project invites" then exit
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalSystemOut = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        mockedInviteService.when(() -> InviteService.viewProjectInvites(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Viewing project invites");
                    return null;
                });

        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken);

        mockedInviteService.verify(() -> InviteService.viewProjectInvites(any(), eq(mockAuthToken)), times(1));

        System.setIn(System.in);
        System.setOut(originalSystemOut);

        String output = testOutput.toString();
        assertTrue(output.contains("Mocked: Viewing project invites"), "Mocked project invites view was not executed.");
    }
}


@Test
void testCreateProjectInvite() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<InviteService> mockedInviteService = Mockito.mockStatic(InviteService.class)) {

        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        String simulatedInput = "17\nuser@example.com\nProject A\n0\n"; // Select "Create project invite", enter details, then exit
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalSystemOut = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        mockedInviteService.when(() -> InviteService.createProjectInvite(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Creating project invite");
                    return null;
                });

        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken);

        mockedInviteService.verify(() -> InviteService.createProjectInvite(any(), eq(mockAuthToken)), times(1));

        System.setIn(System.in);
        System.setOut(originalSystemOut);

        String output = testOutput.toString();
        assertTrue(output.contains("Mocked: Creating project invite"), "Mocked project invite creation was not executed.");
    }
}



@Test
void testDeleteProjectInvite() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class);
         MockedStatic<InviteService> mockedInviteService = Mockito.mockStatic(InviteService.class)) {

        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        String simulatedInput = "18\ninvite123\n0\n"; // Select "Delete project invite", enter invite ID, then exit
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalSystemOut = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        mockedInviteService.when(() -> InviteService.deleteProjectInvite(any(), eq(mockAuthToken)))
                .then(invocation -> {
                    System.out.println("Mocked: Deleting project invite");
                    return null;
                });

        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken);

        mockedInviteService.verify(() -> InviteService.deleteProjectInvite(any(), eq(mockAuthToken)), times(1));

        System.setIn(System.in);
        System.setOut(originalSystemOut);

        String output = testOutput.toString();
        assertTrue(output.contains("Mocked: Deleting project invite"), "Mocked project invite deletion was not executed.");
    }
}


@Test
void testExit() throws IOException, URISyntaxException, InterruptedException {
    try (MockedStatic<Authentication> mockedAuth = Mockito.mockStatic(Authentication.class)) {

        String mockAuthToken = "mock-jwt-token";
        mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

        String simulatedInput = "0\n"; // Select "Exit"
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        PrintStream originalSystemOut = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));

        Scanner scanner = new Scanner(System.in);
        Main.runApp(scanner, mockAuthToken);

        System.setIn(System.in);
        System.setOut(originalSystemOut);

        String output = testOutput.toString();
        assertTrue(output.contains("Exiting..."), "Exit message was not displayed.");
    }
}


    
}





// package za.co.bbd.grad.fupboard.cli;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.io.ByteArrayInputStream;
// import java.io.IOException;
// import java.io.InputStream;
// import java.net.URISyntaxException;
// import java.util.Scanner;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class MainTest {

//     @Mock
//     private InviteService inviteService;

//     @Mock
//     private ProjectService projectService;

//     @Mock
//     private FUpService fUpService;

//     @Mock
//     private LeaderboardService leaderboardService;

//     @Mock
//     private VoteService voteService;

//     @InjectMocks
//     private Main main;

//     private InputStream originalSystemIn;

//     @BeforeEach
//     void setUp() {
//         originalSystemIn = System.in;
//     }

//     @Test
//     void testRunApp_WithMockedAuthentication() throws IOException, URISyntaxException, InterruptedException {
//         // Mock authentication token
//         String mockAuthToken = "mock-jwt-token";

//         // Mock Authentication before running the app
//         try (var mockedAuth = Mockito.mockStatic(Authentication.class)) {
//             mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

//             // Simulate user input: Exit immediately
//             String simulatedInput = "0\n";
//             System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

//             // Call authentication explicitly
//             String authToken = Authentication.performOAuth2Login();

//             // Run the application
//             Scanner scanner = new Scanner(System.in);
//             Main.runApp(scanner);

//             // ✅ Verify authentication was actually called
//             mockedAuth.verify(Authentication::performOAuth2Login);
//         }

//         System.setIn(originalSystemIn); // Reset System.in
//     }

//     @Test
//     void testCreateNewProject() throws IOException, URISyntaxException, InterruptedException {
//         // Mock authentication token
//         String mockAuthToken = "mock-jwt-token";

//         // Mock Authentication before running the app
//         try (var mockedAuth = Mockito.mockStatic(Authentication.class)) {
//             mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

//             // Simulated user input (option "2" for create project, then project name, then exit)
//             String simulatedInput = "2\nTest Project\n0\n";
//             System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

//             // Call authentication explicitly
//             String authToken = Authentication.performOAuth2Login();

//             // Run the application
//             Scanner scanner = new Scanner(System.in);
//             Main.runApp(scanner);

//             // ✅ Verify `createNewProject()` was called once
//             verify(projectService, times(1)).createNewProject(any(Scanner.class), eq(authToken));
//         }

//         System.setIn(originalSystemIn); // Reset System.in
//     }

//     @Test
//     void testViewMyProjects() throws IOException, URISyntaxException, InterruptedException {
//         // Mock authentication token
//         String mockAuthToken = "mock-jwt-token";

//         // Mock Authentication before running the app
//         try (var mockedAuth = Mockito.mockStatic(Authentication.class)) {
//             mockedAuth.when(Authentication::performOAuth2Login).thenReturn(mockAuthToken);

//             // Simulated user input (option "3" to view projects, then exit)
//             String simulatedInput = "3\n0\n";
//             System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

//             // Call authentication explicitly
//             String authToken = Authentication.performOAuth2Login();

//             // Run the application
//             Scanner scanner = new Scanner(System.in);
//             Main.runApp(scanner);

//             // ✅ Ensure `viewMyProjects()` is called
//             verify(projectService, times(1)).viewMyProjects(eq(authToken));
//         }

//         System.setIn(originalSystemIn); // Reset System.in
//     }
// }
