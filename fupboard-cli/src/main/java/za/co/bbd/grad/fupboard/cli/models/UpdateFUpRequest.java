package za.co.bbd.grad.fupboard.cli.models;

public class UpdateFUpRequest {
    private String name;
    private String description;
    
    public UpdateFUpRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
}
