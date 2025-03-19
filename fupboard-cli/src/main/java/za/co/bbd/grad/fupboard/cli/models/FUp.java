package za.co.bbd.grad.fupboard.cli.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class FUp {
    @JsonProperty("fUpId") // Ensures Jackson correctly maps "fUpId" in JSON to fupId in Java
    private int fupId;

    @JsonProperty("fUpName") // Ensures "fUpName" maps correctly
    private String fupName;

    private String description;
    private List<String> votes;

    // Getters and setters
    public int getFupId() {
        return fupId;
    }

    public void setFupId(int fupId) {
        this.fupId = fupId;
    }

    public String getFupName() {
        return fupName;
    }

    public void setFupName(String fupName) {
        this.fupName = fupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getVotes() {
        return votes;
    }

    public void setVotes(List<String> votes) {
        this.votes = votes;
    }
}
