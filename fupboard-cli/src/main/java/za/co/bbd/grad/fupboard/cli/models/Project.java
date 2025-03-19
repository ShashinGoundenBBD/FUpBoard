package za.co.bbd.grad.fupboard.cli.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import za.co.bbd.grad.fupboard.cli.services.AuthenticationService;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
    private int projectId;
    private String projectName;
    private String ownerUsername;
    private int ownerId;

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
    public int getOwnerId() {
        return ownerId;
    }
    
    public boolean isCurrentUserOwner() {
        return ownerId == AuthenticationService.getCurrentUserId();
    }
}
