package za.co.bbd.grad.fupboard.api;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByGoogleId(String googleId);
}
