package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.List;
import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.FUp;
import za.co.bbd.grad.fupboard.cli.models.Project;
import za.co.bbd.grad.fupboard.cli.services.FUpService;

public class FUpMenuState implements NavState {
    private Project project;

    public FUpMenuState(Project project) {
        this.project = project;
    }

    @Override
    public String getLocation() {
        return Constants.BLUE + "F-Ups" + Constants.RESET;
    }

    @Override
    public NavResponse handle(Scanner scanner) throws NavStateException {
        System.out.println("0. Back.");
        System.out.println("1. Report an F-Up");
        System.out.println("2. View F-Up");

        System.out.print(Constants.INPUT_QUERY);
        var choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 0: 
                return NavResponse.back();
            case 1: 
                System.out.print("F-Up Name: ");
                var name = scanner.nextLine().trim();
                if (name.isEmpty()) {
                    return NavResponse.stay();
                }
                
                System.out.print("Description: ");
                var description = scanner.nextLine().trim();

                var fUp = FUpService.createFUp(project.getProjectId(), name, description);
                return NavResponse.push(new FUpState(project, fUp));
            case 2:
                return chooseFromFUps(FUpService.getFUps(project.getProjectId()), scanner);
            default:
                throw new NavStateException("Invalid option.");
        }
    }

    private NavResponse chooseFromFUps(List<FUp> fUps, Scanner scanner) throws NavStateException {
        if (fUps.isEmpty()) {
            System.out.println("No f-ups.");
            return NavResponse.stay();
        }

        for (int i = 0; i < fUps.size(); i++) {
            var f = fUps.get(i);
            System.out.println((i+1) + ". " + f);
        }

        System.out.print("F-Up: ");

        int fUpIndex = Integer.parseInt(scanner.nextLine());

        try {
            var f = fUps.get(fUpIndex-1);
            return NavResponse.push(new FUpState(project, f));
        } catch (IndexOutOfBoundsException e) {
            throw new NavStateException(e);
        }
    }
}