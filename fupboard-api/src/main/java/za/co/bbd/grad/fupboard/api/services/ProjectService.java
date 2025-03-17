package za.co.bbd.grad.fupboard.api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import za.co.bbd.grad.fupboard.api.FupboardUtils;
import za.co.bbd.grad.fupboard.api.dbobjects.Project;
import za.co.bbd.grad.fupboard.api.dbobjects.User;
import za.co.bbd.grad.fupboard.api.repositories.ProjectInviteRepository;
import za.co.bbd.grad.fupboard.api.repositories.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectInviteRepository projectInviteRepository;

    private final ProjectRepository projectRepository;

    ProjectService(ProjectRepository projectRepository, ProjectInviteRepository projectInviteRepository) {
        this.projectRepository = projectRepository;
        this.projectInviteRepository = projectInviteRepository;
    }
    
    public List<Project> getProjectsOwnerOrCollaborator(User user) {
        return projectRepository.findAllByOwnerOrCollaboratorUserId(user.getUserId());
    }

    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    public boolean allowedToReadProject(Project project, User user) {
        return
            project.getOwner().getUserId() == user.getUserId() ||
            projectInviteRepository.existsByProjectAndUserAndAccepted(project, user, true) ||
            FupboardUtils.HasPermission("projects::read::all");
    }

    public boolean allowedToWriteProject(Project project, User user) {
        return
            project.getOwner().getUserId() == user.getUserId() ||
            projectInviteRepository.existsByProjectAndUserAndAccepted(project, user, true) ||
            FupboardUtils.HasPermission("projects::write::all");
    }

    public boolean allowedToDeleteProject(Project project, User user) {
        return
            project.getOwner().getUserId() == user.getUserId() ||
            FupboardUtils.HasPermission("projects::delete::all");
    }

    public Optional<Project> getProjectById(int projectId) {
        return projectRepository.findById(projectId);
    }

    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }
}
