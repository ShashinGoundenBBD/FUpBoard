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
import za.co.bbd.grad.fupboard.api.dbobjects.Vote;
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
    public ResponseEntity<?> getVotes(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int fUpId) throws NotFoundException {
        var user = userService.getUserByJwt(jwt).get();

        var fUpOpt = fUpService.getFUpById(fUpId);
        if (fUpOpt.isEmpty()) return ApiError.F_UP_NOT_FOUND.response();

        var fUp = fUpOpt.get();

        if (fUp.getProject().getProjectId() != projectId) return ApiError.PROJECT_NOT_FOUND.response();

        if (!fUpService.allowedToReadFUp(fUp, user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        return ResponseEntity.ok(fUp.getVotes());
    }
    
    @Transactional
    @PostMapping("/v1/projects/{projectId}/fups/{fUpId}/votes")
    public ResponseEntity<?> createVote(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int fUpId, @RequestBody CreateVoteRequest request) throws NotFoundException {
        var reporter = userService.getUserByJwt(jwt).get();
        
        var fUpOpt = fUpService.getFUpById(fUpId);
        if (fUpOpt.isEmpty()) return ApiError.F_UP_NOT_FOUND.response();

        var fUp = fUpOpt.get();

        if (fUp.getProject().getProjectId() != projectId) return ApiError.PROJECT_NOT_FOUND.response();

        if (!fUpService.allowedToWriteFUp(fUp, reporter))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        // Vlidation
        if (request.getAccusedUsername() == null || request.getAccusedUsername().isEmpty())
            return ApiError.VALIDATION.response("`accusedUsername` must be non-empty");

        if (request.getScore() < 1 || request.getScore() > 5)
            return ApiError.VALIDATION.response("`score` must be between 1 and 5");
        
        var accusedOpt = userService.getUserByUsername(request.getAccusedUsername());

        if (accusedOpt.isEmpty())
            return ApiError.USER_NOT_FOUND.response();
    
        var accused = accusedOpt.get();
        
        var vote = new Vote(reporter, accused, fUp, request.getScore());

        vote = voteService.saveVote(vote);
        
        return ResponseEntity.ok(vote);
    }
    
    @Transactional
    @GetMapping("/v1/projects/{projectId}/fups/{fUpId}/votes/{voteId}")
    public ResponseEntity<?> getVote(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int fUpId, @PathVariable int voteId) throws NotFoundException {
        var user = userService.getUserByJwt(jwt).get();

        var voteOpt = voteService.getVoteById(voteId);
        if (voteOpt.isEmpty())
            return ApiError.VOTE_NOT_FOUND.response();
        
        var vote = voteOpt.get();

        if (vote.getfUp().getfUpId() != fUpId) return ApiError.F_UP_NOT_FOUND.response();

        if (vote.getfUp().getProject().getProjectId() != projectId) return ApiError.PROJECT_NOT_FOUND.response();

        if (!fUpService.allowedToReadFUp(vote.getfUp(), user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        return ResponseEntity.ok(vote);
    }

    @Transactional
    @PatchMapping("/v1/projects/{projectId}/fups/{fUpId}/votes/{voteId}")
    public ResponseEntity<?> patchVote(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int fUpId, @PathVariable int voteId, @RequestBody UpdateVoteRequest request) {
        var user = userService.getUserByJwt(jwt).get();
        
        var voteOpt = voteService.getVoteById(voteId);
        if (voteOpt.isEmpty())
            return ApiError.VOTE_NOT_FOUND.response();
    
        var vote = voteOpt.get();

        if (vote.getfUp().getfUpId() != fUpId) return ApiError.F_UP_NOT_FOUND.response();

        if (vote.getfUp().getProject().getProjectId() != projectId) return ApiError.PROJECT_NOT_FOUND.response();

        if (!fUpService.allowedToWriteFUp(vote.getfUp(), user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        // only the reporter by change their vote
        if (vote.getReporter().getUserId() != user.getUserId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        if (request.getScore() < 1 || request.getScore() > 5)
            return ApiError.VALIDATION.response("`score` must be between 1 and 5");
        
        vote.setScore(request.getScore());

        vote = voteService.saveVote(vote);
        
        return ResponseEntity.ok(vote);
    }

    @Transactional
    @DeleteMapping("/v1/projects/{projectId}/fups/{fUpId}/votes/{voteId}")
    public ResponseEntity<?> deleteVote(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int fUpId, @PathVariable int voteId) throws NotFoundException {
        var user = userService.getUserByJwt(jwt).get();

        var voteOpt = voteService.getVoteById(voteId);
        if (voteOpt.isEmpty())
            return ApiError.VOTE_NOT_FOUND.response();
    
        var vote = voteOpt.get();

        if (vote.getfUp().getfUpId() != fUpId) return ApiError.F_UP_NOT_FOUND.response();

        if (vote.getfUp().getProject().getProjectId() != projectId) return ApiError.PROJECT_NOT_FOUND.response();

        if (!fUpService.allowedToWriteFUp(vote.getfUp(), user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        // only the owner or the reporter by delete their vote
        if (vote.getfUp().getProject().getOwner().getUserId() != user.getUserId() && vote.getReporter().getUserId() != user.getUserId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        voteService.deleteVote(vote);
        
        return ResponseEntity.noContent().build();
    }
}
