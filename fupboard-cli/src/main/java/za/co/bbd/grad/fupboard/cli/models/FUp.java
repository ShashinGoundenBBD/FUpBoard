package za.co.bbd.grad.fupboard.cli.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FUp {
    private int fUpId;
    private String fUpName;
    private String description;

    @Override
    public String toString() {
        return "'" + fUpName + "' (#" + fUpId + ")";
    }

    public int getfUpId() {
        return fUpId;
    }
    public String getfUpName() {
        return fUpName;
    }
    public String getDescription() {
        return description;
    }
}
