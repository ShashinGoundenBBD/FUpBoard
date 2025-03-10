package za.co.bbd.grad.fupboard.api;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
        var user = userRepository.findByGoogleId(jwt.getSubject());

        var changed = false;

        var jwtEmail = jwt.getClaimAsString("email");
        var jwtEmailVerified = jwt.getClaimAsBoolean("email_verified");

        // if user does not exist, create new user with default role
        if (user == null) {
            changed = true;
            user = new User();
            
            user.setGoogleId(jwt.getSubject());
            user.setEmail(jwtEmail);
            user.setEmailVerified(jwtEmailVerified);
            
            user = userRepository.save(user);

            Set<Role> roles = new HashSet<Role>();
            roles.add(roleRepository.findByRoleName(defaultRole));

            user.setRoles(roles);
        }
        
        // update email & email verification status if changed
        if (jwtEmail != null && user.getEmail() != jwtEmail) {
            user.setEmail(jwtEmail);
        }
        if (jwtEmailVerified != null && user.getEmailVerified() != jwtEmailVerified) {
            user.setEmailVerified(jwtEmailVerified);
        }
        if (changed) userRepository.save(user);

        var authorities = new ArrayList<GrantedAuthority>();

        for (Role role : user.getRoles()) {
            for (Permission permission : role.getPermissions()) {
                authorities.add(permission);
            }
        }
        
        return authorities;
    }
}
