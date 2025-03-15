package za.co.bbd.grad.fupboard.api.models;

public class LeaderboardEntry {
    private String username;
    private double score;

    public LeaderboardEntry(String username, double score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
