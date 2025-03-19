package za.co.bbd.grad.fupboard.api.services;

import java.util.Optional;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import za.co.bbd.grad.fupboard.api.dbobjects.User;
import za.co.bbd.grad.fupboard.api.repositories.UserRepository;

@Service
public class UserService {
    public static final String USERNAME_REGEX = "^[\\w-\\.]+$";
    public static final String USERNAME_BAD_CHARS_REGEX = "[^\\w-\\.]";
    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserByJwt(Jwt jwt) {
        var user = userRepository.findByGoogleId(jwt.getSubject());
        if (user.isPresent()) return user;
        
        String email = jwt.getClaimAsString("email");
        Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");
        
        if (email != null && emailVerified) {
            user = userRepository.findByEmailIfVerified(email);
        }
        
        return user;
    }
    
    public Optional<User> getUserByEmailIfVerified(String email) {
        return userRepository.findByEmailIfVerified(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
