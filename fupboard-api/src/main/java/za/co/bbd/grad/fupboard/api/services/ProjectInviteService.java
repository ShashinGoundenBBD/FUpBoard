package za.co.bbd.grad.fupboard.api.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import za.co.bbd.grad.fupboard.api.dbobjects.Project;
import za.co.bbd.grad.fupboard.api.dbobjects.ProjectInvite;
import za.co.bbd.grad.fupboard.api.dbobjects.User;
import za.co.bbd.grad.fupboard.api.repositories.ProjectInviteRepository;

@Service
public class ProjectInviteService {

    private final ProjectInviteRepository projectInviteRepository;

    ProjectInviteService(ProjectInviteRepository projectInviteRepository) {
        this.projectInviteRepository = projectInviteRepository;
    }

    public ProjectInvite saveProjectInvite(ProjectInvite projectInvite) {
        return projectInviteRepository.save(projectInvite);
    }

    public Optional<ProjectInvite> getProjectInviteById(int projectInviteId) {
        return projectInviteRepository.findById(projectInviteId);
    }

    public Optional<ProjectInvite> getProjectInviteByProjectAndUser(Project project, User user) {
        return projectInviteRepository.findByProjectAndUser(project, user);
    }

    public void deleteProjectInvite(ProjectInvite projectInvite) {
        projectInviteRepository.delete(projectInvite);
    }

    public boolean isUserInvited(Project project, User invitee) {
        return projectInviteRepository.existsByProjectAndUser(project, invitee);
    }
}
