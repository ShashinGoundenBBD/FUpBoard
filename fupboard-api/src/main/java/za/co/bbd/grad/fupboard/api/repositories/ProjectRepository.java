package za.co.bbd.grad.fupboard.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import za.co.bbd.grad.fupboard.api.dbobjects.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    List<Project> findAllByOwnerUserId(int userId);
    boolean existsByProjectIdAndOwnerUserId(int projectId, int userId);
    @Query("select distinct p from Project p left join p.invites i where p.owner.userId = :userId or (i.user.userId = :userId and i.accepted)")
    List<Project> findAllByOwnerOrCollaboratorUserId(int userId);
}
