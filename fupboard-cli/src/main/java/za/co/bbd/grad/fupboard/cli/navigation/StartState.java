package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.Config;

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
        

        System.out.print(Config.InputCharacter);
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 0:
                return new NavResponse.Back();
            case 1:
                return new NavResponse.Push(new UserState());
            case 2:
                return new NavResponse.Push(new ProjectMenuState());
            default:
                System.out.println("Invalid choice.");
                return new NavResponse.Stay();
        }
    }
}
