package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.ConsoleColors;

public class UserState implements NavState {
   

    public UserState() {
    }

    @Override
    public String getLocation() {
        return "User" ;
    }

    @Override
    public NavResponse handle(Scanner scanner) {
        System.out.println("0. Back");
        System.out.println("1. View my information.");
        System.out.println("2. Update my details");
        System.out.println("3. Manage invites");
        var choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 0: 
                return new NavResponse.Back();
            case 1 : 
              //  viewMyInformation();
                return new NavResponse.Stay();
            case 2:
               // updateMyDetails();
                return new NavResponse.Stay();
            case 3:
                //show all invites
                //Select invite to accept/decline
                System.out.println("Which invite would you like to accept/decline?");
                var inviteNumber = Integer.parseInt(scanner.nextLine());
                return new NavResponse.Push(new ManageInvitesState(inviteNumber));
            default:
                System.out.println(ConsoleColors.RED + "Please select a valid option." + ConsoleColors.RESET);
                return new NavResponse.Stay();
        }


    }
}