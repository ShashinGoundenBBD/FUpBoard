package za.co.bbd.grad.fupboard.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.bbd.grad.fupboard.api.dbobjects.Commit;

public interface CommitRepository extends JpaRepository<Commit, Integer> {
    Optional<Commit> findByRepositoryId(int repositoryId);
    Optional<Commit> findByFUpId(int fUpId);
    Optional<Commit> findByCommitHash(String commitHash);
}