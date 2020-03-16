package lizzy.medium.example.exams.web.exams;

import lizzy.medium.example.exams.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
class UserController {
    User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "You need to be logged in");
        }
        if (!(authentication instanceof JwtAuthenticationToken)) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Wrong authentication");
        }
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        Jwt jwt = jwtAuthenticationToken.getToken();
        return User.builder()
                .id(jwt.getSubject())
                .firstName(jwt.getClaimAsString("given_name"))
                .lastName(jwt.getClaimAsString("family_name"))
                .build();
    }
}
