package za.co.bbd.grad.fupboard.api.models;

public class CreateProjectRequest {

    private String name;

    // Constructor
    public CreateProjectRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
