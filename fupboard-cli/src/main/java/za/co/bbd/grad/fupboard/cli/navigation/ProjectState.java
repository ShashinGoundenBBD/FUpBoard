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
        System.out.println("1. Manage project F-Ups.");
        System.out.println("2. View Leaderboard.");
        
        var choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 0:
                return new NavResponse.Back();
            case 1: 
                //editMyProject()
                return new NavResponse.Stay();
            case 2:
                //deleteMyProject()
                return new NavResponse.Stay();
            case 3:
                return new NavResponse.Push(new FUpMenuState(project));
            case 4:
                //call leaderboard function
                return new NavResponse.Stay();
            case 5:
                //call invite to project function
                return new NavResponse.Stay();
            default:
                System.out.println("Invalid choice.");
                return new NavResponse.Stay();
                
        }
    }
}