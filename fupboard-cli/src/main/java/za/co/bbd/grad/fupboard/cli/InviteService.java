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
import static za.co.bbd.grad.fupboard.cli.ProjectService.viewMyProjects;

public class InviteService {

    public static void acceptOrDeclineInvite(Scanner scanner, String authToken) {

        Map<Integer, Integer> inviteIndexMap = viewMyInvites(authToken);
        if (inviteIndexMap.isEmpty()) return;
        System.out.print("Enter invite ID: ");
        int inviteId = scanner.nextInt();
        scanner.nextLine();

        System.out.print(ConsoleColors.YELLOW + "Accept invite? (yes/no): " + ConsoleColors.RESET);
        String response = scanner.nextLine().trim().toLowerCase();
        if (!response.equals("yes") && !response.equals("no")) {
            System.out.println(ConsoleColors.RED + "Invalid input. Please enter 'yes' or 'no'." + ConsoleColors.RESET);
            return;
        }
        boolean accepted = response.equals("yes");
    
        String jsonBody = String.format("{\"accepted\":%b}", accepted);
        String apiResponse = sendRequest(patchRequest(BASE_URL + "/v1/invites/" + inviteId, jsonBody));
        if (apiResponse != null) {
            System.out.println(ConsoleColors.GREEN + "Invite " + (accepted ? "accepted" : "declined") + " successfully." + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.RED + "Failed to process invite. Please try again." + ConsoleColors.RESET);
        }
    }

    public static Map<Integer, Integer> viewProjectInvites(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return Collections.emptyMap();

        System.out.print(ConsoleColors.YELLOW + "Enter project ID: " + ConsoleColors.RESET);
        int projectId = scanner.nextInt();
        scanner.nextLine();

        String responseBody = sendRequest(getRequest(BASE_URL + "/v1/projects/" + projectId + "/invites"));
        if (responseBody == null) {
            System.out.println(ConsoleColors.RED + "Failed to retrieve invites for the project. Please try again." + ConsoleColors.RESET);
            return Collections.emptyMap();
        }

        Map<Integer, Integer> inviteIndexMap = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode invites = objectMapper.readTree(responseBody);

            if (invites.isArray() && invites.size() > 0) {
                System.out.println(ConsoleColors.BLUE + "-> Invites for Project ID: " + projectId + ConsoleColors.RESET);
                int index = 1;
                for (JsonNode invite : invites) {
                    int inviteId = invite.get("inviteId").asInt();
                    String username = invite.get("username").asText();
                    inviteIndexMap.put(index, inviteId);
                    System.out.println(ConsoleColors.GREEN + " - [" + index + "] " + username + ConsoleColors.RESET);
                    index++;
                }
            } else {
                System.out.println(ConsoleColors.RED + "-> No invites found for this project." + ConsoleColors.RESET);
            }
        } catch (IOException e) {
            System.err.println(ConsoleColors.RED + "Error parsing response: " + e.getMessage() + ConsoleColors.RESET);
        }
        return inviteIndexMap;
    }

    public static void createProjectInvite(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;
        System.out.print(ConsoleColors.YELLOW + "Enter project ID: " + ConsoleColors.RESET);
        int projectId = scanner.nextInt();
        scanner.nextLine();

        System.out.print(ConsoleColors.YELLOW + "Enter invitee's username: " + ConsoleColors.RESET);
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println(ConsoleColors.RED + "Username cannot be empty." + ConsoleColors.RESET);
            return;
        }

        String jsonBody = String.format("{\"username\":\"%s\"}", username);
        String response = sendRequest(postRequest(BASE_URL + "/v1/projects/" + projectId + "/invites", jsonBody));
        if (response != null) {
            System.out.println(ConsoleColors.GREEN + "Project invite created successfully." + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.RED + "Failed to create project invite. Please try again." + ConsoleColors.RESET);
        }
    }

    public static void deleteProjectInvite(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;
        System.out.print(ConsoleColors.YELLOW + "Enter project ID: " + ConsoleColors.RESET);

        int projectId = scanner.nextInt();
        System.out.print(ConsoleColors.YELLOW + "Enter invite ID: " + ConsoleColors.RESET);
        int inviteId = scanner.nextInt();

        sendRequest(deleteRequest( BASE_URL + "/v1/projects/" + projectId + "/invites/" + inviteId));
    }

        public static Map<Integer, Integer> viewMyInvites(String authToken) {
        String responseBody = sendRequest(getRequest(BASE_URL + "/v1/users/me"));
        if (responseBody == null) {
            System.out.println(ConsoleColors.RED + "Failed to retrieve your invites. Please try again." + ConsoleColors.RESET);
            return Collections.emptyMap();
        }

        Map<Integer, Integer> inviteIndexMap = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode invites = objectMapper.readTree(responseBody);

            if (invites.isArray() && invites.size() > 0) {
                System.out.println(ConsoleColors.BLUE + "-> Your Invites:" + ConsoleColors.RESET);
                int index = 1;
                for (JsonNode invite : invites) {
                    int inviteId = invite.get("inviteId").asInt();
                    String projectName = invite.get("projectName").asText();
                    inviteIndexMap.put(index, inviteId);
                    System.out.println(ConsoleColors.GREEN + " - [" + index + "] " + projectName + ConsoleColors.RESET);
                    index++;
                }
            } else {
                System.out.println(ConsoleColors.RED + "-> No invites found." + ConsoleColors.RESET);
            }
        } catch (IOException e) {
            System.err.println(ConsoleColors.RED + "Error parsing response: " + e.getMessage() + ConsoleColors.RESET);
        }
        return inviteIndexMap;
    }
}
