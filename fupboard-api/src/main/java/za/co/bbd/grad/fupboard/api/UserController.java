package za.co.bbd.grad.fupboard.api;

import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {
	@Autowired
	private UserRepository userRepository;
    
	@GetMapping("/v1/users/me")
	public User get_me(@AuthenticationPrincipal Jwt jwt) {
		return userRepository.findByJwt(jwt).get();
	}

	@PatchMapping("/v1/users/me")
	public User patch_me(@AuthenticationPrincipal Jwt jwt, @RequestBody UserUpdate update) throws URISyntaxException, IOException, InterruptedException {
		var user = userRepository.findByJwt(jwt).get();

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
			userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		}
        
		return user;
	}
}

