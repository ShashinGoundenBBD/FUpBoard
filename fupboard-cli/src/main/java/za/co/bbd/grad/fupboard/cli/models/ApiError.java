package za.co.bbd.grad.fupboard.cli.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiError {
    private int status;
    private String code;
    private String error;
    private Date timestamp;
    
    public int getStatus() {
        return status;
    }
    public String getCode() {
        return code;
    }
    public String getError() {
        return error;
    }
    public Date getTimestamp() {
        return timestamp;
    }
}
