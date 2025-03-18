package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

public class StartState implements NavState {
    @Override
    public String getLocation() {
        return "";
    }

    @Override
    public NavResponse handle(Scanner scanner) {
        System.out.println("0. Exit");
        System.out.println("1. Users");
        System.out.println("2. Projects");

        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 0:
                return new NavResponse.Back();
            case 1:
                // todo
                return new NavResponse.Stay();
            case 2:
                System.out.println("Enter project id: ");
                int project = Integer.parseInt(scanner.nextLine());
                return new NavResponse.Push(new ProjectState(project));
            default:
                System.out.println("Invalid choice.");
                return new NavResponse.Stay();
        }
    }
}
