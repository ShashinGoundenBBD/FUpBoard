package za.co.bbd.grad.fupboard.api;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.oauth2.jwt.Jwt;


public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByEmailIgnoreCaseAndEmailVerifiedIsTrue(String email);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    
    default Optional<User> findByEmailIfVerified(String email) {
        return findByEmailIgnoreCaseAndEmailVerifiedIsTrue(email);
    }

    default Optional<User> findByJwt(Jwt jwt) {
        var user = findByGoogleId(jwt.getSubject());
        if (user.isPresent()) return user;
        
        String email = jwt.getClaimAsString("email");
        Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");
        
        if (email != null && emailVerified) {
            user = findByEmailIfVerified(email);
        }
        
        return user;
    }
}
