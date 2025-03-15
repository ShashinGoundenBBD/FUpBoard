package za.co.bbd.grad.fupboard.api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import za.co.bbd.grad.fupboard.api.FupboardUtils;
import za.co.bbd.grad.fupboard.api.dbobjects.Project;
import za.co.bbd.grad.fupboard.api.dbobjects.User;
import za.co.bbd.grad.fupboard.api.repositories.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getProjectsForOwner(User user) {
        return projectRepository.findAllByOwnerUserId(user.getUserId());
    }

    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    public boolean allowedToReadProject(Project project, User user) {
        return
            project.getOwner().getUserId() == user.getUserId() ||
            // todo: check if collaborator
            FupboardUtils.HasPermission("projects::read::all");
    }

    public boolean allowedToWriteProject(Project project, User user) {
        return
            project.getOwner().getUserId() == user.getUserId() ||
            // todo: check if collaborator
            FupboardUtils.HasPermission("projects::write::all");
    }

    public boolean allowedToDeleteProject(Project project, User user) {
        return
            project.getOwner().getUserId() == user.getUserId() ||
            // todo: check if collaborator
            FupboardUtils.HasPermission("projects::delete::all");
    }

    public Optional<Project> getProjectById(int projectId) {
        return projectRepository.findById(projectId);
    }

    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }
}
