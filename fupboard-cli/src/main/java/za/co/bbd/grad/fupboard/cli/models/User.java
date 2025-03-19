package za.co.bbd.grad.fupboard.cli.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String username;
    private String email;

    @Override
    public String toString() {
        return "'" + username + "' (" + email + ")";
    }

    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
}
