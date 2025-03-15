package za.co.bbd.grad.fupboard.cli;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {
    private static String authToken;
    private static final String BASE_URL = "http://localhost:8080/";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
    
        //Welcome to FUPBoard

        //Sign into Google account to proceed
        try
        {
            authToken = Authentication.performOAuth2Login();
            System.out.println("Signed in succesfully!");
        }
        catch(Exception e)
        {
            System.out.println("Sign in was not successful: " + e.getMessage());
            //maybe ask to retry instead
            return;
        }

        System.out.println("\nChoose an option:");
        System.out.println("User");
        System.out.println("1. Accept/Decline invites");

        System.out.println("Projects");
        System.out.println("2. Create a project");
        System.out.println("3. View my projects");
        System.out.println("3. Edit my projects");
        System.out.println("4. Delete a project");

        System.out.println("FUps");
        System.out.println("5. Report an FUp");
        System.out.println("6. View an FUp");
        System.out.println("7. Delete an FUp");
        System.out.println("8. Edit an FUp");
        System.out.println("9. View leaderboard");

        System.out.println("9. Exit");
    
        System.out.println("What would you like to do? Please enter the number only:");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        do {
            System.out.println("\nChoose an option:");
            System.out.println("User");
            System.out.println("1. Accept/Decline invites");
            System.out.println("Projects");
            System.out.println("2. Create a project");
            System.out.println("3. View my projects");
            System.out.println("4. Edit my projects");
            System.out.println("5. Delete a project");
            System.out.println("FUps");
            System.out.println("6. Report an FUp");
            System.out.println("7. View an FUp");
            System.out.println("8. Delete an FUp");
            System.out.println("9. Edit an FUp");
            System.out.println("10. View leaderboard");
            System.out.println("0. Exit");
            System.out.print("What would you like to do? Please enter the number only: ");
            
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 2 -> createNewProject();
                case 3 -> viewMyProjects();
                case 4 -> editMyProjects();
                case 5 -> deleteProject();
                case 6 -> reportFUp(scanner);
                case 7 -> viewFUp(scanner);
                case 8 -> deleteFUp(scanner);
                case 9 -> editFUp(scanner);
                case 10 -> viewLeaderboard();
                case 0 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice. Please enter a valid option.");
            }
        } while (choice != 0);
        
        scanner.close();
    }
    

    //Project methods
    public static void createNewProject()
    {
        System.out.println("Enter project name:");
        String projectName = "Test";
        String jsonBody = "{\"name\":\"" + projectName + "\"}";
        sendRequest(postRequest(BASE_URL + "v1/projects", jsonBody), "Creating project");
    }

    
    //FUpMethods
    private static void reportFUp(Scanner scanner) {
        System.out.println("Enter FUp name:");
        String name = scanner.nextLine();
        
        System.out.println("Enter FUp description:");
        String description = scanner.nextLine();

        System.out.println("Enter project id:");
        int projectId = scanner.nextInt();
        
        String jsonBody = String.format("{\"name\":\"%s\", \"description\":\"%s\"}", name, description);
        sendRequest(postRequest(BASE_URL + "v1/projects/" + projectId + "/fups", jsonBody), "Reporting FUp");
    }

    private static void editFUp(Scanner scanner) {
        System.out.println("Enter FUp ID to edit:");
        int fUpId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        System.out.println("Enter new FUp name (leave blank to keep current):");
        String name = scanner.nextLine();
        
        System.out.println("Enter new FUp description (leave blank to keep current):");
        String description = scanner.nextLine();

        System.out.println("Enter project id:");
        int projectId = scanner.nextInt();
        
        String jsonBody = String.format("{\"name\":\"%s\", \"description\":\"%s\"}", name, description);
        sendRequest(patchRequest(BASE_URL + "v1/projects/" + projectId + "/fups/" + fUpId, jsonBody), "Editing FUp");
    }
    
    private static void deleteFUp(Scanner scanner) {
        System.out.println("Enter FUp ID to delete:");
        int fUpId = scanner.nextInt();

        System.out.println("Enter project id:");
        int projectId = scanner.nextInt();

        sendRequest(deleteRequest(BASE_URL + "v1/projects/" + projectId + "/fups/" + fUpId), "Deleting FUp");
    }

    public static void viewFUp(Scanner scanner)
    {
        System.out.println("Enter project id to view FUps for:");
        int fUpId = scanner.nextInt();

        System.out.println("Enter project id:");
        int projectId = scanner.nextInt();

        sendRequest(getRequest(BASE_URL + "v1/projects/" + projectId + "/fups/" + fUpId), "Getting FUps");
    }

    
    public static void viewMyProjects() {
        sendRequest(getRequest(BASE_URL + "v1/projects"), "Viewing my projects");
    }
    
    public static void editMyProjects() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter project ID to edit:");
        int projectId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
    
        System.out.println("Enter new project name (leave blank to keep current):");
        String name = scanner.nextLine();
    
        if (name.isEmpty()) {
            System.out.println("No changes made.");
            return;
        }
    
        String jsonBody = String.format("{\"name\":\"%s\"}", name);
        sendRequest(patchRequest(BASE_URL + "v1/projects/" + projectId, jsonBody), "Editing project");
    }
    
    public static void deleteProject() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter project ID to delete:");
        int projectId = scanner.nextInt();
    
        sendRequest(deleteRequest(BASE_URL + "v1/projects/" + projectId), "Deleting project");
    }
    
    public static void viewLeaderboard() {
        System.out.println("Viewing leaderboard...");
        // Add logic to display the leaderboard
    }

    //generic post,get, patch and delete request methods
    private static HttpRequest postRequest(String url, String json) {
        return HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
    }

    private static HttpRequest getRequest(String url) {
        return HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .GET()
                .build();
    }
    
    private static HttpRequest patchRequest(String url, String json) {
        return HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
    }
    
    private static HttpRequest deleteRequest(String url) {
        return HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .DELETE()
                .build();
    }
    
    private static void sendRequest(HttpRequest request, String action) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(action + " Response: " + response.statusCode());
            System.out.println(response.body());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}
