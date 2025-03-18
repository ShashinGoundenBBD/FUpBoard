package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

public class ProjectState implements NavState {
    private int project;

    public ProjectState(int project) {
        this.project = project;
    }

    @Override
    public String getLocation() {
        return "Project #" + project;
    }

    @Override
    public NavResponse handle(Scanner scanner) {
        System.out.println("0. Back");
        System.out.println("1. Do something to project.");
        System.out.println("2. go to f-up.");
        var fUp = Integer.parseInt(scanner.nextLine());
        return new NavResponse.Push(new FUpState(project, fUp));
    }
}