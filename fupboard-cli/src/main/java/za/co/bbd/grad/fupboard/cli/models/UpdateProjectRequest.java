package za.co.bbd.grad.fupboard.cli.models;

public class UpdateProjectRequest {
    private String name;
    
    public UpdateProjectRequest(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
