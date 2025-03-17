package za.co.bbd.grad.fupboard.api.models;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public enum ApiError {
    USERNAME_TAKEN(HttpStatus.BAD_REQUEST, "Username has been taken."),
    VALIDATION(HttpStatus.BAD_REQUEST, "Failed to validate request."),
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "Project not found."),
    INVITE_NOT_FOUND(HttpStatus.NOT_FOUND, "Invite not found."),
    F_UP_NOT_FOUND(HttpStatus.NOT_FOUND, "F-Up not found."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found."),
    VOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "Vote not found."),
    DATA_INTEGRITY(HttpStatus.BAD_REQUEST, "This request would violate database constraints."),
    USER_ALREADY_INVITED(HttpStatus.BAD_REQUEST, "User already invited.");

    public final HttpStatus status;
    public final String message;

    private ApiError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    private static class Response {
        private int status;
        private ApiError code;
        private String error;

        public Response(ApiError error) {
            this.status = error.status.value();
            this.error = error.message;
            this.code = error;
        }

        public Response(ApiError error, String message) {
            this(error);
            this.error = message;
        }

        public Date getTimestamp() {
            return new Date();
        }

        public int getStatus() {
            return status;
        }

        public ApiError getCode() {
            return code;
        }

        public String getError() {
            return error;
        }
    } 

    public ResponseEntity<Response> response() {
        return new ResponseEntity<>(new Response(this), status);
    }

    public ResponseEntity<Response> response(String message) {
        return new ResponseEntity<>(new Response(this, message), status);
    }
}
