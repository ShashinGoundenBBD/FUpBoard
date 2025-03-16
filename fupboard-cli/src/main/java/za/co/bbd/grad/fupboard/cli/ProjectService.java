package za.co.bbd.grad.fupboard.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class ProjectService {
    private static final String BASE_URL = "http://localhost:8080/";

    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";

    public static void createNewProject(Scanner scanner, String authToken) {
        System.out.print(YELLOW + "Enter project name: " + RESET);
        String projectName = scanner.nextLine();

        if (projectName.isEmpty()) {
            System.out.println(RED + "Project name cannot be empty." + RESET);
            return;
        }

        String jsonBody = String.format("{\"name\":\"%s\"}", projectName);
        String responseBody = sendRequest(authToken, postRequest(authToken, BASE_URL + "v1/projects", jsonBody));

        if (responseBody != null) {
            System.out.println(GREEN + "-> Project created successfully!" + RESET);
            displayProject(responseBody);
        }
    }

    public static void getProjectByIndex(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(YELLOW + "Enter project number: " + RESET);
        int index = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(index);
        if (projectId == null) {
            System.out.println(RED + "Invalid selection." + RESET);
            return;
        }

        String responseBody = sendRequest(authToken, getRequest(authToken, BASE_URL + "v1/projects/" + projectId));
        if (responseBody != null) {
            System.out.println(BLUE + "-> Project Details:" + RESET);
            displayProject(responseBody);
        }
    }

    public static void deleteProject(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(YELLOW + "Enter project number to delete: " + RESET);
        int index = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(index);
        if (projectId == null) {
            System.out.println(RED + "Invalid selection." + RESET);
            return;
        }

        String responseBody = sendRequest(authToken, deleteRequest(authToken, BASE_URL + "v1/projects/" + projectId));

        if (responseBody != null) {
            System.out.println(GREEN + "-> Project deleted successfully!" + RESET);
        } else {
            System.out.println(RED + "-> Failed to delete project." + RESET);
        }
    }

    public static void editMyProjects(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(YELLOW + "Enter project number to edit: " + RESET);
        int index = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(index);
        if (projectId == null) {
            System.out.println(RED + "Invalid selection." + RESET);
            return;
        }

        System.out.print(YELLOW + "Enter new project name (leave blank to keep current): " + RESET);
        String name = scanner.nextLine();

        if (name.isEmpty()) {
            System.out.println(BLUE + "-> No changes made." + RESET);
            return;
        }

        String jsonBody = String.format("{\"name\":\"%s\"}", name);
        String responseBody = sendRequest(authToken, patchRequest(authToken, BASE_URL + "v1/projects/" + projectId, jsonBody));

        if (responseBody != null) {
            System.out.println(GREEN + "-> Project updated successfully!" + RESET);
            displayProject(responseBody);
        }
    }

    public static Map<Integer, Integer> viewMyProjects(String authToken) {
        String responseBody = sendRequest(authToken, getRequest(authToken, BASE_URL + "v1/projects"));

        if (responseBody == null) {
            System.out.println(RED + "-> Failed to retrieve projects." + RESET);
            return Collections.emptyMap();
        }

        Map<Integer, Integer> projectIndexMap = new HashMap<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode projects = objectMapper.readTree(responseBody);

            if (projects.isArray() && projects.size() > 0) {
                System.out.println(BLUE + "-> Your Projects:" + RESET);
                int index = 1;
                for (JsonNode project : projects) {
                    int projectId = project.get("projectId").asInt();
                    String projectName = project.get("projectName").asText();
                    projectIndexMap.put(index, projectId);
                    System.out.println(GREEN + " - [" + index + "] " + projectName + RESET);
                    index++;
                }
            } else {
                System.out.println(RED + "-> No projects found." + RESET);
            }
        } catch (IOException e) {
            System.err.println(RED + "Error parsing response: " + e.getMessage() + RESET);
        }
        return projectIndexMap;
    }

    private static String sendRequest(String authToken, HttpRequest request) {
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return response.body();
            } else {
                System.out.println(RED + "-> Request failed with status: " + response.statusCode() + RESET);
                System.out.println(response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println(RED + "Error: " + e.getMessage() + RESET);
        }
        return null;
    }

    private static void displayProject(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode project = objectMapper.readTree(responseBody);

            String projectName = project.get("projectName").asText();

            System.out.println(BLUE + "-> Project Info:" + RESET);
            System.out.println("   Name: " + GREEN + projectName + RESET);
            System.out.println("--------------------------");

        } catch (IOException e) {
            System.err.println(RED + "Error parsing response: " + e.getMessage() + RESET);
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
