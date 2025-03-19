package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.FUp;
import za.co.bbd.grad.fupboard.cli.models.Project;
import za.co.bbd.grad.fupboard.cli.services.FUpService;

public class FUpState implements NavState {
    private Project project;
    private FUp fUp;

    public FUpState(Project project, FUp fUp) {
        this.project = project;
        this.fUp = fUp;
    }

    @Override
    public String getLocation() {
        return Constants.YELLOW + fUp.getfUpName() + " (#" + fUp.getfUpId() + ")" + Constants.RESET;
    }

    @Override
    public NavResponse handle(Scanner scanner) throws NavStateException {
        System.out.println(Constants.GREEN + fUp + Constants.RESET);
        if (!fUp.getDescription().isEmpty())
            System.out.println(Constants.YELLOW + fUp.getDescription() + Constants.RESET);
        System.out.println("0. Back");
        System.out.println("1. Votes");
        System.out.println("2. Rename F-Up");
        System.out.println("3. Edit Description");
        System.out.println("4. Delete F-Up");

        System.out.print(Constants.INPUT_QUERY);
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 0:
                return NavResponse.back();
            case 1:
                return NavResponse.push(new VoteMenuState(project, fUp));
            case 2:
                System.out.print("New Name: ");
                var newName = scanner.nextLine();
                var newFUp = FUpService.updateFUp(project.getProjectId(), fUp.getfUpId(), newName, null);
                return NavResponse.replace(new FUpState(project, newFUp));
            case 3:
                System.out.print("New Description: ");
                var newDescription = scanner.nextLine();
                var newFUpWithDesc = FUpService.updateFUp(project.getProjectId(), fUp.getfUpId(), null, newDescription);
                return NavResponse.replace(new FUpState(project, newFUpWithDesc));
            case 4:
                System.out.print("Are you sure you want to delete this f-up? [y/N] ");
                var areYouSure = scanner.nextLine().trim().toLowerCase();
                if (!areYouSure.equals("y")) {
                    return NavResponse.stay();
                }
                FUpService.deleteFUp(project.getProjectId(), fUp.getfUpId());
                return NavResponse.back();
            default:
                throw new NavStateException("Invalid option.");
        }
    }
}