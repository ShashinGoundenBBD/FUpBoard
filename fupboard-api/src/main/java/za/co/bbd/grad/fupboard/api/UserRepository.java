package za.co.bbd.grad.fupboard.api;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findByEmailIgnoreCaseAndEmailVerifiedIsTrue(String email);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    
    default Optional<User> findByEmailIfVerified(String email) {
        return findByEmailIgnoreCaseAndEmailVerifiedIsTrue(email);
    }
}
