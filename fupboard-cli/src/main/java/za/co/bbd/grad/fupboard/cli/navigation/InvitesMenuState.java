package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.List;
import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.Invite;
import za.co.bbd.grad.fupboard.cli.models.Project;
import za.co.bbd.grad.fupboard.cli.services.InviteService;

public class InvitesMenuState implements NavState {
    private Project project;

    public InvitesMenuState(Project project) {
        this.project = project;
    }

    @Override
    public String getLocation() {
        return Constants.BLUE + "Collaborators" + Constants.RESET;
    }

    @Override
    public NavResponse handle(Scanner scanner) throws NavStateException {
        System.out.println("0. Back.");
        System.out.println("1. Invite a User");
        System.out.println("2. View Invites");

        System.out.print(Constants.INPUT_QUERY);
        var choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 0: 
                return NavResponse.back();
            case 1: 
                System.out.print("Username: ");
                var username = scanner.nextLine().trim();
                if (username.isEmpty()) {
                    return NavResponse.stay();
                }
                
                var invite = InviteService.createInvite(project.getProjectId(), username);
                return NavResponse.push(new InviteState(invite));
            case 2:
                return chooseFromInvites(InviteService.getInvites(project.getProjectId()), scanner);
            default:
                throw new NavStateException("Invalid option.");
        }
    }

    private NavResponse chooseFromInvites(List<Invite> invites, Scanner scanner) throws NavStateException {
        if (invites.isEmpty()) {
            System.out.println("No invites.");
            return NavResponse.stay();
        }

        for (int i = 0; i < invites.size(); i++) {
            var pi = invites.get(i);
            System.out.println((i+1) + ". " + pi);
        }

        System.out.print("Invite: ");

        int inviteIndex = Integer.parseInt(scanner.nextLine());

        try {
            var pi = invites.get(inviteIndex-1);
            return NavResponse.push(new InviteState(pi));
        } catch (IndexOutOfBoundsException e) {
            throw new NavStateException(e);
        }
    }
}
