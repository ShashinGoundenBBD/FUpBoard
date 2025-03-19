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
                return NavResponse.back();
            case 1: 
                //editMyProject()
                return NavResponse.stay();
            case 2:
                //deleteMyProject()
                return NavResponse.stay();
            case 3:
                return NavResponse.push(new FUpMenuState(project));
            case 4:
                //call leaderboard function
                return NavResponse.stay();
            case 5:
                //call invite to project function
                return NavResponse.stay();
            default:
                System.out.println("Invalid choice.");
                return NavResponse.stay();
                
        }
    }
}