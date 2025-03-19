package za.co.bbd.grad.fupboard.cli.models;

public class UpdateVoteRequest {
    private int score;
    
    public UpdateVoteRequest(int score) {
        this.score = score;
    }
    
    public int getScore() {
        return score;
    }
}
