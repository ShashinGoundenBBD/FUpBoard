package za.co.bbd.grad.fupboard.cli.models;

public class CreateVoteRequest {
    private String accusedUsername;
    private int score;
    
    public CreateVoteRequest(String accused, int score) {
        this.accusedUsername = accused;
        this.score = score;
    }

    public String getAccusedUsername() {
        return accusedUsername;
    }
    public int getScore() {
        return score;
    }
}
