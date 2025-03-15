package za.co.bbd.grad.fupboard.api.models;

public class UpdateProjectInviteRequest {
    private boolean accepted;

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
