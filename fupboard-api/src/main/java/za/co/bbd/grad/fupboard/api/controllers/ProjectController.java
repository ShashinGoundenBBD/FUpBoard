package za.co.bbd.grad.fupboard.api.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import za.co.bbd.grad.fupboard.api.FupboardUtils;
import za.co.bbd.grad.fupboard.api.dbobjects.Project;
import za.co.bbd.grad.fupboard.api.models.CreateProjectRequest;
import za.co.bbd.grad.fupboard.api.models.UpdateProjectRequest;
import za.co.bbd.grad.fupboard.api.repositories.ProjectRepository;
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

    @GetMapping("/v1/projects")
    public List<Project> getProjects(@AuthenticationPrincipal Jwt jwt) {
        var user = userService.getUserByJwt(jwt).get();
        return projectService.getProjectsForOwner(user);
    }

    @PostMapping("/v1/projects")
    public Project createProject(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateProjectRequest request) {
        var user = userService.getUserByJwt(jwt).get();

        var name = request.getName();

        if (name == null || name.isEmpty() || name.length() > FupboardUtils.SHORT_NAME_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        var project = new Project(request.getName(), user);
        project = projectService.saveProject(project);
        
        return project;
    }
    
    @GetMapping("/v1/projects/{projectId}")
    public Project getProject(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId) {
        var user = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);

        if (projectOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var project = projectOpt.get();
        
        if (!projectService.allowedToReadProject(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return project;
    }
    
    @PatchMapping("/v1/projects/{projectId}")
    public Project patchProject(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @RequestBody UpdateProjectRequest request) {
        var user = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);

        if (projectOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var project = projectOpt.get();
        
        if (!projectService.allowedToWriteProject(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        var name = request.getName();

        if (name != null && !name.isEmpty()) {
            if (name.length() > FupboardUtils.SHORT_NAME_LENGTH) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            project.setProjectName(name);
        }

        project = projectService.saveProject(project);

        return project;
    }

    @DeleteMapping("/v1/projects/{projectId}")
    public void deleteProject(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @RequestBody UpdateProjectRequest request) {
        var user = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);

        if (projectOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var project = projectOpt.get();
        
        if (!projectService.allowedToDeleteProject(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        projectService.deleteProject(project);
    }
}

