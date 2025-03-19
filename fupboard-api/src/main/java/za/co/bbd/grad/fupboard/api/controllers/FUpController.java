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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import za.co.bbd.grad.fupboard.api.FupboardUtils;
import za.co.bbd.grad.fupboard.api.dbobjects.FUp;
import za.co.bbd.grad.fupboard.api.models.ApiError;
import za.co.bbd.grad.fupboard.api.models.CreateFUpRequest;
import za.co.bbd.grad.fupboard.api.models.UpdateFUpRequest;
import za.co.bbd.grad.fupboard.api.services.FUpService;
import za.co.bbd.grad.fupboard.api.services.ProjectService;
import za.co.bbd.grad.fupboard.api.services.UserService;

@RestController
public class FUpController {

    private final FUpService fUpService;
    private final ProjectService projectService;
    private final UserService userService;

    FUpController(UserService userService, ProjectService projectService, FUpService fUpService) {
        this.userService = userService;
        this.projectService = projectService;
        this.fUpService = fUpService;
    }
    
    @Transactional
    @GetMapping("/v1/projects/{projectId}/fups")
    public ResponseEntity<?> getFUps(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId) throws NotFoundException {
        var user = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) return ApiError.PROJECT_NOT_FOUND.response();

        var project = projectOpt.get();

        if (!projectService.allowedToReadProject(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        
        return ResponseEntity.ok(projectOpt.get().getfUps());
    }
    
    @Transactional
    @PostMapping("/v1/projects/{projectId}/fups")
    public ResponseEntity<?> createFUp(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @RequestBody CreateFUpRequest request) throws NotFoundException {
        var user = userService.getUserByJwt(jwt).get();
        var projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) return ApiError.PROJECT_NOT_FOUND.response();

        var project = projectOpt.get();

        if (!projectService.allowedToWriteProject(project, user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        if (request.getName() == null || request.getName().isEmpty() || request.getName().length() > FupboardUtils.LONG_NAME_LENGTH)
            return ApiError.VALIDATION.response("Name must be set, and must be between 1 and " + FupboardUtils.LONG_NAME_LENGTH + " characters long.");

        if (request.getDescription() == null || request.getDescription().length() > FupboardUtils.DESCRIPTION_LENGTH)
            return ApiError.VALIDATION.response("Description must be set, and must be between 1 and " + FupboardUtils.DESCRIPTION_LENGTH + " characters long.");
        
        if (!projectService.allowedToWriteProject(project, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        
        var fUp = new FUp(project, request.getName(), request.getDescription());

        fUp = fUpService.saveFUp(fUp);
        
        return ResponseEntity.ok(fUp);
    }

    @Transactional
    @GetMapping("/v1/projects/{projectId}/fups/{fUpId}")
    public ResponseEntity<?> getFUp(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int fUpId) {
        var user = userService.getUserByJwt(jwt).get();
        var fUpOpt = fUpService.getFUpById(fUpId);

        if (fUpOpt.isEmpty()) return ApiError.F_UP_NOT_FOUND.response();

        var fUp = fUpOpt.get();

        if (fUp.getProject().getProjectId() != projectId) return ApiError.PROJECT_NOT_FOUND.response();

        if (!fUpService.allowedToReadFUp(fUp, user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return ResponseEntity.ok(fUp);
    }

    @Transactional
    @GetMapping("/v1/projects/{projectId}/fups/{fUpId}/leaderboard")
    public ResponseEntity<?> getFUpLeaderboard(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int fUpId) {
        var user = userService.getUserByJwt(jwt).get();

        var fUpOpt = fUpService.getFUpById(fUpId);

        if (fUpOpt.isEmpty()) return ApiError.F_UP_NOT_FOUND.response();

        var fUp = fUpOpt.get();

        if (fUp.getProject().getProjectId() != projectId) return ApiError.PROJECT_NOT_FOUND.response();

        if (!fUpService.allowedToReadFUp(fUp, user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return ResponseEntity.ok(fUp.getLeaderboard());
    }

    @Transactional
    @PatchMapping("/v1/projects/{projectId}/fups/{fUpId}")
    public ResponseEntity<?> patchFUp(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int fUpId, @RequestBody UpdateFUpRequest request) {
        var user = userService.getUserByJwt(jwt).get();

        var fUpOpt = fUpService.getFUpById(fUpId);

        if (fUpOpt.isEmpty()) return ApiError.F_UP_NOT_FOUND.response();

        var fUp = fUpOpt.get();

        if (fUp.getProject().getProjectId() != projectId) return ApiError.PROJECT_NOT_FOUND.response();

        if (!fUpService.allowedToWriteFUp(fUp, user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        if (request.getName() != null && !request.getName().isEmpty()) {
            if (request.getName().length() > FupboardUtils.LONG_NAME_LENGTH) {
                return ApiError.VALIDATION.response("Name must be between 1 and " + FupboardUtils.LONG_NAME_LENGTH + " characters long.");
            }
            fUp.setfUpName(request.getName());
        }

        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            if (request.getDescription().length() > FupboardUtils.DESCRIPTION_LENGTH) {
                return ApiError.VALIDATION.response("Description must be between 1 and " + FupboardUtils.LONG_NAME_LENGTH + " characters long.");
            }
            fUp.setDescription(request.getDescription());
        }
        
        return ResponseEntity.ok(fUpService.saveFUp(fUp));
    }

    @Transactional
    @DeleteMapping("/v1/projects/{projectId}/fups/{fUpId}")
    public ResponseEntity<?> deleteFUp(@AuthenticationPrincipal Jwt jwt, @PathVariable int projectId, @PathVariable int fUpId) throws NotFoundException {
        var user = userService.getUserByJwt(jwt).get();

        var fUpOpt = fUpService.getFUpById(fUpId);

        if (fUpOpt.isEmpty()) return ApiError.F_UP_NOT_FOUND.response();

        var fUp = fUpOpt.get();

        if (fUp.getProject().getProjectId() != projectId) return ApiError.PROJECT_NOT_FOUND.response();

        if (!fUpService.allowedToDeleteFUp(fUp, user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        fUpService.deleteFUp(fUp);

        return ResponseEntity.noContent().build();
    }
}

