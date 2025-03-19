package za.co.bbd.grad.fupboard.cli.services;

import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.deleteRequest;
import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.getRequest;
import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.patchRequest;
import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.postRequest;
import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.sendRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.bbd.grad.fupboard.cli.common.Constants;

public class ProjectService {

    public static void createNewProject(Scanner scanner) {
        System.out.print(Constants.YELLOW + "Enter project name: " + Constants.RESET);
        String projectName = scanner.nextLine();

        if (projectName.isEmpty()) {
            System.out.println(Constants.RED + "Project name cannot be empty." + Constants.RESET);
            return;
        }

        String jsonBody = String.format("{\"name\":\"%s\"}", projectName);
        String responseBody = sendRequest(postRequest(Constants.BASE_URL + "/v1/projects", jsonBody));

        if (responseBody != null) {
            System.out.println(Constants.GREEN + "-> Project created successfully!" + Constants.RESET);
            displayProject(responseBody);
        }
    }

    public static void getProjectByIndex(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(Constants.YELLOW + "Enter project number: " + Constants.RESET);
        int index = scanner.nextInt();
        scanner.nextLine();
        

        Integer projectId = projectIndexMap.get(index);
        if (projectId == null) {
            System.out.println(Constants.RED + "Invalid selection." + Constants.RESET);
            return;
        }

        String responseBody = sendRequest(getRequest(Constants.BASE_URL + "/v1/projects/" + projectId));
        if (responseBody != null) {
            System.out.println(Constants.BLUE + "-> Project Details:" + Constants.RESET);
            displayProject(responseBody);
        }
    }

    public static void deleteProject(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(Constants.YELLOW + "Enter project number to delete: " + Constants.RESET);
        int index = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(index);
        if (projectId == null) {
            System.out.println(Constants.RED + "Invalid selection." + Constants.RESET);
            return;
        }

        String responseBody = sendRequest(deleteRequest( Constants.BASE_URL + "/v1/projects/" + projectId));

        if (responseBody != null) {
            System.out.println(Constants.GREEN + "-> Project deleted successfully!" + Constants.RESET);
        } else {
            System.out.println(Constants.RED + "-> Failed to delete project." + Constants.RESET);
        }
    }

    public static void editMyProjects(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;

        System.out.print(Constants.YELLOW + "Enter project number to edit: " + Constants.RESET);
        int index = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(index);
        if (projectId == null) {
            System.out.println(Constants.RED + "Invalid selection." + Constants.RESET);
            return;
        }

        System.out.print(Constants.YELLOW + "Enter new project name (leave blank to keep current): " + Constants.RESET);
        String name = scanner.nextLine();

        if (name.isEmpty()) {
            System.out.println(Constants.BLUE + "-> No changes made." + Constants.RESET);
            return;
        }

        String jsonBody = String.format("{\"name\":\"%s\"}", name);
        String responseBody = sendRequest(patchRequest(Constants.BASE_URL + "/v1/projects/" + projectId, jsonBody));

        if (responseBody != null) {
            System.out.println(Constants.GREEN + "-> Project updated successfully!" + Constants.RESET);
            displayProject(responseBody);
        }
    }

    public static Map<Integer, Integer> viewMyProjects(String authToken) {
         String responseBody = sendRequest(getRequest(Constants.BASE_URL + "/v1/projects"));

        if (responseBody == null) {
            System.out.println(Constants.RED + "-> Failed to retrieve projects." + Constants.RESET);
            return Collections.emptyMap();
        }

        Map<Integer, Integer> projectIndexMap = new HashMap<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode projects = objectMapper.readTree(responseBody);

            if (projects.isArray() && projects.size() > 0) {
                System.out.println(Constants.BLUE + "-> Your Projects:" + Constants.RESET);
                int index = 1;
                for (JsonNode project : projects) {
                    int projectId = project.get("projectId").asInt();
                    String projectName = project.get("projectName").asText();
                    projectIndexMap.put(index, projectId);
                    System.out.println(Constants.GREEN + " - [" + index + "] " + projectName + Constants.RESET);
                    index++;
                }
            } else {
                System.out.println(Constants.RED + "-> No projects found." + Constants.RESET);
            }
        } catch (IOException e) {
            System.err.println(Constants.RED + "Error parsing response: " + e.getMessage() + Constants.RESET);
        }
        return projectIndexMap;
    }

    private static void displayProject(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode project = objectMapper.readTree(responseBody);

            String projectName = project.get("projectName").asText();

            System.out.println(Constants.BLUE + "-> Project Info:" + Constants.RESET);
            System.out.println("   Name: " + Constants.GREEN + projectName + Constants.RESET);
            System.out.println("--------------------------");

        } catch (IOException e) {
            System.err.println(Constants.RED + "Error parsing response: " + e.getMessage() + Constants.RESET);
        }
    }
}
