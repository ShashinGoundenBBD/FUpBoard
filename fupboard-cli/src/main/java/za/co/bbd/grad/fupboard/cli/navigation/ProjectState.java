package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.Project;
import za.co.bbd.grad.fupboard.cli.services.ProjectService;

public class ProjectState implements NavState {
    private Project project;

    public ProjectState(Project project) {
        this.project = project;
    }

    @Override
    public String getLocation() {
        return Constants.YELLOW + "'" + project.getProjectName() + "' (#" + project.getProjectId() + ")" + Constants.RESET;
    }

    @Override
    public NavResponse handle(Scanner scanner) throws NavStateException {
        System.out.println(Constants.GREEN + project + Constants.RESET);
        System.out.println("0. Back");
        System.out.println("1. View Leaderboard");
        System.out.println("2. F-Ups");
        System.out.println("3. Collaborators");
        System.out.println("4. Rename Project");
        System.out.println("5. Delete Project");

        System.out.print(Constants.INPUT_QUERY);
        var choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 0:
                return NavResponse.back();
            case 1:
                var leaderboard = ProjectService.getProjectLeaderboard(project.getProjectId());
                leaderboard.print();
                return NavResponse.stay();
            case 2:
                return NavResponse.push(new FUpMenuState(project));
            case 3:
                return NavResponse.push(new InvitesMenuState(project));
            case 4:
                System.out.print("New project name: ");
                String newName = scanner.nextLine().trim();
                if (newName.isEmpty()) {
                    return NavResponse.stay();
                }
                var newProject = ProjectService.renameProject(project.getProjectId(), newName);
                return NavResponse.replace(new ProjectState(newProject));
            default:
                throw new NavStateException("Invalid option.");
        }
    }
}