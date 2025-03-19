package za.co.bbd.grad.fupboard.api.controllers;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import za.co.bbd.grad.fupboard.api.dbobjects.ProjectInvite;
import za.co.bbd.grad.fupboard.api.models.ApiError;
import za.co.bbd.grad.fupboard.api.models.CreateProjectInviteRequest;
import za.co.bbd.grad.fupboard.api.models.UpdateProjectInviteRequest;
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
    public ResponseEntity<?> getProjectInvites(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId) throws NotFoundException {
        var user = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) return ApiError.PROJECT_NOT_FOUND.response();

        var project = projectOpt.get();

        if (!projectService.allowedToReadProject(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        
        return ResponseEntity.ok(projectOpt.get().getInvites());
    }
    
    @Transactional
    @PostMapping("/v1/projects/{projectId}/invites")
    public ResponseEntity<?> createProjectInvite(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @RequestBody CreateProjectInviteRequest request) throws NotFoundException {
        var requester = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) return ApiError.PROJECT_NOT_FOUND.response();

        var project = projectOpt.get();

        // only the owner may invite people
        if (project.getOwner().getUserId() != requester.getUserId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        if (request.getUsername() == null || request.getUsername().isEmpty())
            return ApiError.VALIDATION.response("Username not set.");
        
        var inviteeOpt = userService.getUserByUsername(request.getUsername());

        if (inviteeOpt.isEmpty())
            return ApiError.USER_NOT_FOUND.response();
    
        var invitee = inviteeOpt.get();

        if (invitee.getUserId() == requester.getUserId())
            return ApiError.VALIDATION.response("You cannot invite yourself.");
        
        if (projectInviteService.isUserInvited(project, invitee))
            return ApiError.USER_ALREADY_INVITED.response();
        
        var projectInvite = new ProjectInvite(project, invitee, false);

        projectInvite = projectInviteService.saveProjectInvite(projectInvite);
        
        return ResponseEntity.ok(projectInvite);
    }

    @Transactional
    @PatchMapping("/v1/projects/{projectId}/invites/{projectInviteId}")
    public ResponseEntity<?> patchProjectInvite(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int projectInviteId, @RequestBody UpdateProjectInviteRequest request) {
        var requester = userService.getUserByJwt(jwt).get();

        var projectInviteOpt = projectInviteService.getProjectInviteById(projectInviteId);

        if (projectInviteOpt.isEmpty())
            return ApiError.INVITE_NOT_FOUND.response();

        var projectInvite = projectInviteOpt.get();

        if (projectInvite.getProject().getProjectId() != projectId)
            return ApiError.PROJECT_NOT_FOUND.response();

        // only the invitee may accept
        if (requester.getUserId() != projectInvite.getUser().getUserId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        projectInvite.setAccepted(request.isAccepted());

        return ResponseEntity.ok(projectInviteService.saveProjectInvite(projectInvite));
    }

    @Transactional
    @DeleteMapping("/v1/projects/{projectId}/invites/{projectInviteId}")
    public ResponseEntity<?> deleteProjectInvite(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int projectInviteId) throws NotFoundException {
        var requester = userService.getUserByJwt(jwt).get();

        var projectInviteOpt = projectInviteService.getProjectInviteById(projectInviteId);
        if (projectInviteOpt.isEmpty())
            return ApiError.INVITE_NOT_FOUND.response();

        var projectInvite = projectInviteOpt.get();

        if (projectInvite.getProject().getProjectId() != projectId)
            return ApiError.PROJECT_NOT_FOUND.response();

        // either the owner or the invitee can delete an invite
        if (requester.getUserId() != projectInvite.getProject().getOwner().getUserId() && requester.getUserId() != projectInvite.getUser().getUserId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        projectInviteService.deleteProjectInvite(projectInviteOpt.get());

        return ResponseEntity.noContent().build();
    }
}
