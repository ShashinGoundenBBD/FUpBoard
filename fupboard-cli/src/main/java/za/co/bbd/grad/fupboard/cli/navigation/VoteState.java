package za.co.bbd.grad.fupboard.cli.navigation;

import java.util.Scanner;

import za.co.bbd.grad.fupboard.cli.common.Constants;
import za.co.bbd.grad.fupboard.cli.common.NavStateException;
import za.co.bbd.grad.fupboard.cli.models.FUp;
import za.co.bbd.grad.fupboard.cli.models.Project;
import za.co.bbd.grad.fupboard.cli.models.Vote;
import za.co.bbd.grad.fupboard.cli.services.VoteService;

public class VoteState implements NavState {
    private Project project;
    private FUp fUp;
    private Vote vote;

    public VoteState(Project project, FUp fUp, Vote vote) {
        this.project = project;
        this.fUp = fUp;
        this.vote = vote;
    }

    @Override
    public String getLocation() {
        return Constants.YELLOW + "Vote #" + vote.getVoteId() + Constants.RESET;
    }

    @Override
    public NavResponse handle(Scanner scanner) throws NavStateException {
        System.out.println(Constants.GREEN + vote + Constants.RESET);
        System.out.println("0. Back");
        System.out.println("1. Edit Score");
        System.out.println("2. Delete Vote");

        System.out.print(Constants.INPUT_QUERY);
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 0:
                return NavResponse.back();
            case 1:
                System.out.print("Score [" + vote.getScore() + "]: ");
                var newScoreString = scanner.nextLine();
                Integer newScore;
                try {
                    newScore = Integer.parseInt(newScoreString);
                } catch (NumberFormatException e) {
                    newScore = null;
                }

                if (newScore == null)
                    return NavResponse.stay();

                var newVote = VoteService.updateVote(project.getProjectId(), fUp.getfUpId(), vote.getVoteId(), newScore);
                return NavResponse.replace(new VoteState(project, fUp, newVote));
            case 2:
                VoteService.deleteVote(project.getProjectId(), fUp.getfUpId(), vote.getVoteId());
                return NavResponse.back();
            default:
                throw new NavStateException("Invalid option.");
        }
    }
}