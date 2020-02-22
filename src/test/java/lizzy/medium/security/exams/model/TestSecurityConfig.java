package lizzy.medium.security.exams.model;

import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.HashMap;
import java.util.Map;

@EnableWebSecurity
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TestSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated();

        http.oauth2ResourceServer().bearerTokenResolver(httpServletRequest -> "token").jwt();
        http.cors();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("given_name", "First");
        claims.put("family_name", "Last");
        claims.put("sub", "testuser");
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "none");
        return s -> new Jwt("token", null, null, headers, claims);
    }
}
