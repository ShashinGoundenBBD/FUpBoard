package za.co.bbd.grad.fupboard.cli;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ProjectService {
    private static final String BASE_URL = "http://localhost:8080/";

    public static void createNewProject(Scanner scanner, String authToken) {
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine();

        if (projectName.isEmpty()) {
            System.out.println("Project name cannot be empty.");
            return;
        }

        String jsonBody = String.format("{\"name\":\"%s\"}", projectName);
        sendRequest(authToken, postRequest(authToken, BASE_URL + "v1/projects", jsonBody), "Creating project");
    }

    public static void getProjectById(Scanner scanner, String authToken) {
        System.out.print("Enter project ID: ");
        int projectId = scanner.nextInt();
        sendRequest(authToken, getRequest(authToken, BASE_URL + "v1/projects/" + projectId), "Fetching project details");
    }

    public static void deleteProject(Scanner scanner, String authToken) {
        System.out.print("Enter project ID to delete: ");
        int projectId = scanner.nextInt();
        sendRequest(authToken, deleteRequest(authToken, BASE_URL + "v1/projects/" + projectId), "Deleting project");
    }

    public static void viewMyProjects(String authToken) {
        sendRequest(authToken, getRequest(authToken, BASE_URL + "v1/projects"), "Viewing all projects");
    }

    public static void editMyProjects(Scanner scanner, String authToken) {
        System.out.print("Enter project ID to edit: ");
        int projectId = scanner.nextInt();
        scanner.nextLine(); 
        
        System.out.print("Enter new project name (leave blank to keep current): ");
        String name = scanner.nextLine();
        
        if (name.isEmpty()) {
            System.out.println("No changes made.");
            return;
        }
    
        String jsonBody = String.format("{\"name\":\"%s\"}", name);
        sendRequest(authToken, patchRequest(authToken, BASE_URL + "v1/projects/" + projectId, jsonBody), "Updating project");
    }

    private static void sendRequest(String authToken, HttpRequest request, String action) {
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(action + " Response: " + response.statusCode());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static HttpRequest postRequest(String authToken, String url, String json) {
        return HttpRequest.newBuilder().uri(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
    }

    private static HttpRequest getRequest(String authToken, String url) {
        return HttpRequest.newBuilder().uri(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .GET().build();
    }

    private static HttpRequest deleteRequest(String authToken, String url) {
        return HttpRequest.newBuilder().uri(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .DELETE().build();
    }

    
    private static HttpRequest patchRequest(String authToken, String url, String json) {
        return HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
    }
}
