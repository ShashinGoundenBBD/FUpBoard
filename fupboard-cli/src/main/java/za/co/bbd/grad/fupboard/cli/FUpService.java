package za.co.bbd.grad.fupboard.cli;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.bbd.grad.fupboard.Config;

public class FUpService {
    private static final String BASE_URL = "http://ec2-13-245-83-65.af-south-1.compute.amazonaws.com/";

        // ANSI color codes
        private static final String RESET = "\u001B[0m";
        private static final String GREEN = "\u001B[32m";
        private static final String RED = "\u001B[31m";
        private static final String YELLOW = "\u001B[33m";
        private static final String BLUE = "\u001B[34m";

    public static void reportFUp(Scanner scanner, String authToken) {
        System.out.println(YELLOW + "Enter FUp name:" + RESET);
        String name = scanner.nextLine();

        if (name.isEmpty()) {
            System.out.println(RED + "FUp name cannot be empty." + RESET);
        }

        System.out.println(YELLOW + "Enter FUp description:" + RESET);
        String description = scanner.nextLine();

        if (description.isEmpty()) {
            System.out.println(RED + "Description cannot be empty." + RESET);
        }

        System.out.println(YELLOW + "Enter project id:" + RESET);
        int projectId = scanner.nextInt();

        
        if (projectId <= 0) {
            System.out.println(RED + "Project id cannot be empty." + RESET);
        }

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

    public static void getFUpSummary(Scanner scanner, String authToken) {
        // Show projects and their IDs
        ProjectService.viewMyProjects(authToken);

        // Ask user to pick a project ID
        System.out.print(ConsoleColors.YELLOW + "Enter project number of f-up you want to view: " + ConsoleColors.RESET);
        int projectId = scanner.nextInt();
        scanner.nextLine();
        

        //Show fups and their ids
        System.out.print(ConsoleColors.YELLOW + "Enter fUp number to view summary of it: " + ConsoleColors.RESET);
        int fUpId = scanner.nextInt();
        scanner.nextLine();
         
        HttpRequest request = HttpUtil.getRequest(authToken, Config.BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId + "/leaderboard");
        String response = HttpUtil.sendRequest(request);

        if (response == null) {
            System.out.println(ConsoleColors.RED + "Failed to retrieve summary." + ConsoleColors.RESET);
            return;
        }
        else
        {
            displayFUpSummary(response);
        }
    }

    public static void displayFUpSummary(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode entriesNode = root.get("entries");
    
            if (entriesNode == null || !entriesNode.isArray() || entriesNode.isEmpty()) {
                System.out.println(ConsoleColors.RED + "No summary data available." + ConsoleColors.RESET);
                return;
            }
    
            String reset = ConsoleColors.RESET;
            String blue = ConsoleColors.BLUE;
    
            String borderTop = "┌───────────────────────────┬──────────┐";
            String separator = "├───────────────────────────┼──────────┤";
            String borderBottom = "└───────────────────────────┴──────────┘";
    
            System.out.println(borderTop);
            System.out.printf("│" + blue + " %-25s " + reset +  "│" + blue + " %-8s " + reset + "│\n", "Reporter", "Score");
            System.out.println(separator);
    
            for (JsonNode entry : entriesNode) {
                String username = entry.get("username").asText();
                double score = entry.get("score").asDouble();
    
                System.out.printf("│ %-25s │ %-8.1f │\n", username, score);
            }
    
            System.out.println(borderBottom);
        } catch (IOException e) {
            System.err.println(ConsoleColors.RED + "Error parsing response: " + e.getMessage() + ConsoleColors.RESET);
        }
    }
    
    
}
