package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;

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
        System.out.println("0. Back");
        System.out.println("1. Score F-Up");
        System.out.println("2. Edit F-Up score");
        System.out.println("3. Delete F-Up");

        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 0:
                return NavResponse.back();
            case 1:
                //voteForFUpFunction
                return NavResponse.stay();
            case 2:
                //editFUpFunction
                return NavResponse.stay();
            case 3:
                //deleteFUpFunction
                return NavResponse.stay();
            default:
                System.out.println(Constants.RED + "Please select a valid option." + Constants.RESET);
                return NavResponse.stay();
        }

    }
}