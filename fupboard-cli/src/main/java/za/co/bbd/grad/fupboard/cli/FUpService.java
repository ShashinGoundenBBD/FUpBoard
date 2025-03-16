package za.co.bbd.grad.fupboard.cli;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class FUpService {
    private static final String BASE_URL = "http://localhost:8080/";

    public static void reportFUp(Scanner scanner, String authToken) {
        System.out.println("Enter FUp name:");
        String name = scanner.nextLine();

        System.out.println("Enter FUp description:");
        String description = scanner.nextLine();

        System.out.println("Enter project id:");
        int projectId = scanner.nextInt();

        String jsonBody = String.format("{\"name\":\"%s\", \"description\":\"%s\"}", name, description);
        sendRequest(authToken, postRequest(authToken, BASE_URL + "v1/projects/" + projectId + "/fups", jsonBody), "Reporting FUp");
    }

    public static void viewFUp(Scanner scanner, String authToken)
    {
        System.out.println("Enter project id to view FUps for:");
        int fUpId = scanner.nextInt();

        System.out.println("Enter project id:");
        int projectId = scanner.nextInt();

        sendRequest(authToken, getRequest(authToken, BASE_URL + "v1/projects/" + projectId + "/fups/" + fUpId), "Getting FUps");
    }

    public static void deleteFUp(Scanner scanner, String authToken) {
        System.out.println("Enter FUp ID to delete:");
        int fUpId = scanner.nextInt();

        System.out.println("Enter project id:");
        int projectId = scanner.nextInt();

        sendRequest(authToken, deleteRequest(authToken, BASE_URL + "v1/projects/" + projectId + "/fups/" + fUpId), "Deleting FUp");
    }

    public static void editFUp(Scanner scanner, String authToken) {
        System.out.println("Enter FUp ID to edit:");
        int fUpId = scanner.nextInt();
        scanner.nextLine(); 
        
        System.out.println("Enter new FUp name (leave blank to keep current):");
        String name = scanner.nextLine();
        
        System.out.println("Enter new FUp description (leave blank to keep current):");
        String description = scanner.nextLine();

        System.out.println("Enter project id:");
        int projectId = scanner.nextInt();
        
        String jsonBody = String.format("{\"name\":\"%s\", \"description\":\"%s\"}", name, description);
        sendRequest(authToken, patchRequest(authToken, BASE_URL + "v1/projects/" + projectId + "/fups/" + fUpId, jsonBody), "Editing FUp");
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
}
