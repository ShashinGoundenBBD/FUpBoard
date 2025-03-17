package za.co.bbd.grad.fupboard.cli;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    private static String authToken;

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        // Sign into Google account to proceed
        try {
            authToken = Authentication.performOAuth2Login();
            System.out.println("Signed in successfully!");
        } catch (Exception e) {
            System.out.println("Sign-in was not successful: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        runApp(scanner);  // ✅ Now calling the testable method
        scanner.close();
    }

    // ✅ Extracted Method for Testing
    public static void runApp(Scanner scanner) {
        int choice = -1; // Default to -1 to enter the loop

        do {
            displayMenu();
            choice = getValidIntegerInput(scanner); // Read user input

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
                case 0 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice. Please enter a valid option.");
            }
        } while (choice != 0);
    }


    public static void runApp(Scanner scanner, String authToken) { // Pass authToken explicitly
      
        // do {
        //     System.out.println("\nChoose an option:");
        //     System.out.println("3. View my projects");
        //     System.out.println("0. Exit");
        //     System.out.print("Enter your choice: ");
    
        //     choice = scanner.nextInt();
        //     scanner.nextLine(); // Consume newline
    
        //     switch (choice) {
        //         case 3 -> ProjectService.viewMyProjects(authToken); // ✅ Ensure authToken is used
        //         case 0 -> System.out.println("Exiting...");
        //         default -> System.out.println("Invalid choice. Please enter a valid option.");
        //     }
        // } while (choice != 0);


        int choice = -1; // Default to -1 to enter the loop

        do {
            displayMenu();
            choice = getValidIntegerInput(scanner); // Read user input

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
                case 0 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice. Please enter a valid option.");
            }
        } while (choice != 0);
    }

    // public static void runApp(Scanner scanner, String authToken) { // Pass authToken explicitly
    //     int choice = -1;
    //     do {
    //         System.out.println("\nChoose an option:");
    //         System.out.println("3. View my projects");
    //         System.out.println("0. Exit");
    //         System.out.print("Enter your choice: ");
    
    //         choice = scanner.nextInt();
    //         scanner.nextLine(); // Consume newline
    
    //         switch (choice) {
    //             case 3 -> ProjectService.viewMyProjects(authToken); // ✅ Ensure authToken is used
    //             case 0 -> System.out.println("Exiting...");
    //             default -> System.out.println("Invalid choice. Please enter a valid option.");
    //         }
    //     } while (choice != 0);
    // }
    

    // ✅ Helper function to display the menu
    private static void displayMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Accept/Decline invites");
        System.out.println("2. Create a project");
        System.out.println("3. View my projects");
        System.out.println("4. Edit my projects");
        System.out.println("5. Delete a project");
        System.out.println("6. Report an FUp");
        System.out.println("7. View an FUp");
        System.out.println("8. Delete an FUp");
        System.out.println("9. Edit an FUp");
        System.out.println("10. View leaderboard");
        System.out.println("11. View votes on an FUp");
        System.out.println("12. Create a vote");
        System.out.println("13. View a specific vote");
        System.out.println("14. Edit a vote");
        System.out.println("15. Delete a vote");
        System.out.println("16. View project invites");
        System.out.println("17. Send project invite");
        System.out.println("18. Delete project invite");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    // ✅ Helper function to validate integer input
    private static int getValidIntegerInput(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Consume invalid input
            System.out.print("Enter your choice: ");
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        return choice;
    }
}
