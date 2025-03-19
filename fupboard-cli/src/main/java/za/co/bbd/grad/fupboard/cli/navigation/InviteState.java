package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.Invite;
import za.co.bbd.grad.fupboard.cli.services.InviteService;

public class InviteState implements NavState {
    private Invite invite;
    private boolean showProject;

    public InviteState(Invite invite) throws NavStateException {
        this(invite, false);
    }

    public InviteState(Invite invite, boolean showProject) throws NavStateException {
        this.invite = invite;
        this.showProject = showProject;
    }

    @Override
    public String getLocation() {
        if (showProject)
            return Constants.YELLOW + invite.getProjectName() + " - " + invite.getProjectOwnerUsername() + " " + (invite.isAccepted() ? "(accepted)" : "(pending)") + Constants.RESET;
        else
            return Constants.YELLOW + invite.toString() + Constants.RESET;
    }

    @Override
    public NavResponse handle(Scanner scanner) throws NavStateException {
        if (showProject)
            System.out.println(Constants.GREEN + invite + Constants.RESET);
        else
            System.out.println(Constants.GREEN + invite.toProjectString() + Constants.RESET);
        System.out.println("0. Back");
        System.out.println("1. Accept Invite");
        System.out.println("2. Cancel Invite");

        System.out.print(Constants.INPUT_QUERY);
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 0:
                return NavResponse.back();
            case 1:
                var newInvite = InviteService.acceptInvite(invite.getProjectId(), invite.getProjectInviteId());
                return NavResponse.replace(new InviteState(newInvite, showProject));
            case 2:
                InviteService.deleteInvite(invite.getProjectId(), invite.getProjectInviteId());
                return NavResponse.back();
            default:
                throw new NavStateException("Invalid option.");
        }
    }

}
