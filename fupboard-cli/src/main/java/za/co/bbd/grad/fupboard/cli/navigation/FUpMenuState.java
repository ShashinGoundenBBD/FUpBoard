package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.ConsoleColors;

public class FUpMenuState implements NavState {
    private int project;

    public FUpMenuState(int project) {
        this.project = project;
    }

    @Override
    public String getLocation() {
        return "F-Up Menu" ;
    }

    @Override
    public NavResponse handle(Scanner scanner) {
        System.out.println("0. Back.");
        System.out.println("1. Report an F-Up.");
        System.out.println("2. Manage existing F-Ups.");
        var choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 0: 
                return new NavResponse.Back();
            case 1 : 
              //  reportFUpFunction();
                return new NavResponse.Stay();
            case 2:
               //show all FUps for project
               System.out.print("Enter f-up number:");
               var fUp = Integer.parseInt(scanner.nextLine());
               return new NavResponse.Push(new FUpState(project, fUp));
            default:
                System.out.println(ConsoleColors.RED + "Please select a valid option." + ConsoleColors.RESET);
                return new NavResponse.Stay();
        }


    }
}