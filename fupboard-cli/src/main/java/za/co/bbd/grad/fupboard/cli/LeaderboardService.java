package za.co.bbd.grad.fupboard.cli;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpRequest;

import za.co.bbd.grad.fupboard.Config;

public class LeaderboardService {

    public static void viewLeaderboard(Scanner scanner, String authToken) {
        // Show projects and their IDs
        Map<Integer, Integer> projectIndexMap = viewMyProjects(authToken);

     

        // Ask user to pick a project ID
        System.out.print(ConsoleColors.YELLOW + "Enter project number to view leaderboard for: " + ConsoleColors.RESET);
        int index = scanner.nextInt();
        scanner.nextLine();

        Integer projectId = projectIndexMap.get(index);
        if (projectId == null) {
            System.out.println(ConsoleColors.RED + "Invalid selection." + ConsoleColors.RESET);
            return;
        }
      
        HttpRequest request = HttpUtil.getRequest(authToken, Config.BASE_URL + "/v1/projects/" + projectId + "/leaderboard");
        String response = HttpUtil.sendRequest(request);

        if (response == null) {
            System.out.println(ConsoleColors.RED + "Failed to retrieve leaderboard." + ConsoleColors.RESET);
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();

            LeaderboardResponse leaderboardResponse = mapper.readValue(response, LeaderboardResponse.class);

            // Check if leaderboard is empty
            if (leaderboardResponse.entries == null || leaderboardResponse.entries.isEmpty()) {
                System.out.println(ConsoleColors.YELLOW + "There is no leaderboard for this project because there have been no F-Ups reported." + ConsoleColors.RESET);
                return;
            }

            // Print leaderboard
            printLeaderboard(leaderboardResponse.entries);

        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Error parsing leaderboard data: " + e.getMessage() + ConsoleColors.RESET);
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
        String responseBody = HttpUtil.sendRequest(HttpUtil.getRequest(authToken, Config.BASE_URL + "/v1/projects"));

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
