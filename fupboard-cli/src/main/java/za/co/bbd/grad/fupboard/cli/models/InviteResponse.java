package za.co.bbd.grad.fupboard.cli.models;

public class InviteResponse {
    private int projectInviteId;
    private boolean accepted;
    private String username;
    private int projectId;

    // Getters and Setters
    public int getProjectInviteId() {
        return projectInviteId;
    }

    public void setProjectInviteId(int projectInviteId) {
        this.projectInviteId = projectInviteId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}
