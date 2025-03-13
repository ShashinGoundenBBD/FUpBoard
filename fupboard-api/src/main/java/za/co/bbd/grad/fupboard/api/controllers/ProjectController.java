package za.co.bbd.grad.fupboard.api.controllers;

import java.util.List;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import za.co.bbd.grad.fupboard.api.repositories.UserRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class ProjectController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    
    @GetMapping("/v1/projects")
    public List<Project> getProjects(@AuthenticationPrincipal Jwt jwt) {
        return projectRepository.findByOwnerGoogleId(jwt.getSubject());
    }

    @PostMapping("/v1/projects")
    public Project createProject(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateProjectRequest request) {
        var user = userRepository.findByJwt(jwt).get();

        var name = request.getProjectName();

        if (name == null || name.isEmpty() || name.length() > FupboardUtils.SHORT_NAME_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        var project = new Project(request.getProjectName(), user);
        
        project = projectRepository.save(project);
        
        return project;
    }
    
    @GetMapping("/v1/projects/{projectId}")
    public Project getProject(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId) {
        var projectOpt = projectRepository.findById(projectId);

        if (projectOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        
        if (!projectOpt.get().getOwner().getGoogleId().equals(jwt.getSubject())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return projectOpt.get();
    }
    
    @PatchMapping("/v1/projects/{projectId}")
    public Project patchProject(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @RequestBody UpdateProjectRequest request) {
        var projectOpt = projectRepository.findById(projectId);

        if (projectOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var project = projectOpt.get();
        
        if (!project.getOwner().getGoogleId().equals(jwt.getSubject())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        var name = project.getProjectName();

        if (name.isEmpty() || name.length() > FupboardUtils.SHORT_NAME_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        project.setProjectName(request.getProjectName());

        return projectOpt.get();
    }

    @DeleteMapping("/v1/projects/{projectId}")
    public void deleteProject(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @RequestBody UpdateProjectRequest request) {
        var projectOpt = projectRepository.findById(projectId);

        if (projectOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var project = projectOpt.get();
        
        if (!project.getOwner().getGoogleId().equals(jwt.getSubject())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        projectRepository.delete(project);
    }
}

