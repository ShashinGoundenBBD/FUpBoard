package za.co.bbd.grad.fupboard.api.models;

public class CreateProjectInviteRequest {
    private String username;

    public CreateProjectInviteRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
