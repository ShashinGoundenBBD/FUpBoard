package za.co.bbd.grad.fupboard.cli.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Invite {
    private int projectInviteId;
    private boolean accepted;
    private String username;
    private String projectName;
    private String projectOwnerUsername;
    private int projectId;

    @Override
    public String toString() {
        return "'" + username + "' (#" + projectInviteId + ") " +
            (accepted ? "(accepted)" : "(pending)");
    }

    public String toProjectString() {
        return projectName + " - " + projectOwnerUsername + " " + (accepted ? "(accepted)" : "(pending)");
    }

    public int getProjectInviteId() {
        return projectInviteId;
    }
    public boolean isAccepted() {
        return accepted;
    }
    public String getUsername() {
        return username;
    }
    public int getProjectId() {
        return projectId;
    }
    public String getProjectName() {
        return projectName;
    }
    public String getProjectOwnerUsername() {
        return projectOwnerUsername;
    }
}
