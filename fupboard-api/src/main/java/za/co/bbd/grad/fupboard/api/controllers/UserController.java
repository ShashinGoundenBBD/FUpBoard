package za.co.bbd.grad.fupboard.api.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import za.co.bbd.grad.fupboard.api.dbobjects.User;
import za.co.bbd.grad.fupboard.api.models.UserUpdateRequest;
import za.co.bbd.grad.fupboard.api.repositories.UserRepository;
import za.co.bbd.grad.fupboard.api.services.UserService;

@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/v1/users/me")
    public User getUserMe(@AuthenticationPrincipal Jwt jwt) {
        return userService.getUserByJwt(jwt).get();
    }

    @Transactional
    @PatchMapping("/v1/users/me")
    public User updateUserMe(@AuthenticationPrincipal Jwt jwt, @RequestBody UserUpdateRequest update) throws URISyntaxException, IOException, InterruptedException {
        var user = userService.getUserByJwt(jwt).get();

        update.setEmail(update.getEmail().toLowerCase());

        try {
            if (update.getEmail() != null && !update.getEmail().equals(user.getEmail())) {
                user.setEmail(update.getEmail());
    
                var jwtEmail = jwt.getClaimAsString("email");
                boolean jwtEmailVerified = jwt.getClaimAsBoolean("email_verified");
    
                var verified = update.getEmail().equals(jwtEmail) && jwtEmailVerified;
                user.setEmailVerified(verified);
            }
            if (update.getUsername() != null && !update.getUsername().equals(user.getUsername())) {
                user.setUsername(update.getUsername());
            }
            userService.saveUser(user);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        
        return user;
    }
}

