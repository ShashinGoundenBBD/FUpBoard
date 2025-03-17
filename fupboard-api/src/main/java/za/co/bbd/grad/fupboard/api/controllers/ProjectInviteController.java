package za.co.bbd.grad.fupboard.api.controllers;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import za.co.bbd.grad.fupboard.api.FupboardUtils;
import za.co.bbd.grad.fupboard.api.dbobjects.ProjectInvite;
import za.co.bbd.grad.fupboard.api.dbobjects.Project;
import za.co.bbd.grad.fupboard.api.models.CreateProjectInviteRequest;
import za.co.bbd.grad.fupboard.api.models.CreateProjectRequest;
import za.co.bbd.grad.fupboard.api.models.UpdateProjectInviteRequest;
import za.co.bbd.grad.fupboard.api.models.UpdateProjectRequest;
import za.co.bbd.grad.fupboard.api.repositories.ProjectRepository;
import za.co.bbd.grad.fupboard.api.services.ProjectInviteService;
import za.co.bbd.grad.fupboard.api.services.ProjectService;
import za.co.bbd.grad.fupboard.api.services.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class ProjectInviteController {

    private final ProjectInviteService projectInviteService;

    private final ProjectService projectService;
    private final UserService userService;

    ProjectInviteController(UserService userService, ProjectService projectService, ProjectInviteService projectInviteService) {
        this.userService = userService;
        this.projectService = projectService;
        this.projectInviteService = projectInviteService;
    }
    


    @Transactional
    @GetMapping("/v1/projects/{projectId}/invites")
    public List<ProjectInvite> getProjectInvites(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId) throws NotFoundException {
        var user = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        var project = projectOpt.get();

        if (!projectService.allowedToReadProject(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // Fix: Ensure the list is never null
        return project.getInvites() != null ? project.getInvites() : List.of();
    }

    
    @Transactional
    @PostMapping("/v1/projects/{projectId}/invites")
    public ProjectInvite createProjectInvite(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @RequestBody CreateProjectInviteRequest request) throws NotFoundException {
        var requester = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        var project = projectOpt.get();

        // only the owner may invite people
        if (project.getOwner().getUserId() != requester.getUserId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        if (request.getUsername() == null || request.getUsername().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        
        var inviteeOpt = userService.getUserByUsername(request.getUsername());

        if (inviteeOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    
        var invitee = inviteeOpt.get();
        
        var projectInvite = new ProjectInvite(project, invitee, false);

        projectInvite = projectInviteService.saveProjectInvite(projectInvite);
        
        return projectInvite;
    }

    @Transactional
    @PatchMapping("/v1/projects/{projectId}/invites/{projectInviteId}")
    public ProjectInvite patchProjectInvite(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int projectInviteId, @RequestBody UpdateProjectInviteRequest request) {
        var requester = userService.getUserByJwt(jwt).get();

        var projectInviteOpt = projectInviteService.getProjectInviteById(projectInviteId);

        if (projectInviteOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        var projectInvite = projectInviteOpt.get();

        if (requester.getUserId() != projectInvite.getUser().getUserId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        projectInvite.setAccepted(request.isAccepted());

        return projectInviteService.saveProjectInvite(projectInvite);
    }

    @Transactional
    @DeleteMapping("/v1/projects/{projectId}/invites/{projectInviteId}")
    public void deleteProjectInvite(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int projectInviteId) throws NotFoundException {
        var requester = userService.getUserByJwt(jwt).get();

        var projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        var project = projectOpt.get();

        var projectInviteOpt = projectInviteService.getProjectInviteById(projectInviteId);
        if (projectInviteOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        var projectInvite = projectInviteOpt.get();

        if (requester.getUserId() != project.getOwner().getUserId() && requester.getUserId() != projectInvite.getUser().getUserId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        projectInviteService.deleteProjectInvite(projectInviteOpt.get());
    }
}
