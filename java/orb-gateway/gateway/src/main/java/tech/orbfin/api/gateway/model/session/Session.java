package tech.orbfin.api.gateway.model.session;

import java.io.Serializable;
import java.util.Collection;

import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.*;

import lombok.*;

import org.springframework.data.redis.core.RedisHash;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;

import org.springframework.security.core.GrantedAuthority;

@Builder
@Getter
@Setter
@AllArgsConstructor
@RedisHash("sessions")
@Data
@Document
public class Session implements Serializable {
    @Indexed
    private String id;
    @Id
    @Indexed
    private String accessToken;
    @Indexed
    private String refreshToken;
    @Indexed
    private SignatureAlgorithm algorithm;
    @Indexed
    private String username;
    @Indexed
    private Collection<GrantedAuthority> authorities;
    @Indexed
    private long issued;
    @Indexed
    private long expiration;
    @Indexed
    private boolean revoked;

    public Session(
            SignatureAlgorithm algorithm,
            String accessToken,
            String refreshToken,
            String username,
            Collection<GrantedAuthority> authorities,
            long issued,
            long expiration,
            Boolean revoked) {
        this.algorithm = algorithm;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.authorities = authorities;
        this.issued = issued;
        this.expiration = expiration;
        this.revoked = revoked;
    }

    public Session() {}
}
