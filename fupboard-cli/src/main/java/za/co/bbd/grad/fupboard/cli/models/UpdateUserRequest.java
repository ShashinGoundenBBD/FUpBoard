package za.co.bbd.grad.fupboard.cli.models;

public class UpdateUserRequest {
    private String username;
    private String email;
    
    public UpdateUserRequest(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
}
