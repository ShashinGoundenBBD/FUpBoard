package za.co.bbd.grad.fupboard.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.bbd.grad.fupboard.api.dbobjects.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    List<Project> findAllByOwnerUserId(int userId);
    boolean existsByProjectIdAndOwnerUserId(int projectId, int userId);
}
