package za.co.bbd.grad.fupboard.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.bbd.grad.fupboard.api.dbobjects.FUp;
import za.co.bbd.grad.fupboard.api.dbobjects.User;
import za.co.bbd.grad.fupboard.api.dbobjects.Vote;

public interface VoteRepository extends JpaRepository<Vote, Integer> {
    public boolean existsByFUpAndAccused(FUp fUp, User accused);
}
