package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.Invite;
import za.co.bbd.grad.fupboard.cli.services.AuthenticationService;
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
            return Constants.YELLOW + invite.getProjectName() + " - " + invite.getProjectOwnerUsername() + Constants.RESET;
        else
            return Constants.YELLOW + invite.getUsername() + Constants.RESET;
    }

    @Override
    public NavResponse handle(Scanner scanner) throws NavStateException {
        if (showProject)
            System.out.println(Constants.GREEN + invite.toProjectString() + Constants.RESET);
        else
            System.out.println(Constants.GREEN + invite + Constants.RESET);
        System.out.println("0. Back");
        
        var acceptColour = invite.getUserId() != AuthenticationService.getCurrentUserId()
            ? Constants.GREY : "";
        
        // only the invitee may cancel an invite
        System.out.println(acceptColour + "1. Accept Invite" + Constants.RESET);
        
        // only the project owner or the invitee may cancel an invite
        var cancelColour = invite.getUserId() != AuthenticationService.getCurrentUserId()
            && invite.getUserId() != invite.getProjectOwnerUserId()
            ? Constants.GREY : "";
        
        System.out.println(cancelColour + "2. Cancel Invite" + Constants.RESET);

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
