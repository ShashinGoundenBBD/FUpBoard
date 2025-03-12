package za.co.bbd.grad.fupboard.api;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Service
public class FUpboardAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Value("${fupboard.default-role}")
    private String defaultRole;

    @Override
    @Nullable
    @Transactional
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        var userOpt = userRepository.findByJwt(jwt);
        
        String jwtGoogleId = jwt.getSubject();
        String jwtEmail = jwt.getClaimAsString("email").toLowerCase();
        boolean jwtEmailVerified = jwt.getClaimAsBoolean("email_verified");
        
        if (userOpt.isEmpty() && jwtEmailVerified) {
            // user not found by google id, but they have a verified email in the system
            userOpt = userRepository.findByEmailIfVerified(jwtEmail);
            if (userOpt.isPresent()) {
                userOpt.get().setGoogleId(jwtGoogleId);
            }
        }

        // if user does not exist, create new user with default role
        if (userOpt.isEmpty()) {
            var newUser = new User();

            var random = new Random();
            
            newUser.setGoogleId(jwtGoogleId);
            newUser.setEmail(jwtEmail);
            newUser.setEmailVerified(jwtEmailVerified);

            // make username from first part of email + 4 numbers
            var username = jwtEmail.split("@")[0];
            username = username.substring(0, Math.min(username.length(), 48)) + random.nextInt(1000, 10000);

            // add more numbers if taken already
            for (int i = 0; i < 12; i++) {
                if (!userRepository.existsByUsername(username))
                    break;
                username += random.nextInt(10);
            }
            newUser.setUsername(username);
            newUser = userRepository.save(newUser);

            Set<Role> roles = new HashSet<Role>();
            roles.add(roleRepository.findByRoleName(defaultRole));

            newUser.setRoles(roles);
            newUser = userRepository.save(newUser);

            userOpt = Optional.of(newUser);
        }

        var user = userOpt.get();

        if (user.getEmail().equals(jwtEmail) && jwtEmailVerified && !user.getEmailVerified()) {
            user.setEmailVerified(jwtEmailVerified);
            userRepository.save(user);
        }
        
        var authorities = new ArrayList<GrantedAuthority>();

        for (Role role : user.getRoles()) {
            for (Permission permission : role.getPermissions()) {
                authorities.add(permission);
            }
        }
        
        return authorities;
    }
}
