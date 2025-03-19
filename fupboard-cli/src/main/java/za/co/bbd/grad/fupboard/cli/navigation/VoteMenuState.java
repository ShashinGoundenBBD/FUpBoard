package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.List;
import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.FUp;
import za.co.bbd.grad.fupboard.cli.models.Project;
import za.co.bbd.grad.fupboard.cli.models.Vote;
import za.co.bbd.grad.fupboard.cli.services.FUpService;
import za.co.bbd.grad.fupboard.cli.services.VoteService;

public class VoteMenuState implements NavState {
    private Project project;
    private FUp fUp;

    public VoteMenuState(Project project, FUp fUp) {
        this.project = project;
        this.fUp = fUp;
    }

    @Override
    public String getLocation() {
        return Constants.BLUE + "Votes" + Constants.RESET;
    }

    @Override
    public NavResponse handle(Scanner scanner) throws NavStateException {
        System.out.println("0. Back.");
        System.out.println("1. View Summary");
        System.out.println("2. Create Vote");
        System.out.println("3. View Vote");

        System.out.print(Constants.INPUT_QUERY);
        var choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 0:
                return NavResponse.back();
            case 1:
                var leaderboard = FUpService.getFUpLeaderboard(project.getProjectId(), fUp.getfUpId());
                System.out.println(leaderboard);
                return NavResponse.stay();
            case 2:
                System.out.print("Accused: ");
                var accused = scanner.nextLine();
                System.out.print("Score: ");
                var score = Integer.parseInt(scanner.nextLine());
                var newVote = VoteService.createVote(project.getProjectId(), fUp.getfUpId(), accused, score);
                return NavResponse.push(new VoteState(project, fUp, newVote));
            case 3:
                return chooseFromVotes(VoteService.getVotes(project.getProjectId(), fUp.getfUpId()), scanner);
            default:
                throw new NavStateException("Invalid option.");
        }
    }

    private NavResponse chooseFromVotes(List<Vote> votes, Scanner scanner) throws NavStateException {
        if (votes.isEmpty()) {
            System.out.println("No votes.");
            return NavResponse.stay();
        }

        System.out.println("0. Back");
        for (int i = 0; i < votes.size(); i++) {
            var v = votes.get(i);
            System.out.println((i+1) + ". " + v);
        }

        System.out.print("Vote: ");

        int voteIndex = Integer.parseInt(scanner.nextLine());

        if (voteIndex == 0) {
            return NavResponse.stay();
        }

        try {
            var v = votes.get(voteIndex-1);
            return NavResponse.push(new VoteState(project, fUp, v));
        } catch (IndexOutOfBoundsException e) {
            throw new NavStateException(e);
        }
    }
}
