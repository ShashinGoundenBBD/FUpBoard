package za.co.bbd.grad.fupboard.cli;

import java.util.List;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;

import za.co.bbd.grad.fupboard.Config;

import com.fasterxml.jackson.core.type.TypeReference;
import java.net.http.HttpRequest;

public class LeaderboardService {

    public static void viewLeaderboard(Scanner scanner, String authToken) {
        // Show projects and their IDs
        ProjectService.viewMyProjects(authToken);

        // Ask user to pick a project ID
        System.out.print(ConsoleColors.YELLOW + "Enter project number to view leaderboard for: " + ConsoleColors.RESET);
        int projectId = scanner.nextInt();
        scanner.nextLine();
      
        HttpRequest request = HttpUtil.getRequest(authToken, Config.BASE_URL + "/v1/projects/" + projectId + "/leaderboard");
        String response = HttpUtil.sendRequest(request);

        if (response == null) {
            System.out.println(ConsoleColors.RED + "Failed to retrieve leaderboard." + ConsoleColors.RESET);
            return;
        }

        try {
            // Parse JSON response
            ObjectMapper mapper = new ObjectMapper();
            List<LeaderboardEntry> leaderboard = mapper.readValue(response, new TypeReference<List<LeaderboardEntry>>() {});

            // Print leaderboard
            printLeaderboard(leaderboard);

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

    static class LeaderboardEntry {
        private String username;
        private int fupCount;

        public String getUsername() { return username; }
        public int getFupCount() { return fupCount; }
    }
}
