package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;


import za.co.bbd.grad.fupboard.cli.Main;
import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.services.ProjectService;

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

        System.out.print(Constants.InputCharacter);
        int option = Integer.parseInt(scanner.nextLine());

        switch (option)
        {
            case 0:
                return NavResponse.back();
            case 1:
                //call editProjectFunction()
                return NavResponse.stay();
            case 2:
                //call deleteProjectFunction()
                return NavResponse.stay();
            case 3:
                //call inviteUsersFunction()
                //return to invite menue
                return NavResponse.stay();
            default:
                System.out.println(Constants.RED + "Please select a valid option." + Constants.RESET);
                return NavResponse.stay();
        }
    }
}