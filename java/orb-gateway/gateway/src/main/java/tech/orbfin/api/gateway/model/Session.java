package tech.orbfin.api.gateway.model;

import java.io.Serializable;

import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.*;

import lombok.*;

import org.springframework.data.redis.core.RedisHash;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Builder
@Getter
@Setter
//@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@RedisHash("sessions")
@Data
@Document
public class Session implements Serializable {
    @Indexed
    private String id;
    @Id
    @Indexed
    private String token;
    @Indexed
    private SignatureAlgorithm algorithm;
    @Indexed
    private String refreshToken;
    @Indexed
    private Long userid;
    @Indexed
    private boolean isAuthenticated;
    @Indexed
    private boolean expired;
    @Indexed
    private boolean revoked;

    public Session(
            String token,
            SignatureAlgorithm algorithm,
            String refreshToken,
            Long userid,
            Boolean isAuthenticated,
            Boolean expired,
            Boolean revoked) {
        this.token = token;
        this.algorithm = algorithm;
        this.refreshToken = refreshToken;
        this.userid = userid;
        this.isAuthenticated = isAuthenticated;
        this.expired = expired;
        this.revoked = revoked;
    }

    public Session(){}
}
