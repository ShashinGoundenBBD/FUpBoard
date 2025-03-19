package za.co.bbd.grad.fupboard.cli;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static za.co.bbd.grad.fupboard.Config.BASE_URL;
import static za.co.bbd.grad.fupboard.cli.FUpService.viewFUps;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.deleteRequest;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.getRequest;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.patchRequest;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.postRequest;
import static za.co.bbd.grad.fupboard.cli.HttpUtil.sendRequest;
import static za.co.bbd.grad.fupboard.cli.ProjectService.viewMyProjects;

public class VoteService {

    public static void createVote(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return;
        
        System.out.print(ConsoleColors.YELLOW + "Enter project ID: " + ConsoleColors.RESET);
        int projectId = scanner.nextInt();
        if (!projectIndexMap.containsKey(projectId)) {
            System.out.println(ConsoleColors.RED + "Invalid project ID. Please select from the displayed list." + ConsoleColors.RESET);
            return;
        }

        Map<Integer, Integer> fUpIndexMap = viewFUps(authToken, projectIndexMap.get(projectId));
        if (fUpIndexMap.isEmpty()) return;
        
        System.out.print(ConsoleColors.YELLOW + "Enter FUp ID: " + ConsoleColors.RESET);
        int fUpId = scanner.nextInt();
        scanner.nextLine();
        if (!fUpIndexMap.containsKey(fUpId)) {
            System.out.println(ConsoleColors.RED + "Invalid FUp ID. Please select from the displayed list." + ConsoleColors.RESET);
            return;
        }
        
        System.out.print(ConsoleColors.YELLOW + "Enter accused username: " + ConsoleColors.RESET);
        String accusedUsername = scanner.nextLine().trim();
        if (accusedUsername.isEmpty()) {
            System.out.println(ConsoleColors.RED + "Accused username cannot be empty." + ConsoleColors.RESET);
            return;
        }
        
        System.out.print(ConsoleColors.YELLOW + "Enter vote score (1-5): " + ConsoleColors.RESET);
        while (!scanner.hasNextInt()) {
            System.out.println(ConsoleColors.RED + "Invalid input. Please enter a number between 1 and 5." + ConsoleColors.RESET);
            scanner.next();
        }
        int score = scanner.nextInt();
        if (score < 1 || score > 5) {
            System.out.println(ConsoleColors.RED + "Invalid score. Please enter a number between 1 and 5." + ConsoleColors.RESET);
            return;
        }

        String jsonBody = String.format("{\"accusedUsername\":\"%s\", \"score\":%d}", accusedUsername, score);
        String response = sendRequest(postRequest(BASE_URL + "/v1/projects/" + projectIndexMap.get(projectId) + "/fups/" + fUpIndexMap.get(fUpId) + "/votes", jsonBody));

        if (response != null && !response.isEmpty()) {
            System.out.println(ConsoleColors.GREEN + "Vote successfully created!" + ConsoleColors.RESET);
        } else {
            System.out.println(ConsoleColors.RED + "Failed to create vote. Please try again." + ConsoleColors.RESET);
        }
    }

    public static VoteSelection viewVotes(Scanner scanner, String authToken) {
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);
        if (projectIndexMap.isEmpty()) return null;

        System.out.print(ConsoleColors.YELLOW + "Enter project ID: " + ConsoleColors.RESET);
        int projectId = scanner.nextInt();
        if (!projectIndexMap.containsKey(projectId)) {
            System.out.println(ConsoleColors.RED + "Invalid project ID. Please select from the displayed list." + ConsoleColors.RESET);
            return null;
        }

        Map<Integer, Integer> fUpIndexMap = viewFUps(authToken, projectId);
        if (fUpIndexMap.isEmpty()) return null;

        System.out.print(ConsoleColors.YELLOW + "Enter FUp ID: " + ConsoleColors.RESET);
        int fUpId = scanner.nextInt();
        if (!fUpIndexMap.containsKey(fUpId)) {
            System.out.println(ConsoleColors.RED + "Invalid FUp ID. Please select from the displayed list." + ConsoleColors.RESET);
            return null;
        }

        String responseBody = sendRequest(getRequest(BASE_URL + "/v1/projects/" + projectIndexMap.get(projectId) + "/fups/" + fUpIndexMap.get(fUpId) + "/votes"));
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
                    System.out.println(ConsoleColors.GREEN + "[" + index + "] Vote ID: " + voteId + ", Score: " + score + ConsoleColors.RESET);
                    index++;
                }
            }
        } catch (IOException e) {
            System.out.println(ConsoleColors.RED + "Failed to retrieve votes." + ConsoleColors.RESET);
        }
        return new VoteSelection(projectId, fUpId, voteIndexMap);
    }

    public static void editVote(Scanner scanner, String authToken) {
        VoteSelection voteSelection = viewVotes(scanner, authToken);
        if (voteSelection == null || voteSelection.getVoteIndexMap().isEmpty()) return;

        System.out.print(ConsoleColors.YELLOW + "Enter Vote ID: " + ConsoleColors.RESET);
        int voteSelectionIndex = scanner.nextInt();
        if (!voteSelection.getVoteIndexMap().containsKey(voteSelectionIndex)) {
            System.out.println(ConsoleColors.RED + "Invalid Vote ID. Please select from the displayed list." + ConsoleColors.RESET);
            return;
        }
        int voteId = voteSelection.getVoteIndexMap().get(voteSelectionIndex);

        System.out.print(ConsoleColors.YELLOW + "Enter new vote score (1-5): " + ConsoleColors.RESET);
        int score = scanner.nextInt();
        if (score < 1 || score > 5) {
            System.out.println(ConsoleColors.RED + "Invalid score. Please enter a number between 1 and 5." + ConsoleColors.RESET);
            return;
        }

        String jsonBody = String.format("{\"score\":%d}", score);
        sendRequest(patchRequest(BASE_URL + "/v1/projects/" + voteSelection.getProjectId() + "/fups/" + voteSelection.getFUpId() + "/votes/" + voteId, jsonBody));
    }

    public static void deleteVote(Scanner scanner, String authToken) {
        VoteSelection voteSelection = viewVotes(scanner, authToken);
        if (voteSelection == null || voteSelection.getVoteIndexMap().isEmpty()) return;

        System.out.print(ConsoleColors.YELLOW + "Enter Vote ID: " + ConsoleColors.RESET);
        int voteSelectionIndex = scanner.nextInt();
        if (!voteSelection.getVoteIndexMap().containsKey(voteSelectionIndex)) {
            System.out.println(ConsoleColors.RED + "Invalid Vote ID. Please select from the displayed list." + ConsoleColors.RESET);
            return;
        }
        int voteId = voteSelection.getVoteIndexMap().get(voteSelectionIndex);

        sendRequest(deleteRequest( BASE_URL + "/v1/projects/" + voteSelection.getProjectId() + "/fups/" + voteSelection.getFUpId() + "/votes/" + voteId));
    }
    
}
