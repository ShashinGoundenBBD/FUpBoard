package za.co.bbd.grad.fupboard.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.bbd.grad.fupboard.api.dbobjects.Vote;

public interface VoteRepository extends JpaRepository<Vote, Integer> {
    Optional<Vote> findByVoteId(int voteId);
    Optional<Vote> findByReporterId(int reporterId);
    Optional<Vote> findByAccusedId(int accusedId);
}