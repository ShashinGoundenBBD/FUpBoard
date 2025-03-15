package za.co.bbd.grad.fupboard.api.models;

public class CreateVoteRequest {
    private String accusedUsername;
    private int score;

    public String getAccusedUsername() {
        return accusedUsername;
    }

    public int getScore() {
        return score;
    }
}
