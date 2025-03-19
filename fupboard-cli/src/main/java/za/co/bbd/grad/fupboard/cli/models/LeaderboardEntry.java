package za.co.bbd.grad.fupboard.cli.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LeaderboardEntry {
    private String username;
    private double score;

    public String getUsername() {
        return username;
    }
    public double getScore() {
        return score;
    }
}
