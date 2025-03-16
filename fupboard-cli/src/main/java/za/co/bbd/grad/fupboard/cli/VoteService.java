package za.co.bbd.grad.fupboard.cli;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class VoteService {
    private static final String BASE_URL = "http://localhost:8080/";

    public static void createVote(Scanner scanner, String authToken) {
        System.out.print("Enter project ID: ");
        int projectId = scanner.nextInt();
        System.out.print("Enter FUp ID: ");
        int fUpId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter accused username: ");
        String accusedUsername = scanner.nextLine();
        System.out.print("Enter vote score (1-5): ");
        int score = scanner.nextInt();

        String jsonBody = String.format("{\"accusedUsername\":\"%s\", \"score\":%d}", accusedUsername, score);
        sendRequest(authToken, postRequest(authToken, BASE_URL + "v1/projects/" + projectId + "/fups/" + fUpId + "/votes", jsonBody), "Creating vote");
    }

    public static void viewVotes(Scanner scanner, String authToken) {
        System.out.print("Enter project ID: ");
        int projectId = scanner.nextInt();
        System.out.print("Enter FUp ID: ");
        int fUpId = scanner.nextInt();

        sendRequest(authToken, getRequest(authToken, BASE_URL + "v1/projects/" + projectId + "/fups/" + fUpId + "/votes"), "Viewing votes");
    }

    public static void editVote(Scanner scanner, String authToken) {
        System.out.print("Enter project ID: ");
        int projectId = scanner.nextInt();
        System.out.print("Enter FUp ID: ");
        int fUpId = scanner.nextInt();
        System.out.print("Enter Vote ID: ");
        int voteId = scanner.nextInt();
        System.out.print("Enter new vote score (1-5): ");
        int score = scanner.nextInt();

        String jsonBody = String.format("{\"score\":%d}", score);
        sendRequest(authToken, patchRequest(authToken, BASE_URL + "v1/projects/" + projectId + "/fups/" + fUpId + "/votes/" + voteId, jsonBody), "Editing vote");
    }

    public static void deleteVote(Scanner scanner, String authToken) {
        System.out.print("Enter project ID: ");
        int projectId = scanner.nextInt();
        System.out.print("Enter FUp ID: ");
        int fUpId = scanner.nextInt();
        System.out.print("Enter Vote ID: ");
        int voteId = scanner.nextInt();

        sendRequest(authToken, deleteRequest(authToken, BASE_URL + "v1/projects/" + projectId + "/fups/" + fUpId + "/votes/" + voteId), "Deleting vote");
    }



    
    private static HttpRequest getRequest(String authToken, String url) {
        return HttpRequest.newBuilder().uri(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .GET().build();
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

    private static HttpRequest patchRequest(String authToken, String url, String json) {
        return HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
    }

    private static HttpRequest deleteRequest(String authToken, String url) {
        return HttpRequest.newBuilder().uri(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .DELETE().build();
    }



    private static HttpRequest postRequest(String authToken, String url, String json) {
        return HttpRequest.newBuilder().uri(URI.create(url))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
    }
}
