package za.co.bbd.grad.fupboard.cli.services;


import java.io.IOException;
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
import static za.co.bbd.grad.fupboard.cli.services.FUpService.viewFUps;
import static za.co.bbd.grad.fupboard.cli.services.ProjectService.viewMyProjects;

public class VoteService {

    public static void createVote(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;
        
        System.out.print(Constants.YELLOW + "Enter project ID: " + Constants.RESET);
        int projectId = scanner.nextInt();
        if (!projectIndexMap.containsKey(projectId)) {
            System.out.println(Constants.RED + "Invalid project ID. Please select from the displayed list." + Constants.RESET);
            return;
        }

        Map<Integer, Integer> fUpIndexMap = viewFUps(authToken, projectIndexMap.get(projectId));
        if (fUpIndexMap.isEmpty()) return;
        
        System.out.print(Constants.YELLOW + "Enter FUp ID: " + Constants.RESET);
        int fUpId = scanner.nextInt();
        scanner.nextLine();
        if (!fUpIndexMap.containsKey(fUpId)) {
            System.out.println(Constants.RED + "Invalid FUp ID. Please select from the displayed list." + Constants.RESET);
            return;
        }
        
        System.out.print(Constants.YELLOW + "Enter accused username: " + Constants.RESET);
        String accusedUsername = scanner.nextLine().trim();
        if (accusedUsername.isEmpty()) {
            System.out.println(Constants.RED + "Accused username cannot be empty." + Constants.RESET);
            return;
        }
        
        System.out.print(Constants.YELLOW + "Enter vote score (1-5): " + Constants.RESET);
        while (!scanner.hasNextInt()) {
            System.out.println(Constants.RED + "Invalid input. Please enter a number between 1 and 5." + Constants.RESET);
            scanner.next();
        }
        int score = scanner.nextInt();
        if (score < 1 || score > 5) {
            System.out.println(Constants.RED + "Invalid score. Please enter a number between 1 and 5." + Constants.RESET);
            return;
        }

        String jsonBody = String.format("{\"accusedUsername\":\"%s\", \"score\":%d}", accusedUsername, score);
        String response = sendRequest(postRequest(Constants.BASE_URL + "/v1/projects/" + projectIndexMap.get(projectId) + "/fups/" + fUpIndexMap.get(fUpId) + "/votes", jsonBody));

        if (response != null && !response.isEmpty()) {
            System.out.println(Constants.GREEN + "Vote successfully created!" + Constants.RESET);
        } else {
            System.out.println(Constants.RED + "Failed to create vote. Please try again." + Constants.RESET);
        }
    }

    public static VoteSelection viewVotes(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return null;

        System.out.print(Constants.YELLOW + "Enter project ID: " + Constants.RESET);
        int projectId = scanner.nextInt();
        if (!projectIndexMap.containsKey(projectId)) {
            System.out.println(Constants.RED + "Invalid project ID. Please select from the displayed list." + Constants.RESET);
            return null;
        }

        Map<Integer, Integer> fUpIndexMap = viewFUps(authToken, projectId);
        if (fUpIndexMap.isEmpty()) return null;

        System.out.print(Constants.YELLOW + "Enter FUp ID: " + Constants.RESET);
        int fUpId = scanner.nextInt();
        if (!fUpIndexMap.containsKey(fUpId)) {
            System.out.println(Constants.RED + "Invalid FUp ID. Please select from the displayed list." + Constants.RESET);
            return null;
        }

        String responseBody = sendRequest(getRequest(Constants.BASE_URL + "/v1/projects/" + projectIndexMap.get(projectId) + "/fups/" + fUpIndexMap.get(fUpId) + "/votes"));
        Map<Integer, Integer> voteIndexMap = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode votesArray = objectMapper.readTree(responseBody);
            if (votesArray.isArray()) {
                int index = 1;
                for (JsonNode vote : votesArray) {
                    int voteId = vote.get("id").asInt();
                    int score = vote.get("score").asInt();
                    voteIndexMap.put(index, voteId);
                    System.out.println(Constants.GREEN + "[" + index + "] Vote ID: " + voteId + ", Score: " + score + Constants.RESET);
                    index++;
                }
            }
        } catch (IOException e) {
            System.out.println(Constants.RED + "Failed to retrieve votes." + Constants.RESET);
        }
        return new VoteSelection(projectId, fUpId, voteIndexMap);
    }

    public static void editVote(Scanner scanner, String authToken) {
        VoteSelection voteSelection = viewVotes(scanner, authToken);
        if (voteSelection == null || voteSelection.getVoteIndexMap().isEmpty()) return;

        System.out.print(Constants.YELLOW + "Enter Vote ID: " + Constants.RESET);
        int voteSelectionIndex = scanner.nextInt();
        if (!voteSelection.getVoteIndexMap().containsKey(voteSelectionIndex)) {
            System.out.println(Constants.RED + "Invalid Vote ID. Please select from the displayed list." + Constants.RESET);
            return;
        }
        int voteId = voteSelection.getVoteIndexMap().get(voteSelectionIndex);

        System.out.print(Constants.YELLOW + "Enter new vote score (1-5): " + Constants.RESET);
        int score = scanner.nextInt();
        if (score < 1 || score > 5) {
            System.out.println(Constants.RED + "Invalid score. Please enter a number between 1 and 5." + Constants.RESET);
            return;
        }

        String jsonBody = String.format("{\"score\":%d}", score);
        sendRequest(patchRequest(Constants.BASE_URL + "/v1/projects/" + voteSelection.getProjectId() + "/fups/" + voteSelection.getFUpId() + "/votes/" + voteId, jsonBody));
    }

    public static void deleteVote(Scanner scanner, String authToken) {
        VoteSelection voteSelection = viewVotes(scanner, authToken);
        if (voteSelection == null || voteSelection.getVoteIndexMap().isEmpty()) return;

        System.out.print(Constants.YELLOW + "Enter Vote ID: " + Constants.RESET);
        int voteSelectionIndex = scanner.nextInt();
        if (!voteSelection.getVoteIndexMap().containsKey(voteSelectionIndex)) {
            System.out.println(Constants.RED + "Invalid Vote ID. Please select from the displayed list." + Constants.RESET);
            return;
        }
        int voteId = voteSelection.getVoteIndexMap().get(voteSelectionIndex);

        sendRequest(deleteRequest( Constants.BASE_URL + "/v1/projects/" + voteSelection.getProjectId() + "/fups/" + voteSelection.getFUpId() + "/votes/" + voteId));
    }
    
}
