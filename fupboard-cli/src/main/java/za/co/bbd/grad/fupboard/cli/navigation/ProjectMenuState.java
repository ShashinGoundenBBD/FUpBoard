package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.Config;
import za.co.bbd.grad.fupboard.cli.ConsoleColors;
import za.co.bbd.grad.fupboard.cli.Main;
import za.co.bbd.grad.fupboard.cli.ProjectService;

public class ProjectMenuState implements NavState {
   

    public ProjectMenuState() {
    }

    @Override
    public String getLocation() {
        return "Project Menu"; 
    }

    @Override
    public NavResponse handle(Scanner scanner) {
        System.out.println("0. Back");
        System.out.println("1. Create a new project");
        System.out.println("2. Maintain projects I own");
        System.out.println("3. Maintain projects I am part of");

        System.out.print(Config.InputCharacter);
        int option = Integer.parseInt(scanner.nextLine());

        if (option == 0)
        {
            return new NavResponse.Back();
        }
        else if (option == 1)
        {
            //create function
            ProjectService.createNewProject(scanner);
            return new NavResponse.Stay();
        }
        else if (option == 2)
        {
            // maintain existing proj
            //list all projects user has created
            System.out.println("3. Enter project id:");
            System.out.print(Config.InputCharacter);
            int project = Integer.parseInt(scanner.nextLine());
            return new NavResponse.Push(new MaintainOwnedProjectsState(project));
        }
        else if (option == 3)
        {
            // maintain existing proj
            //list all projects user is part of
            System.out.println("3. Enter project id:");
            System.out.print(Config.InputCharacter);
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