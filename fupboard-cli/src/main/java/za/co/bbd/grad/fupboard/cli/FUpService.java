package za.co.bbd.grad.fupboard.cli;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.bbd.grad.fupboard.Config;
import static za.co.bbd.grad.fupboard.Config.BASE_URL;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.deleteRequest;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.getRequest;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.patchRequest;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.postRequest;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.sendRequest;


public class FUpService {

    public static void reportFUp(Scanner scanner, String authToken) {

        Map<Integer, Integer> projectIndexMap = ProjectService.viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(ConsoleColors.YELLOW + "Enter project number: " + ConsoleColors.RESET);
        int index = scanner.nextInt();
        scanner.nextLine();
        Integer projectId = projectIndexMap.get(index);

        if (projectId == null) {
            System.out.println(ConsoleColors.RED + "Invalid project selection." + ConsoleColors.RESET);
            return;
        }

        System.out.print(ConsoleColors.YELLOW + "Enter FUp name: " + ConsoleColors.RESET);
        String name = scanner.nextLine();

        if (name.isEmpty()) {
            System.out.println(ConsoleColors.RED + "FUp name cannot be empty." + ConsoleColors.RESET);
            return;
        }

        System.out.print(ConsoleColors.YELLOW + "Enter FUp description: " + ConsoleColors.RESET);
        String description = scanner.nextLine();

        if (description.isEmpty()) {
            System.out.println(ConsoleColors.RED + "Description cannot be empty." + ConsoleColors.RESET);
            return;
        }

        String jsonBody = String.format("{\"name\":\"%s\", \"description\":\"%s\"}", name, description);
        String responseBody = sendRequest(postRequest(authToken, BASE_URL + "/v1/projects/" + projectId + "/fups", jsonBody));
        if (responseBody != null) {
            System.out.println(ConsoleColors.GREEN + "-> FUp reported successfully!" + ConsoleColors.RESET);
            displayFUp(responseBody);
        }
    }

    public static Map<Integer, Integer> viewFUps(String authToken, int projectId) {
        String responseBody = sendRequest(getRequest(authToken, BASE_URL + "/v1/projects/" + projectId + "/fups"));
        if (responseBody == null) {
            System.out.println(ConsoleColors.RED + "-> Failed to retrieve FUps." + ConsoleColors.RESET);
            return Collections.emptyMap();
        }

        Map<Integer, Integer> fUpIndexMap = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode fUps = objectMapper.readTree(responseBody);

            if (fUps.isArray() && fUps.size() > 0) {
                System.out.println(ConsoleColors.BLUE + "-> FUps in Project:" + ConsoleColors.RESET);
                int index = 1;
                for (JsonNode fUp : fUps) {
                    int fUpId = fUp.get("fUpId").asInt();
                    String fUpName = fUp.get("fUpName").asText();
                    fUpIndexMap.put(index, fUpId);
                    System.out.println(ConsoleColors.GREEN + " - [" + index + "] " + fUpName + ConsoleColors.RESET);
                    index++;
                }
            } else {
                System.out.println(ConsoleColors.RED + "-> No FUps found for this project." + ConsoleColors.RESET);
            }
        } catch (IOException e) {
            System.err.println(ConsoleColors.RED + "Error parsing response: " + e.getMessage() + ConsoleColors.RESET);
        }
        return fUpIndexMap;
    }

    public static void viewFUp(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = ProjectService.viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(ConsoleColors.YELLOW + "Enter project number: " + ConsoleColors.RESET);
        int projectIndex = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(projectIndex);
        if (projectId == null) {
            System.out.println(ConsoleColors.RED + "Invalid project selection." + ConsoleColors.RESET);
            return;
        }

        Map<Integer, Integer> fUpIndexMap = viewFUps(authToken, projectId);
        if (fUpIndexMap.isEmpty()) return;

        System.out.print(ConsoleColors.YELLOW + "Enter FUp number: " + ConsoleColors.RESET);
        int fUpIndex = scanner.nextInt();
        scanner.nextLine();

        Integer fUpId = fUpIndexMap.get(fUpIndex);
        if (fUpId == null) {
            System.out.println(ConsoleColors.RED + "Invalid FUp selection." + ConsoleColors.RESET);
            return;
        }

        String responseBody = sendRequest(getRequest(authToken, BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId));
        if (responseBody != null) {
            System.out.println(ConsoleColors.BLUE + "-> FUp Details:" + ConsoleColors.RESET);
            displayFUp(responseBody);
        }
    }


    public static void deleteFUp(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = ProjectService.viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(ConsoleColors.YELLOW + "Enter project number: " + ConsoleColors.RESET);
        int projectIndex = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(projectIndex);
        if (projectId == null) {
            System.out.println(ConsoleColors.RED + "Invalid project selection." + ConsoleColors.RESET);
            return;
        }

        Map<Integer, Integer> fUpIndexMap = viewFUps(authToken, projectId);
        if (fUpIndexMap.isEmpty()) return;

        System.out.print(ConsoleColors.YELLOW + "Enter FUp number to delete: " + ConsoleColors.RESET);
        int fUpIndex = scanner.nextInt();
        scanner.nextLine();

        Integer fUpId = fUpIndexMap.get(fUpIndex);
        if (fUpId == null) {
            System.out.println(ConsoleColors.RED + "Invalid FUp selection." + ConsoleColors.RESET);
            return;
        }

        sendRequest(deleteRequest(authToken, BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId));
        System.out.println(ConsoleColors.GREEN + "-> FUp deleted successfully!" + ConsoleColors.RESET);
    }

    public static void editFUp(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = ProjectService.viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(ConsoleColors.YELLOW + "Enter project number: " + ConsoleColors.RESET);
        int projectIndex = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(projectIndex);
        if (projectId == null) {
            System.out.println(ConsoleColors.RED + "Invalid project selection." + ConsoleColors.RESET);
            return;
        }

        Map<Integer, Integer> fUpIndexMap = viewFUps(authToken, projectId);
        if (fUpIndexMap.isEmpty()) return;

        System.out.print(ConsoleColors.YELLOW + "Enter FUp number to edit: " + ConsoleColors.RESET);
        int fUpIndex = scanner.nextInt();
        scanner.nextLine();

        Integer fUpId = fUpIndexMap.get(fUpIndex);
        if (fUpId == null) {
            System.out.println(ConsoleColors.RED + "Invalid FUp selection." + ConsoleColors.RESET);
            return;
        }

        System.out.print(ConsoleColors.YELLOW + "Enter new FUp name (leave blank to keep current): " + ConsoleColors.RESET);
        String name = scanner.nextLine();

        System.out.print(ConsoleColors.YELLOW + "Enter new FUp description (leave blank to keep current): " + ConsoleColors.RESET);
        String description = scanner.nextLine();

        if (name.isEmpty() && description.isEmpty()) {
            System.out.println(ConsoleColors.BLUE + "-> No changes made." + ConsoleColors.RESET);
            return;
        }

        String jsonBody = String.format("{\"name\":\"%s\", \"description\":\"%s\"}", name, description);
        String responseBody = sendRequest(patchRequest(authToken, BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId, jsonBody));

        if (responseBody != null) {
            System.out.println(ConsoleColors.GREEN + "-> FUp updated successfully!" + ConsoleColors.RESET);
            displayFUp(responseBody);
        }
    }

    private static void displayFUp(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode fUp = objectMapper.readTree(responseBody);

            String fUpName = fUp.get("fUpName").asText();
            String fUpDescription = fUp.get("description").asText();

            System.out.println(ConsoleColors.BLUE + "-> FUp Info:" + ConsoleColors.RESET);
            System.out.println("   Name: " + ConsoleColors.GREEN + fUpName + ConsoleColors.RESET);
            System.out.println("   Description: " + ConsoleColors.GREEN + fUpDescription + ConsoleColors.RESET);
            System.out.println("--------------------------");

        } catch (IOException e) {
            System.err.println(ConsoleColors.RED + "Error parsing response: " + e.getMessage() + ConsoleColors.RESET);
        }
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

