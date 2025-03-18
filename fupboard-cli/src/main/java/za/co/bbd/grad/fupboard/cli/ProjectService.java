package za.co.bbd.grad.fupboard.cli;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static za.co.bbd.grad.fupboard.Config.BASE_URL;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.deleteRequest;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.getRequest;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.patchRequest;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.postRequest;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.sendRequest;

public class ProjectService {

    public static void createNewProject(Scanner scanner, String authToken) {
        System.out.print(ConsoleColors.YELLOW + "Enter project name: " + ConsoleColors.RESET);
        String projectName = scanner.nextLine();

        if (projectName.isEmpty()) {
            System.out.println(ConsoleColors.RED + "Project name cannot be empty." + ConsoleColors.RESET);
            return;
        }

        String jsonBody = String.format("{\"name\":\"%s\"}", projectName);
        String responseBody = sendRequest(postRequest(authToken, BASE_URL + "/v1/projects", jsonBody));
        System.out.println(responseBody);
        if (responseBody != null) {
            System.out.println(ConsoleColors.GREEN + "-> Project created successfully!" + ConsoleColors.RESET);
            displayProject(responseBody);
        }
    }

    public static void getProjectByIndex(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(ConsoleColors.YELLOW + "Enter project number: " + ConsoleColors.RESET);
        int index = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(index);
        if (projectId == null) {
            System.out.println(ConsoleColors.RED + "Invalid selection." + ConsoleColors.RESET);
            return;
        }

        String responseBody = sendRequest(getRequest(authToken, BASE_URL + "/v1/projects/" + projectId));
        if (responseBody != null) {
            System.out.println(ConsoleColors.BLUE + "-> Project Details:" + ConsoleColors.RESET);
            displayProject(responseBody);
        }
    }

    public static void deleteProject(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(ConsoleColors.YELLOW + "Enter project number to delete: " + ConsoleColors.RESET);
        int index = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(index);
        if (projectId == null) {
            System.out.println(ConsoleColors.RED + "Invalid selection." + ConsoleColors.RESET);
            return;
        }

        String responseBody = sendRequest(deleteRequest(authToken, BASE_URL + "/v1/projects/" + projectId));

        if (responseBody != null) {
            System.out.println(ConsoleColors.GREEN + "-> Project deleted successfully!" + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.RED + "-> Failed to delete project." + ConsoleColors.RESET);
        }
    }

    public static void editMyProjects(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(ConsoleColors.YELLOW + "Enter project number to edit: " + ConsoleColors.RESET);
        int index = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(index);
        if (projectId == null) {
            System.out.println(ConsoleColors.RED + "Invalid selection." + ConsoleColors.RESET);
            return;
        }

        System.out.print(ConsoleColors.YELLOW + "Enter new project name (leave blank to keep current): " + ConsoleColors.RESET);
        String name = scanner.nextLine();

        if (name.isEmpty()) {
            System.out.println(ConsoleColors.BLUE + "-> No changes made." + ConsoleColors.RESET);
            return;
        }

        String jsonBody = String.format("{\"name\":\"%s\"}", name);
        String responseBody = sendRequest(patchRequest(authToken, BASE_URL + "/v1/projects/" + projectId, jsonBody));

        if (responseBody != null) {
            System.out.println(ConsoleColors.GREEN + "-> Project updated successfully!" + ConsoleColors.RESET);
            displayProject(responseBody);
        }
    }

    public static Map<Integer, Integer> viewMyProjects(String authToken) {
        String responseBody = sendRequest(getRequest(authToken, BASE_URL + "/v1/projects/343232"));

        if (responseBody == null) {
            System.out.println(ConsoleColors.RED + "-> Failed to retrieve projects." + ConsoleColors.RESET);
            return Collections.emptyMap();
        }

        Map<Integer, Integer> projectIndexMap = new HashMap<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode projects = objectMapper.readTree(responseBody);

            if (projects.isArray() && projects.size() > 0) {
                System.out.println(ConsoleColors.BLUE + "-> Your Projects:" + ConsoleColors.RESET);
                int index = 1;
                for (JsonNode project : projects) {
                    int projectId = project.get("projectId").asInt();
                    String projectName = project.get("projectName").asText();
                    projectIndexMap.put(index, projectId);
                    System.out.println(ConsoleColors.GREEN + " - [" + index + "] " + projectName + ConsoleColors.RESET);
                    index++;
                }
            } else {
                System.out.println(ConsoleColors.RED + "-> No projects found." + ConsoleColors.RESET);
            }
        } catch (IOException e) {
            System.err.println(ConsoleColors.RED + "Error parsing response: " + e.getMessage() + ConsoleColors.RESET);
        }
        return projectIndexMap;
    }

    private static void displayProject(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode project = objectMapper.readTree(responseBody);

            String projectName = project.get("projectName").asText();

            System.out.println(ConsoleColors.BLUE + "-> Project Info:" + ConsoleColors.RESET);
            System.out.println("   Name: " + ConsoleColors.GREEN + projectName + ConsoleColors.RESET);
            System.out.println("--------------------------");

        } catch (IOException e) {
            System.err.println(ConsoleColors.RED + "Error parsing response: " + e.getMessage() + ConsoleColors.RESET);
        }
    }
}
