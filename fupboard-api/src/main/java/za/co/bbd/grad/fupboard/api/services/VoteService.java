package za.co.bbd.grad.fupboard.api.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import za.co.bbd.grad.fupboard.api.dbobjects.FUp;
import za.co.bbd.grad.fupboard.api.dbobjects.User;
import za.co.bbd.grad.fupboard.api.dbobjects.Vote;
import za.co.bbd.grad.fupboard.api.repositories.VoteRepository;

@Service
public class VoteService {

    private final VoteRepository voteRepository;

    VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public Vote saveVote(Vote vote) {
        return voteRepository.save(vote);
    }

    public void deleteVote(Vote vote) {
        voteRepository.delete(vote);
    }

    public Optional<Vote> getVoteById(int voteId) {
        return voteRepository.findById(voteId);
    }

    public boolean voteExists(FUp fUp, User reporter, User accused) {
        return voteRepository.existsByFUpAndReporterAndAccused(fUp, reporter, accused);
    }
}
