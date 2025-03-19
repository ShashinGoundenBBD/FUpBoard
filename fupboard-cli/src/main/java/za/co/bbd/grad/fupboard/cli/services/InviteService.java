package za.co.bbd.grad.fupboard.cli.services;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.bbd.grad.fupboard.cli.common.Constants;

import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.deleteRequest;
import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.getRequest;
import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.patchRequest;
import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.postRequest;
import static za.co.bbd.grad.fupboard.cli.common.HttpUtil.sendRequest;
import static za.co.bbd.grad.fupboard.cli.services.ProjectService.viewMyProjects;

public class InviteService {

    public static void acceptOrDeclineInvite(Scanner scanner, String authToken) {

        Map<Integer, Integer> inviteIndexMap = viewMyInvites(authToken);
        if (inviteIndexMap.isEmpty()) return;
        System.out.print("Enter invite ID: ");
        int inviteId = scanner.nextInt();
        scanner.nextLine();

        System.out.print(Constants.YELLOW + "Accept invite? (yes/no): " + Constants.RESET);
        String response = scanner.nextLine().trim().toLowerCase();
        if (!response.equals("yes") && !response.equals("no")) {
            System.out.println(Constants.RED + "Invalid input. Please enter 'yes' or 'no'." + Constants.RESET);
            return;
        }
        boolean accepted = response.equals("yes");
    
        String jsonBody = String.format("{\"accepted\":%b}", accepted);
        String apiResponse = sendRequest(patchRequest(Constants.BASE_URL + "/v1/invites/" + inviteId, jsonBody));
        if (apiResponse != null) {
            System.out.println(Constants.GREEN + "Invite " + (accepted ? "accepted" : "declined") + " successfully." + Constants.RESET);
        } else {
            System.out.println(Constants.RED + "Failed to process invite. Please try again." + Constants.RESET);
        }
    }

    public static Map<Integer, Integer> viewProjectInvites(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return Collections.emptyMap();

        System.out.print(Constants.YELLOW + "Enter project ID: " + Constants.RESET);
        int projectId = scanner.nextInt();
        scanner.nextLine();

        String responseBody = sendRequest(getRequest(Constants.BASE_URL + "/v1/projects/" + projectId + "/invites"));
        if (responseBody == null) {
            System.out.println(Constants.RED + "Failed to retrieve invites for the project. Please try again." + Constants.RESET);
            return Collections.emptyMap();
        }

        Map<Integer, Integer> inviteIndexMap = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode invites = objectMapper.readTree(responseBody);

            if (invites.isArray() && invites.size() > 0) {
                System.out.println(Constants.BLUE + "-> Invites for Project ID: " + projectId + Constants.RESET);
                int index = 1;
                for (JsonNode invite : invites) {
                    int inviteId = invite.get("inviteId").asInt();
                    String username = invite.get("username").asText();
                    inviteIndexMap.put(index, inviteId);
                    System.out.println(Constants.GREEN + " - [" + index + "] " + username + Constants.RESET);
                    index++;
                }
            } else {
                System.out.println(Constants.RED + "-> No invites found for this project." + Constants.RESET);
            }
        } catch (IOException e) {
            System.err.println(Constants.RED + "Error parsing response: " + e.getMessage() + Constants.RESET);
        }
        return inviteIndexMap;
    }

    public static void createProjectInvite(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;
        System.out.print(Constants.YELLOW + "Enter project ID: " + Constants.RESET);
        int projectId = scanner.nextInt();
        scanner.nextLine();

        System.out.print(Constants.YELLOW + "Enter invitee's username: " + Constants.RESET);
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println(Constants.RED + "Username cannot be empty." + Constants.RESET);
            return;
        }

        String jsonBody = String.format("{\"username\":\"%s\"}", username);
        String response = sendRequest(postRequest(Constants.BASE_URL + "/v1/projects/" + projectId + "/invites", jsonBody));
        if (response != null) {
            System.out.println(Constants.GREEN + "Project invite created successfully." + Constants.RESET);
        } else {
            System.out.println(Constants.RED + "Failed to create project invite. Please try again." + Constants.RESET);
        }
    }

    public static void deleteProjectInvite(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;
        System.out.print(Constants.YELLOW + "Enter project ID: " + Constants.RESET);

        int projectId = scanner.nextInt();
        System.out.print(Constants.YELLOW + "Enter invite ID: " + Constants.RESET);
        int inviteId = scanner.nextInt();

        sendRequest(deleteRequest( Constants.BASE_URL + "/v1/projects/" + projectId + "/invites/" + inviteId));
    }

        public static Map<Integer, Integer> viewMyInvites(String authToken) {
        String responseBody = sendRequest(getRequest(Constants.BASE_URL + "/v1/users/me"));
        if (responseBody == null) {
            System.out.println(Constants.RED + "Failed to retrieve your invites. Please try again." + Constants.RESET);
            return Collections.emptyMap();
        }

        Map<Integer, Integer> inviteIndexMap = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode invites = objectMapper.readTree(responseBody);

            if (invites.isArray() && invites.size() > 0) {
                System.out.println(Constants.BLUE + "-> Your Invites:" + Constants.RESET);
                int index = 1;
                for (JsonNode invite : invites) {
                    int inviteId = invite.get("inviteId").asInt();
                    String projectName = invite.get("projectName").asText();
                    inviteIndexMap.put(index, inviteId);
                    System.out.println(Constants.GREEN + " - [" + index + "] " + projectName + Constants.RESET);
                    index++;
                }
            } else {
                System.out.println(Constants.RED + "-> No invites found." + Constants.RESET);
            }
        } catch (IOException e) {
            System.err.println(Constants.RED + "Error parsing response: " + e.getMessage() + Constants.RESET);
        }
        return inviteIndexMap;
    }
}
