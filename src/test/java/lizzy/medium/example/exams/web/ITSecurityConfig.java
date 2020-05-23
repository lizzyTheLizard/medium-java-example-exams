package lizzy.medium.example.exams.web;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.PlainJWT;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
class ITSecurityConfig extends WebSecurityConfigurerAdapter {

    static String generateToken(String username) {
        JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("given_name", "first")
                .claim("family_name", "last")
                .build();
        PlainJWT jwt = new PlainJWT(claimSet);
        return jwt.serialize();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated();
        http.oauth2ResourceServer().jwt().decoder(this::decode);
        http.cors();
    }

    private Jwt decode(String token) throws JwtException {
        try {
            JWT jwt = JWTParser.parse(token);
            Map<String, Object> headers = new LinkedHashMap<>(jwt.getHeader().toJSONObject());
            Map<String, Object> claims = jwt.getJWTClaimsSet().getClaims();
            return Jwt.withTokenValue(token)
                    .claims(c -> c.putAll(claims))
                    .headers(h -> h.putAll(headers))
                    .build();
        } catch (ParseException e) {
            throw new JwtException("Cannot decode", e);
        }
    }
}
