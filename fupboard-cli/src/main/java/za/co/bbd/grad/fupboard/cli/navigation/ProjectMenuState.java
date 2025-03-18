package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.ConsoleColors;

public class ProjectMenuState implements NavState {
   

    public ProjectMenuState() {
    }

    @Override
    public String getLocation() {
        return "Project Menu" ;
    }

    @Override
    public NavResponse handle(Scanner scanner) {
        System.out.println("0. Back");
        System.out.println("1. Create a new project");
        System.out.println("2. Maintain existing project");
        int option = Integer.parseInt(scanner.nextLine());

        if (option == 0)
        {
            return new NavResponse.Back();
        }
        else if (option == 1)
        {
            //create function
            return new NavResponse.Stay();
        }
        else if (option == 2)
        {
            // maintain existing proj
            //list all projects user has created

            System.out.println("2. Enter project number you would like to maintain:");
            int project = Integer.parseInt(scanner.nextLine());
            return new NavResponse.Push(new ProjectState(project));
        }
        else
        {
            System.out.println("Invalid choice.");
            return new NavResponse.Stay();
        }
    }
}