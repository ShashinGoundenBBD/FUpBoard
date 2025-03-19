package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.Config;
import za.co.bbd.grad.fupboard.cli.ConsoleColors;
import za.co.bbd.grad.fupboard.cli.Main;
import za.co.bbd.grad.fupboard.cli.ProjectService;

public class MaintainOwnedProjectsState implements NavState {
    private int project;

    public MaintainOwnedProjectsState(int project) {
        this.project = project;
    }

    @Override
    public String getLocation() {
        return "Projects you own"; 
    }

    @Override
    public NavResponse handle(Scanner scanner) {
        System.out.println("0. Back");
        System.out.println("1. Edit project.");
        System.out.println("2. Delete project.");
        System.out.println("3. Invite users to project.");

        System.out.print(Config.InputCharacter);
        int option = Integer.parseInt(scanner.nextLine());

        switch (option)
        {
            case 0:
                return new NavResponse.Back();
            case 1:
                //call editProjectFunction()
                return new NavResponse.Stay();
            case 2:
                //call deleteProjectFunction()
                return new NavResponse.Stay();
            case 3:
                //call inviteUsersFunction()
                //return to invite menue
                return new NavResponse.Stay();
            default:
                System.out.println(ConsoleColors.RED + "Please select a valid option." + ConsoleColors.RESET);
                return new NavResponse.Stay();
        }
    }
}