package za.co.bbd.grad.fupboard.cli.common;

import com.fasterxml.jackson.core.JacksonException;

import za.co.bbd.grad.fupboard.cli.navigation.NavResponse;

public class NavStateException extends Exception {
    private NavResponse response;
    
    public NavStateException(String message) {
        super(message);
        this.response = NavResponse.stay();
    }

    public NavStateException(String message, NavResponse response) {
        super(message);
        this.response = response;
    }

    public NavStateException(JacksonException cause) {
        super("Failed to process response.", cause);
        cause.printStackTrace();
    }

    public NavStateException(NumberFormatException cause) {
        super("Invalid input.", cause);
    }

    public NavStateException(IndexOutOfBoundsException cause) {
        super("Invalid choice.", cause);
    }

    public NavResponse getResponse() {
        return response;
    }
}
