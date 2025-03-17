package za.co.bbd.grad.fupboard.cli;

import java.util.Scanner;

public class LeaderboardService {
    
    public static void viewLeaderboard(Scanner scanner, String authToken) {
        // make endpoint call to get leaderboard
    
        String reset = "\u001B[0m";
        String green = "\u001B[32m";
       
        String separator = "├──────────┼──────────┼──────────┤";
        String borderTop = "┌──────────┬──────────┬──────────┐";
        String borderBottom = "└──────────┴──────────┴──────────┘";
    
        System.out.println(borderTop);
        System.out.printf("│" + green + " %-8s │ %-8s │ %-8s " + reset + "│\n", "Ranking", "FUp", "User");
        System.out.println(separator);
    
        for (int i = 1; i <= 10; i++) {
            System.out.printf("│ %-8d │ %-8s │ %-8s │\n", i, "Test", "Test");
        }
    
        System.out.println(borderBottom);
    }
    
}
