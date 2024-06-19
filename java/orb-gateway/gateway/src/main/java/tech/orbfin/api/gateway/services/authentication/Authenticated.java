package tech.orbfin.api.gateway.services.authentication;

import com.redis.om.spring.annotations.Indexed;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class Authenticated {
    private String id;
    private String email;
    private String username;
    private Collection<GrantedAuthority> authorities;
    private SignatureAlgorithm algorithm;
    private String accessToken;
    private String refreshToken;
    private String auth_time;
    private String login;
    private long expiration;

    public Authenticated(
            String id,
            String email,
            String username,
            String accessToken,
            String refreshToken
    ){
        this.id = id;
        this.email = email;
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
