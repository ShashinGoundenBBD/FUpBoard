package za.co.bbd.grad.fupboard.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.oauth2.jwt.Jwt;

import za.co.bbd.grad.fupboard.api.dbobjects.User;


public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByEmailIgnoreCaseAndEmailVerifiedIsTrue(String email);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    
    default Optional<User> findByEmailIfVerified(String email) {
        return findByEmailIgnoreCaseAndEmailVerifiedIsTrue(email);
    }
}
