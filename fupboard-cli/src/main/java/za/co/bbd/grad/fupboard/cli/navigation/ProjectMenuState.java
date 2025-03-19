package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.List;
import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.Project;
import za.co.bbd.grad.fupboard.cli.services.ProjectService;

public class ProjectMenuState implements NavState {
    public ProjectMenuState() {
    }

    @Override
    public String getLocation() {
        return Constants.BLUE + "Projects" + Constants.RESET;
    }

    @Override
    public NavResponse handle(Scanner scanner) throws NavStateException {
        System.out.println("0. Back");
        System.out.println("1. Create Project");
        System.out.println("2. View Project");
        System.out.println("3. View Projects I Own");

        System.out.print(Constants.INPUT_QUERY);
        int option = Integer.parseInt(scanner.nextLine());

        switch (option) {
            case 0:
                return NavResponse.back();
            case 1:
                System.out.print("Project name: ");
                String name = scanner.nextLine().trim();
                if (name.isEmpty()) {
                    return NavResponse.stay();
                }
                var newProject = ProjectService.createProject(name);
                return NavResponse.push(new ProjectState(newProject));
            case 2:
                return chooseFromProjects(ProjectService.getProjects(), scanner);
            case 3:
                return chooseFromProjects(ProjectService.getOwnedProjects(), scanner);
            default:
                throw new NavStateException("Invalid option.");
        }
    }

    private NavResponse chooseFromProjects(List<Project> projects, Scanner scanner) throws NavStateException {
        if (projects.isEmpty()) {
            System.out.println("No projects.");
            return NavResponse.stay();
        }

        System.out.println("0. Back");
        for (int i = 0; i < projects.size(); i++) {
            var p = projects.get(i);
            System.out.println((i+1) + ". " + p);
        }

        System.out.print("Project: ");

        int projectIndex = Integer.parseInt(scanner.nextLine());

        if (projectIndex == 0) {
            return NavResponse.stay();
        }

        try {
            var p = projects.get(projectIndex-1);
            return NavResponse.push(new ProjectState(p));
        } catch (IndexOutOfBoundsException e) {
            throw new NavStateException(e);
        }
    }
}