package za.co.bbd.grad.fupboard.cli.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Project {
    private int projectId;
    private String projectName;
    private String ownerUsername;

    @JsonProperty("fUps") 
    private List<FUp> fUps;

    private List<String> invites;

    // Getters and setters
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public List<FUp> getFUps() {
        return fUps;
    }

    public void setFUps(List<FUp> fUps) {
        this.fUps = fUps;
    }

    public List<String> getInvites() {
        return invites;
    }

    public void setInvites(List<String> invites) {
        this.invites = invites;
    }
}
