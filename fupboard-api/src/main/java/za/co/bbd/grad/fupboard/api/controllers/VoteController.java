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
import za.co.bbd.grad.fupboard.api.dbobjects.Vote;
import za.co.bbd.grad.fupboard.api.dbobjects.Project;
import za.co.bbd.grad.fupboard.api.models.*;
import za.co.bbd.grad.fupboard.api.services.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class VoteController {

    private final VoteService voteService;

    private final FUpController FUpController;

    private final FUpService fUpService;
    private final ProjectService projectService;
    private final UserService userService;

    VoteController(UserService userService, ProjectService projectService, FUpService fUpService, FUpController FUpController, VoteService voteService) {
        this.userService = userService;
        this.projectService = projectService;
        this.fUpService = fUpService;
        this.FUpController = FUpController;
        this.voteService = voteService;
    }
    
    @Transactional
    @GetMapping("/v1/projects/{projectId}/fups/{fUpId}/votes")
    public List<Vote> getVotes(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int fUpId) throws NotFoundException {
        var user = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        var project = projectOpt.get();

        if (!projectService.allowedToReadProject(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        var fUpOpt = fUpService.getFUpById(fUpId);
        if (fUpOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        var fUp = fUpOpt.get();
        
        return fUp.getVotes();
    }
    
    @Transactional
    @PostMapping("/v1/projects/{projectId}/fups/{fUpId}/votes")
    public Vote createVote(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int fUpId, @RequestBody CreateVoteRequest request) throws NotFoundException {
        var reporter = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        var project = projectOpt.get();

        if (!projectService.allowedToWriteProject(project, reporter)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // only the owner may invite people
        if (project.getOwner().getUserId() != reporter.getUserId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        var fUpOpt = fUpService.getFUpById(fUpId);
        if (fUpOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        var fUp = fUpOpt.get();

        if (request.getAccusedUsername() == null || request.getAccusedUsername().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        if (request.getScore() < 1 || request.getScore() > 5)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        
        var accusedOpt = userService.getUserByUsername(request.getAccusedUsername());

        if (accusedOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    
        var accused = accusedOpt.get();
        
        var vote = new Vote(reporter, accused, fUp, request.getScore());

        vote = voteService.saveVote(vote);
        
        return vote;
    }
    
    @Transactional
    @GetMapping("/v1/projects/{projectId}/fups/{fUpId}/votes/{voteId}")
    public Vote getVotes(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int fUpId, @PathVariable int voteId) throws NotFoundException {
        var user = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        var project = projectOpt.get();

        if (!projectService.allowedToReadProject(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        var fUpOpt = fUpService.getFUpById(fUpId);
        if (fUpOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        var voteOpt = voteService.getVoteById(voteId);
        if (voteOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        
        return voteOpt.get();
    }

    @Transactional
    @PatchMapping("/v1/projects/{projectId}/fups/{fUpId}/votes/{voteId}")
    public Vote patchVote(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int fUpId, @PathVariable int voteId, @RequestBody UpdateVoteRequest request) {
        var reporter = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        var project = projectOpt.get();

        if (!projectService.allowedToWriteProject(project, reporter)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        
        var fUpOpt = fUpService.getFUpById(fUpId);
        if (fUpOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        if (request.getScore() < 1 || request.getScore() > 5)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        
        var voteOpt = voteService.getVoteById(voteId);
        if (voteOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    
        var vote = voteOpt.get();

        // only the reporter by change their vote
        if (vote.getReporter().getUserId() != reporter.getUserId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        vote.setScore(request.getScore());

        vote = voteService.saveVote(vote);
        
        return vote;
    }

    @Transactional
    @DeleteMapping("/v1/projects/{projectId}/fups/{fUpId}/votes/{voteId}")
    public void deleteVote(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int fUpId, @PathVariable int voteId) throws NotFoundException {
        var reporter = userService.getUserByJwt(jwt).get();

        var projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        var project = projectOpt.get();

        var fUpOpt = fUpService.getFUpById(fUpId);
        if (fUpOpt.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        var voteOpt = voteService.getVoteById(voteId);
        if (voteOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    
        var vote = voteOpt.get();

        // only the owner or the reporter by delete their vote
        if (project.getOwner().getUserId() != reporter.getUserId() && vote.getReporter().getUserId() != reporter.getUserId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        voteService.deleteVote(vote);
    }
}
