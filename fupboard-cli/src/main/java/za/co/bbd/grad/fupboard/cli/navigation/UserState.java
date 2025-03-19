package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.List;
import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.Invite;
import za.co.bbd.grad.fupboard.cli.models.User;
import za.co.bbd.grad.fupboard.cli.services.UserService;

public class UserState implements NavState {
    private User user;
    
    public UserState(User user) {
        this.user = user;
    }

    @Override
    public String getLocation() {
        return Constants.BLUE + "My Profile" + Constants.RESET;
    }

    @Override
    public NavResponse handle(Scanner scanner) throws NavStateException {
        System.out.println(Constants.GREEN + user.toString() + Constants.RESET);
        System.out.println("0. Back");
        System.out.println("1. Edit Username");
        System.out.println("2. Edit Email");
        System.out.println("3. My Invites");

        System.out.print(Constants.INPUT_QUERY);
        
        int option = Integer.parseInt(scanner.nextLine());

        switch (option) {
            case 0:
                return NavResponse.back();
            case 1:
                System.out.print("New Username: ");
                var newUsername = scanner.nextLine().trim();
                if (newUsername.isEmpty()) {
                    return NavResponse.stay();
                }
                var newUser = UserService.updateUserMe(newUsername, null);
                return NavResponse.replace(new UserState(newUser));
            case 2:
                System.out.print("New Email: ");
                var newEmail = scanner.nextLine().trim();
                if (newEmail.isEmpty()) {
                    return NavResponse.stay();
                }
                var newUserWithEmail = UserService.updateUserMe(null, newEmail);
                return NavResponse.replace(new UserState(newUserWithEmail));
            case 3:
                return chooseFromInvites(UserService.getMyInvites(), scanner);
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
            System.out.println((i+1) + ". " + pi.toProjectString());
        }

        System.out.print("Invite: ");

        int inviteIndex = Integer.parseInt(scanner.nextLine());

        try {
            var pi = invites.get(inviteIndex-1);
            return NavResponse.push(new InviteState(pi, true));
        } catch (IndexOutOfBoundsException e) {
            throw new NavStateException(e);
        }
    }
}