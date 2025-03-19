package za.co.bbd.grad.fupboard.cli.navigation;

import za.co.bbd.grad.fupboard.cli.common.Constants;

import java.util.Scanner;



public class StartState implements NavState {
    @Override
    public String getLocation() {
        return """                                                                                            
        ███╗░░░███╗░█████╗░██╗███╗░░██╗ ███╗░░░███╗███████╗███╗░░██╗██╗░░░██╗
        ████╗░████║██╔══██╗██║████╗░██║ ████╗░████║██╔════╝████╗░██║██║░░░██║
        ██╔████╔██║███████║██║██╔██╗██║ ██╔████╔██║█████╗░░██╔██╗██║██║░░░██║
        ██║╚██╔╝██║██╔══██║██║██║╚████║ ██║╚██╔╝██║██╔══╝░░██║╚████║██║░░░██║
        ██║░╚═╝░██║██║░░██║██║██║░╚███║ ██║░╚═╝░██║███████╗██║░╚███║╚██████╔╝
        ╚═╝░░░░░╚═╝╚═╝░░╚═╝╚═╝╚═╝░░╚══╝ ╚═╝░░░░░╚═╝╚══════╝╚═╝░░╚══╝░╚═════╝░            
                """ ;
    }

    @Override
    public NavResponse handle(Scanner scanner) {
        System.out.println("0. Exit");
        System.out.println("1. Users");
        System.out.println("2. Projects");
        

        System.out.print(Constants.InputCharacter);
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 0:
                return NavResponse.back();
            case 1:
                return NavResponse.push(new UserState());
            case 2:
                return NavResponse.push(new ProjectMenuState());
            default:
                System.out.println("Invalid choice.");
                return NavResponse.stay();
        }
    }
}
