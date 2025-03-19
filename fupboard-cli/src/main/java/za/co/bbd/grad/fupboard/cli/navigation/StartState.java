package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.services.AuthenticationService;
import za.co.bbd.grad.fupboard.cli.services.UserService;



public class StartState implements NavState {
    @Override
    public String getLocation() {
        return Constants.BLUE + "Main Menu" + Constants.RESET;
    }

    @Override
    public NavResponse handle(Scanner scanner) throws NavStateException {
        System.out.println("0. Exit");
        System.out.println("1. My Profile");
        System.out.println("2. Projects");

        System.out.print(Constants.INPUT_QUERY);
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 0:
                return NavResponse.back();
            case 1:
                return NavResponse.push(new UserState(UserService.getUserMe()));
            case 2:
                return NavResponse.push(new ProjectMenuState());
            case 99:
                System.out.println("JWT: " + AuthenticationService.getAuthToken());
                return NavResponse.stay();
            default:
                throw new NavStateException("Invalid option.");
        }
    }
}
