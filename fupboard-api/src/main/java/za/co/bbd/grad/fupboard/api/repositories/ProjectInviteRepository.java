package za.co.bbd.grad.fupboard.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.bbd.grad.fupboard.api.dbobjects.ProjectInvite;

public interface ProjectInviteRepository extends JpaRepository<ProjectInvite, Integer> {
    Optional<ProjectInvite> findByUserId(int userId);
    Optional<ProjectInvite> findByProjectId(int projectId);
}