package za.co.bbd.grad.fupboard.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    public static final String[] PUBLIC_ROUTES = {
        "/",
        "/error",
        "/v1/jwt",
    };

    @Autowired
    FUpboardAuthorityConverter authorityConverter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf((csrf) -> csrf.disable())
            .authorizeHttpRequests((authz) -> 
                authz.requestMatchers(PUBLIC_ROUTES).permitAll()
                .requestMatchers("/v1/jwt/claims").hasAuthority("jwt::read::mine")
                .requestMatchers(HttpMethod.GET, "/v1/users/me").hasAuthority("user::read::me")
                .requestMatchers(HttpMethod.PATCH, "/v1/users/me").hasAuthority("user::write::me")
                .anyRequest().authenticated()
            )
            .sessionManagement((sm) -> sm.disable())
            .httpBasic(Customizer.withDefaults())
            .oauth2ResourceServer((oauth2) -> 
                oauth2.jwt((jwt) -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        var authConverter = new JwtAuthenticationConverter();
        authConverter.setJwtGrantedAuthoritiesConverter(authorityConverter);
        return authConverter;
    }
}
