package za.co.bbd.grad.fupboard.cli;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    private static String authToken;

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
    
        // Welcome to FUPBoard
        displayWelcomeMessage();
        // Sign into Google account to proceed
        try {
            authToken = Authentication.performOAuth2Login();
            System.out.println("Signed in successfully! Token: " + authToken);
        } catch (Exception e) {
            System.out.println("Sign-in was not successful: " + e.getMessage());
            // Maybe ask to retry instead
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean continueUsing = true;

        while (continueUsing) {
            System.out.println("\nChoose an option:");
            System.out.println(" -> User");
            System.out.println("1. Accept/Decline invites");
            System.out.println("\n -> Projects");
            System.out.println("2. Create a project");
            System.out.println("3. View my projects");
            System.out.println("4. Edit my projects");
            System.out.println("5. Delete a project");
            System.out.println("\n -> FUps");
            System.out.println("6. Report an FUp");
            System.out.println("7. View an FUp");
            System.out.println("8. Delete an FUp");
            System.out.println("9. Edit an FUp");
            System.out.println("10. View leaderboard");
            System.out.println("11. View votes on an FUp");
            System.out.println("\n -> Votes");
            System.out.println("12. Create a vote");
            System.out.println("13. View a specific vote");
            System.out.println("14. Edit a vote");
            System.out.println("15. Delete a vote");
            System.out.println("16. View project invites");
            System.out.println("17. Send project invite");
            System.out.println("18. Delete project invite");
            System.out.println("0. Exit");
            System.out.print("What would you like to do? Please enter the number only: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> InviteService.acceptOrDeclineInvite(scanner, authToken);
                case 2 -> ProjectService.createNewProject(scanner, authToken);
                case 3 -> ProjectService.viewMyProjects(authToken);
                case 4 -> ProjectService.editMyProjects(scanner, authToken);
                case 5 -> ProjectService.deleteProject(scanner, authToken);
                case 6 -> FUpService.reportFUp(scanner, authToken);
                case 7 -> FUpService.viewFUp(scanner, authToken);
                case 8 -> FUpService.deleteFUp(scanner, authToken);
                case 9 -> FUpService.editFUp(scanner, authToken);
                case 10 -> LeaderboardService.viewLeaderboard(scanner, authToken);
                case 11 -> VoteService.viewVotes(scanner, authToken);
                case 12 -> VoteService.createVote(scanner, authToken);
                case 13 -> VoteService.viewVotes(scanner, authToken);
                case 14 -> VoteService.editVote(scanner, authToken);
                case 15 -> VoteService.deleteVote(scanner, authToken);
                case 16 -> InviteService.viewProjectInvites(scanner, authToken);
                case 17 -> InviteService.createProjectInvite(scanner, authToken);
                case 18 -> InviteService.deleteProjectInvite(scanner, authToken);
                case 0 -> {
                    System.out.println("Exiting...");
                    continueUsing = false;
                }
                default -> System.out.println("Invalid choice. Please enter a valid option.");
            }

            if (continueUsing) {
                System.out.print("\nWould you like to do anything else? (y/n): ");
                String response = scanner.nextLine().trim().toLowerCase();
                if (!response.equals("y")) {
                    System.out.println("Exiting...");
                    continueUsing = false;
                }
            }
        }

        scanner.close();
    }

    private static void displayWelcomeMessage() {

        String reset = "\u001B[0m";
        String green = "\u001B[32m";

        System.out.println(green + """
        ─────────────────────────────────────────────────────────────
        ─██████████████────────────────██████──██████─██████████████─
        ─██░░░░░░░░░░██────────────────██░░██──██░░██─██░░░░░░░░░░██─
        ─██░░██████████────────────────██░░██──██░░██─██░░██████░░██─
        ─██░░██────────────────────────██░░██──██░░██─██░░██──██░░██─
        ─██░░██████████─██████████████─██░░██──██░░██─██░░██████░░██─
        ─██░░░░░░░░░░██─██░░░░░░░░░░██─██░░██──██░░██─██░░░░░░░░░░██─
        ─██░░██████████─██████████████─██░░██──██░░██─██░░██████████─
        ─██░░██────────────────────────██░░██──██░░██─██░░██─────────
        ─██░░██────────────────────────██░░██████░░██─██░░██─────────
        ─██░░██────────────────────────██░░░░░░░░░░██─██░░██─────────
        ─██████────────────────────────██████████████─██████─────────
        ─────────────────────────────────────────────────────────────
        ──────────────────────────────────────────────────────────────────────────────────
        ─██████████████───██████████████─██████████████─████████████████───████████████───
        ─██░░░░░░░░░░██───██░░░░░░░░░░██─██░░░░░░░░░░██─██░░░░░░░░░░░░██───██░░░░░░░░████─
        ─██░░██████░░██───██░░██████░░██─██░░██████░░██─██░░████████░░██───██░░████░░░░██─
        ─██░░██──██░░██───██░░██──██░░██─██░░██──██░░██─██░░██────██░░██───██░░██──██░░██─
        ─██░░██████░░████─██░░██──██░░██─██░░██████░░██─██░░████████░░██───██░░██──██░░██─
        ─██░░░░░░░░░░░░██─██░░██──██░░██─██░░░░░░░░░░██─██░░░░░░░░░░░░██───██░░██──██░░██─
        ─██░░████████░░██─██░░██──██░░██─██░░██████░░██─██░░██████░░████───██░░██──██░░██─
        ─██░░██────██░░██─██░░██──██░░██─██░░██──██░░██─██░░██──██░░██─────██░░██──██░░██─
        ─██░░████████░░██─██░░██████░░██─██░░██──██░░██─██░░██──██░░██████─██░░████░░░░██─
        ─██░░░░░░░░░░░░██─██░░░░░░░░░░██─██░░██──██░░██─██░░██──██░░░░░░██─██░░░░░░░░████─
        ─████████████████─██████████████─██████──██████─██████──██████████─████████████───
        ──────────────────────────────────────────────────────────────────────────────────                                                
            """ + reset);
    }
}
