package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

public class FUpState implements NavState {
    private int project;
    private int fUp;

    public FUpState(int project, int fUp) {
        this.project = project;
        this.fUp = fUp;
    }

    @Override
    public String getLocation() {
        return "F-Up #" + fUp;
    }

    @Override
    public NavResponse handle(Scanner scanner) {
        System.out.println("1. Do something to f-up.");
        System.out.println("2. Do something else to f-up.");
        scanner.nextLine();
        return new NavResponse.Back();
    }
}