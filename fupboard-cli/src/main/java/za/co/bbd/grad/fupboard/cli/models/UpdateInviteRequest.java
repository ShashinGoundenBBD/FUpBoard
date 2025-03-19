package za.co.bbd.grad.fupboard.cli.models;

public class UpdateInviteRequest {
    private boolean accepted;

    public UpdateInviteRequest(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
