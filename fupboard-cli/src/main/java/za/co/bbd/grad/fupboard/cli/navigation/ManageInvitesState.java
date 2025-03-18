package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.ConsoleColors;

public class ManageInvitesState implements NavState {
   
    private int inviteNumber;

    public ManageInvitesState(int inviteNumber) {
        this.inviteNumber = inviteNumber;
    }

    @Override
    public String getLocation() {
        return "Invite # " + inviteNumber;
    }

    @Override
    public NavResponse handle(Scanner scanner) {
        System.out.println("0. Back");
        System.out.println("1. Accept invite");
        System.out.println("2. Decline invite");

        var choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 0: 
                return new NavResponse.Back();
            case 1 : 
              //  acceptInviteFunction();
                return new NavResponse.Stay();
            case 2:
               // declineInviteFunction();
                return new NavResponse.Stay();
            default:
                System.out.println(ConsoleColors.RED + "Please select a valid option." + ConsoleColors.RESET);
                return new NavResponse.Stay();
        }


    }
}