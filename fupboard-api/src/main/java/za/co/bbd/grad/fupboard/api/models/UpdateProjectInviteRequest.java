package za.co.bbd.grad.fupboard.api.models;

public class UpdateProjectInviteRequest {
    private boolean accepted;

    public UpdateProjectInviteRequest(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
