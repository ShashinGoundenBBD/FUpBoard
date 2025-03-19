package za.co.bbd.grad.fupboard.cli.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Vote {
    private int voteId;
    private int score;
    private String accusedUsername;
    private String reporterUsername;

    @Override
    public String toString() {
        return reporterUsername + " voted " + score + " for " + accusedUsername;
    }

    public int getVoteId() {
        return voteId;
    }
    public int getScore() {
        return score;
    }
    public String getAccusedUsername() {
        return accusedUsername;
    }
    public String getReporterUsername() {
        return reporterUsername;
    }
}
