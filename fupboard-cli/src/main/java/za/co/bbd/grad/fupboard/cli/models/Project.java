package za.co.bbd.grad.fupboard.cli.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
    private int projectId;
    private String projectName;
    private String ownerUsername;

    @Override
    public String toString() {
        return projectName + " (#" + projectId + ") - " + ownerUsername;
    }

    public int getProjectId() {
        return projectId;
    }
    public String getProjectName() {
        return projectName;
    }
    public String getOwnerUsername() {
        return ownerUsername;
    }
}
