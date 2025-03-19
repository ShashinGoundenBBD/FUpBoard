package za.co.bbd.grad.fupboard.cli.services;

import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.deleteRequest;
import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.getRequest;
import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.patchRequest;
import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.postRequest;
import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.sendRequest;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.HttpUtil;


public class FUpService {

    public static void reportFUp(Scanner scanner, String authToken) {

        Map<Integer, Integer> projectIndexMap = ProjectService.viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(Constants.YELLOW + "Enter project number: " + Constants.RESET);
        int index = scanner.nextInt();
        scanner.nextLine();
        Integer projectId = projectIndexMap.get(index);

        if (projectId == null) {
            System.out.println(Constants.RED + "Invalid project selection." + Constants.RESET);
            return;
        }

        System.out.print(Constants.YELLOW + "Enter FUp name: " + Constants.RESET);
        String name = scanner.nextLine();

        if (name.isEmpty()) {
            System.out.println(Constants.RED + "FUp name cannot be empty." + Constants.RESET);
            return;
        }

        System.out.print(Constants.YELLOW + "Enter FUp description: " + Constants.RESET);
        String description = scanner.nextLine();

        if (description.isEmpty()) {
            System.out.println(Constants.RED + "Description cannot be empty." + Constants.RESET);
            return;
        }

        String jsonBody = String.format("{\"name\":\"%s\", \"description\":\"%s\"}", name, description);
        String responseBody = sendRequest(postRequest(Constants.BASE_URL + "/v1/projects/" + projectId + "/fups", jsonBody));
        if (responseBody != null) {
            System.out.println(Constants.GREEN + "-> FUp reported successfully!" + Constants.RESET);
            displayFUp(responseBody);
        }
    }

    public static Map<Integer, Integer> viewFUps(String authToken, int projectId) {
        String responseBody = sendRequest(getRequest(Constants.BASE_URL + "/v1/projects/" + projectId + "/fups"));
        if (responseBody == null) {
            System.out.println(Constants.RED + "-> Failed to retrieve FUps." + Constants.RESET);
            return Collections.emptyMap();
        }

        Map<Integer, Integer> fUpIndexMap = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode fUps = objectMapper.readTree(responseBody);

            if (fUps.isArray() && fUps.size() > 0) {
                System.out.println(Constants.BLUE + "-> FUps in Project:" + Constants.RESET);
                int index = 1;
                for (JsonNode fUp : fUps) {
                    int fUpId = fUp.get("fUpId").asInt();
                    String fUpName = fUp.get("fUpName").asText();
                    fUpIndexMap.put(index, fUpId);
                    System.out.println(Constants.GREEN + " - [" + index + "] " + fUpName + Constants.RESET);
                    index++;
                }
            } else {
                System.out.println(Constants.RED + "-> No FUps found for this project." + Constants.RESET);
            }
        } catch (IOException e) {
            System.err.println(Constants.RED + "Error parsing response: " + e.getMessage() + Constants.RESET);
        }
        return fUpIndexMap;
    }

    public static void viewFUp(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = ProjectService.viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(Constants.YELLOW + "Enter project number: " + Constants.RESET);
        int projectIndex = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(projectIndex);
        if (projectId == null) {
            System.out.println(Constants.RED + "Invalid project selection." + Constants.RESET);
            return;
        }

        Map<Integer, Integer> fUpIndexMap = viewFUps(authToken, projectId);
        if (fUpIndexMap.isEmpty()) return;

        System.out.print(Constants.YELLOW + "Enter FUp number: " + Constants.RESET);
        int fUpIndex = scanner.nextInt();
        scanner.nextLine();

        Integer fUpId = fUpIndexMap.get(fUpIndex);
        if (fUpId == null) {
            System.out.println(Constants.RED + "Invalid FUp selection." + Constants.RESET);
            return;
        }

        String responseBody = sendRequest(getRequest(Constants.BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId));
        if (responseBody != null) {
            System.out.println(Constants.BLUE + "-> FUp Details:" + Constants.RESET);
            displayFUp(responseBody);
        }
    }


    public static void deleteFUp(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = ProjectService.viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(Constants.YELLOW + "Enter project number: " + Constants.RESET);
        int projectIndex = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(projectIndex);
        if (projectId == null) {
            System.out.println(Constants.RED + "Invalid project selection." + Constants.RESET);
            return;
        }

        Map<Integer, Integer> fUpIndexMap = viewFUps(authToken, projectId);
        if (fUpIndexMap.isEmpty()) return;

        System.out.print(Constants.YELLOW + "Enter FUp number to delete: " + Constants.RESET);
        int fUpIndex = scanner.nextInt();
        scanner.nextLine();

        Integer fUpId = fUpIndexMap.get(fUpIndex);
        if (fUpId == null) {
            System.out.println(Constants.RED + "Invalid FUp selection." + Constants.RESET);
            return;
        }

        sendRequest(deleteRequest( Constants.BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId));
        System.out.println(Constants.GREEN + "-> FUp deleted successfully!" + Constants.RESET);
    }

    public static void editFUp(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = ProjectService.viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(Constants.YELLOW + "Enter project number: " + Constants.RESET);
        int projectIndex = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(projectIndex);
        if (projectId == null) {
            System.out.println(Constants.RED + "Invalid project selection." + Constants.RESET);
            return;
        }

        Map<Integer, Integer> fUpIndexMap = viewFUps(authToken, projectId);
        if (fUpIndexMap.isEmpty()) return;

        System.out.print(Constants.YELLOW + "Enter FUp number to edit: " + Constants.RESET);
        int fUpIndex = scanner.nextInt();
        scanner.nextLine();

        Integer fUpId = fUpIndexMap.get(fUpIndex);
        if (fUpId == null) {
            System.out.println(Constants.RED + "Invalid FUp selection." + Constants.RESET);
            return;
        }

        System.out.print(Constants.YELLOW + "Enter new FUp name (leave blank to keep current): " + Constants.RESET);
        String name = scanner.nextLine();

        System.out.print(Constants.YELLOW + "Enter new FUp description (leave blank to keep current): " + Constants.RESET);
        String description = scanner.nextLine();

        if (name.isEmpty() && description.isEmpty()) {
            System.out.println(Constants.BLUE + "-> No changes made." + Constants.RESET);
            return;
        }

        String jsonBody = String.format("{\"name\":\"%s\", \"description\":\"%s\"}", name, description);
        String responseBody = sendRequest(patchRequest(Constants.BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId, jsonBody));

        if (responseBody != null) {
            System.out.println(Constants.GREEN + "-> FUp updated successfully!" + Constants.RESET);
            displayFUp(responseBody);
        }
    }

    private static void displayFUp(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode fUp = objectMapper.readTree(responseBody);

            String fUpName = fUp.get("fUpName").asText();
            String fUpDescription = fUp.get("description").asText();

            System.out.println(Constants.BLUE + "-> FUp Info:" + Constants.RESET);
            System.out.println("   Name: " + Constants.GREEN + fUpName + Constants.RESET);
            System.out.println("   Description: " + Constants.GREEN + fUpDescription + Constants.RESET);
            System.out.println("--------------------------");

        } catch (IOException e) {
            System.err.println(Constants.RED + "Error parsing response: " + e.getMessage() + Constants.RESET);
        }
    }

    public static void getFUpSummary(Scanner scanner, String authToken) {
        // Show projects and their IDs
        ProjectService.viewMyProjects(authToken);

        // Ask user to pick a project ID
        System.out.print(Constants.YELLOW + "Enter project number of f-up you want to view: " + Constants.RESET);
        int projectId = scanner.nextInt();
        scanner.nextLine();
        

        //Show fups and their ids
        System.out.print(Constants.YELLOW + "Enter fUp number to view summary of it: " + Constants.RESET);
        int fUpId = scanner.nextInt();
        scanner.nextLine();
         
        HttpRequest request = HttpUtil.getRequest(Constants.BASE_URL + "/v1/projects/" + projectId + "/fups/" + fUpId + "/leaderboard");
        String response = HttpUtil.sendRequest(request);

        if (response == null) {
            System.out.println(Constants.RED + "Failed to retrieve summary." + Constants.RESET);
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
                System.out.println(Constants.RED + "No summary data available." + Constants.RESET);
                return;
            }
    
            String reset = Constants.RESET;
            String blue = Constants.BLUE;
    
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
            System.err.println(Constants.RED + "Error parsing response: " + e.getMessage() + Constants.RESET);
        }
    }
    
    
}

