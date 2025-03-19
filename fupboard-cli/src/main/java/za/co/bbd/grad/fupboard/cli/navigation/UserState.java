package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.services.UserService;

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
                return NavResponse.back();
            case 1 : 
                UserService.viewMyInformation();
                return NavResponse.stay();
            case 2:
               // updateMyDetails();
                return NavResponse.stay();
            case 3:
                //show all invites
                //Select invite to accept/decline
                System.out.println("Which invite would you like to accept/decline?");
                var inviteNumber = Integer.parseInt(scanner.nextLine());
                return NavResponse.push(new ManageInvitesState(inviteNumber));
            default:
                System.out.println(Constants.RED + "Please select a valid option." + Constants.RESET);
                return NavResponse.stay();
        }


    }
}