package za.co.bbd.grad.fupboard.cli.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.HttpUtil;

import java.io.IOException;
import java.net.http.HttpRequest;

public class LeaderboardService {

    public static void viewLeaderboard(Scanner scanner, String authToken) {
        // Show projects and their IDs
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);

     

        // Ask user to pick a project ID
        System.out.print(Constants.YELLOW + "Enter project number to view leaderboard for: " + Constants.RESET);
        int index = scanner.nextInt();
        scanner.nextLine();
        System.out.print(Constants.InputCharacter);

        Integer projectId = projectIndexMap.get(index);
        if (projectId == null) {
            System.out.println(Constants.RED + "Invalid selection." + Constants.RESET);
            return;
        }
      
        HttpRequest request = HttpUtil.getRequest(Constants.BASE_URL + "/v1/projects/" + projectId + "/leaderboard");
        String response = HttpUtil.sendRequest(request);

        if (response == null) {
            System.out.println(Constants.RED + "Failed to retrieve leaderboard." + Constants.RESET);
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();

            LeaderboardResponse leaderboardResponse = mapper.readValue(response, LeaderboardResponse.class);

            // Check if leaderboard is empty
            if (leaderboardResponse.entries == null || leaderboardResponse.entries.isEmpty()) {
                System.out.println(Constants.YELLOW + "There is no leaderboard for this project because there have been no F-Ups reported." + Constants.RESET);
                return;
            }

            // Print leaderboard
            printLeaderboard(leaderboardResponse.entries);

        } catch (Exception e) {
            System.out.println(Constants.RED + "Error parsing leaderboard data: " + e.getMessage() + Constants.RESET);
        }
    }

    private static void printLeaderboard(List<LeaderboardEntry> leaderboard) {
        String reset = "\u001B[0m";
        String green = "\u001B[32m";

        String separator = "├──────────┼──────────┼──────────┤";
        String borderTop = "┌──────────┬──────────┬──────────┐";
        String borderBottom = "└──────────┴──────────┴──────────┘";

        System.out.println(borderTop);
        System.out.printf("│" + green + " %-8s │ %-8s │ %-8s " + reset + "│\n", "Ranking", "FUp", "User");
        System.out.println(separator);

        int rank = 1;
        for (LeaderboardEntry entry : leaderboard) {
            System.out.printf("│ %-8d │ %-8s │ %-8s │\n", rank++, entry.getFupCount(), entry.getUsername());
        }

        System.out.println(borderBottom);
    }

        public static Map<Integer, Integer> viewMyProjects(String authToken) {
        String responseBody = HttpUtil.sendRequest(HttpUtil.getRequest(Constants.BASE_URL + "/v1/projects"));

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

    static class LeaderboardResponse {
        public List<LeaderboardEntry> entries;
    }

    static class LeaderboardEntry {
        private String username;
        private int fupCount;

        public String getUsername() { return username; }
        public int getFupCount() { return fupCount; }
    }

    
}
