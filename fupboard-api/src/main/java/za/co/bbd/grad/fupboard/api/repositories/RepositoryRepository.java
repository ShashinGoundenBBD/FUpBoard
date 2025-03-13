package za.co.bbd.grad.fupboard.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.bbd.grad.fupboard.api.dbobjects.Repository;

public interface RepositoryRepository extends JpaRepository<Repository, Integer> {
    Optional<Repository> findByRepositoryId(int repositoryId);
    Optional<Repository> findByProjectId(int projectId);
    Optional<Repository> findByRepositoryName(String repositoryName);
}