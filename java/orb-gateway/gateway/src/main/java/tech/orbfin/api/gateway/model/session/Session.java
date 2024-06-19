package tech.orbfin.api.gateway.model.session;

import java.io.Serializable;
import java.util.Collection;

import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.*;

import java.security.NoSuchAlgorithmException;

import lombok.*;

import org.springframework.data.redis.core.RedisHash;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;

import org.springframework.security.core.GrantedAuthority;

import tech.orbfin.api.gateway.services.authentication.Authenticated;
import tech.orbfin.api.gateway.utils.Hash;

@Builder
@Getter
@Setter
@AllArgsConstructor
@RedisHash("sessions")
@Data
@Document
public class Session implements Serializable {
    @Id
    @Indexed
    private String id;
    private String user_id;
    private String email;
    @Indexed
    private String username;
    @Indexed
    private Collection<GrantedAuthority> authorities;
    @Indexed
    private SignatureAlgorithm algorithm;
    @Indexed
    private String accessToken;
    @Indexed
    private String refreshToken;
    private String token;
    private String ip;
    private String user_agent;
    @Indexed
    private String login;
    @Indexed
    private long expiration;
    private boolean revoked;

    public Session(
            Authenticated authenticated,
            String ip,
            String user_agent
    ) throws NoSuchAlgorithmException {
        this.id = setId(authenticated.getAccessToken());
        this.user_id = authenticated.getId();
        this.email = authenticated.getEmail();
        this.username = authenticated.getUsername();
        this.authorities = authenticated.getAuthorities();
        this.algorithm = authenticated.getAlgorithm();
        this.accessToken = authenticated.getAccessToken();
        this.refreshToken = authenticated.getRefreshToken();
        this.username = authenticated.getUsername();
        this.authorities = authenticated.getAuthorities();
        this.login = authenticated.getLogin();
        this.expiration = authenticated.getExpiration();
        this.ip = ip;
        this.user_agent = user_agent;
    }

    public Session(Authenticated authenticated) {}

    public Session() {}

    public String setId(String refreshToken) throws NoSuchAlgorithmException {
        String token = Hash.hash(refreshToken);
        String verifier = Hash.hash(token);

        return verifier;
    }
}
