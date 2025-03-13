package za.co.bbd.grad.fupboard.api.controllers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import za.co.bbd.grad.fupboard.api.dbobjects.FUp;
import za.co.bbd.grad.fupboard.api.repositories.ProjectRepository;

@RestController
public class FUpController {
    @Autowired
    private ProjectRepository projectRepository;
    
    @GetMapping("/v1/projects/{projectId}/fups")
    public Set<FUp> getFUps(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId) throws NotFoundException {
        var projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        
        return projectOpt.get().getfUps();
    }
}

