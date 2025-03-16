package za.co.bbd.grad.fupboard.cli;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class InviteService {
    private static final String BASE_URL = "http://localhost:8080/";

    public static void acceptOrDeclineInvite(Scanner scanner, String authToken) {
        System.out.print("Enter project ID: ");
        int projectId = scanner.nextInt();
        System.out.print("Enter invite ID: ");
        int inviteId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Accept invite? (yes/no): ");
        boolean accepted = scanner.nextLine().equalsIgnoreCase("yes");
    
        String jsonBody = String.format("{\"accepted\":%b}", accepted);
        sendRequest(authToken, patchRequest(authToken, BASE_URL + "v1/projects/" + projectId + "/invites/" + inviteId, jsonBody), "Updating invite");
    }

    public static void viewProjectInvites(Scanner scanner, String authToken) {
        System.out.print("Enter project ID: ");
        int projectId = scanner.nextInt();
        sendRequest(authToken, getRequest(authToken, BASE_URL + "v1/projects/" + projectId + "/invites"), "Viewing project invites");
    }

    public static void createProjectInvite(Scanner scanner, String authToken) {
        System.out.print("Enter project ID: ");
        int projectId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter invitee's username: ");
        String username = scanner.nextLine();

        String jsonBody = String.format("{\"username\":\"%s\"}", username);
        sendRequest(authToken, postRequest(authToken, BASE_URL + "v1/projects/" + projectId + "/invites", jsonBody), "Creating project invite");
    }

    public static void deleteProjectInvite(Scanner scanner, String authToken) {
        System.out.print("Enter project ID: ");
        int projectId = scanner.nextInt();
        System.out.print("Enter invite ID: ");
        int inviteId = scanner.nextInt();

        sendRequest(authToken, deleteRequest(authToken, BASE_URL + "v1/projects/" + projectId + "/invites/" + inviteId), "Deleting project invite");
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
