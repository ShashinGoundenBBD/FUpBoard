package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.services.InviteService;

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
                return NavResponse.back();
            case 1 : 
                InviteService.acceptInvite(inviteNumber);
                return NavResponse.stay();
            case 2:
                InviteService.declineInvite(inviteNumber);
                return NavResponse.stay();
            default:
                System.out.println(Constants.RED + "Please select a valid option." + Constants.RESET);
                return NavResponse.stay();
        }


    }
}