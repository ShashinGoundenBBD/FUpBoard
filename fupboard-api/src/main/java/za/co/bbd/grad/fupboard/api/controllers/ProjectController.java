package za.co.bbd.grad.fupboard.api.controllers;

import java.util.List;
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
import za.co.bbd.grad.fupboard.api.FupboardUtils;
import za.co.bbd.grad.fupboard.api.dbobjects.Project;
import za.co.bbd.grad.fupboard.api.models.ApiError;
import za.co.bbd.grad.fupboard.api.models.CreateProjectRequest;
import za.co.bbd.grad.fupboard.api.models.UpdateProjectRequest;
import za.co.bbd.grad.fupboard.api.services.ProjectService;
import za.co.bbd.grad.fupboard.api.services.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class ProjectController {
    private final ProjectService projectService;
    private final UserService userService;

    ProjectController(UserService userService, ProjectService projectService) {
        this.userService = userService;
        this.projectService = projectService;
    }

    @Transactional
    @GetMapping("/v1/projects")
    public List<Project> getProjects(@AuthenticationPrincipal Jwt jwt) {
        var user = userService.getUserByJwt(jwt).get();
        return projectService.getProjectsOwnerOrCollaborator(user);
    }

    @Transactional
    @PostMapping("/v1/projects")
    public ResponseEntity<?> createProject(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateProjectRequest request) {
        var user = userService.getUserByJwt(jwt).get();

        var name = request.getName();

        if (name == null || name.isEmpty() || name.length() > FupboardUtils.SHORT_NAME_LENGTH) {
            return ApiError.VALIDATION.response("Name must be set, and be between 1 and " + FupboardUtils.SHORT_NAME_LENGTH + " characters.");
        }

        var project = new Project(request.getName(), user);
        project = projectService.saveProject(project);
        
        return ResponseEntity.ok(project);
    }
    
    @Transactional
    @GetMapping("/v1/projects/{projectId}")
    public ResponseEntity<?> getProject(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId) {
        var user = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);

        if (projectOpt.isEmpty()) {
            return ApiError.PROJECT_NOT_FOUND.response();
        }

        var project = projectOpt.get();
        
        if (!projectService.allowedToReadProject(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(project);
    }
    
    @Transactional
    @GetMapping("/v1/projects/{projectId}/leaderboard")
    public ResponseEntity<?> getProjectLeaderboard(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId) {
        var user = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);

        if (projectOpt.isEmpty()) {
            return ApiError.PROJECT_NOT_FOUND.response();
        }

        var project = projectOpt.get();
        
        if (!projectService.allowedToReadProject(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(project.getLeaderboard());
    }
    
    @Transactional
    @PatchMapping("/v1/projects/{projectId}")
    public ResponseEntity<?> patchProject(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @RequestBody UpdateProjectRequest request) {
        var user = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);

        if (projectOpt.isEmpty()) {
            return ApiError.PROJECT_NOT_FOUND.response();
        }

        var project = projectOpt.get();
        
        if (!projectService.allowedToWriteProject(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        var name = request.getName();

        if (name != null && !name.isEmpty()) {
            if (name.length() > FupboardUtils.SHORT_NAME_LENGTH) {
                return ApiError.VALIDATION.response("Name should be between 1 and " + FupboardUtils.SHORT_NAME_LENGTH + " characters long.");
            }
            project.setProjectName(name);
        }

        project = projectService.saveProject(project);

        return ResponseEntity.ok(project);
    }

    @Transactional
    @DeleteMapping("/v1/projects/{projectId}")
    public ResponseEntity<?> deleteProject(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @RequestBody UpdateProjectRequest request) {
        var user = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);

        if (projectOpt.isEmpty())
            return ApiError.PROJECT_NOT_FOUND.response();

        var project = projectOpt.get();
        
        if (!projectService.allowedToDeleteProject(project, user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        projectService.deleteProject(project);

        return ResponseEntity.noContent().build();
    }
}
