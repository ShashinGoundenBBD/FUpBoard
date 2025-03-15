package za.co.bbd.grad.fupboard.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.bbd.grad.fupboard.api.dbobjects.Project;
import za.co.bbd.grad.fupboard.api.dbobjects.ProjectInvite;
import za.co.bbd.grad.fupboard.api.dbobjects.User;

public interface ProjectInviteRepository extends JpaRepository<ProjectInvite, Integer> {
    boolean existsByProjectAndUserAndAccepted(Project project, User user, boolean accepted);

    Optional<ProjectInvite> findByProjectAndUser(Project project, User user);
}