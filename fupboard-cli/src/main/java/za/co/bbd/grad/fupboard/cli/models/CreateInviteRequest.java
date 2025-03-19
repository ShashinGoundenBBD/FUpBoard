package za.co.bbd.grad.fupboard.cli.models;

public class CreateInviteRequest {
    private String username;

    public CreateInviteRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
