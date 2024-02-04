package tech.orbfin.api.gateway.model;

import java.io.Serializable;

import jakarta.persistence.*;

import lombok.*;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@RedisHash("sessions")
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session<T, U> implements Serializable {
    private T id;
    @Id
    @Indexed
    private T token;
    private String type;
    private U refreshToken;
    private String userid;
    private boolean isAuthenticated;
    private boolean expired;
    private boolean revoked;


    public Session(T token, String type, U refreshToken, String userid) {
        this.id = token;
        this.token = token;
        this.type = type;
        this.refreshToken = refreshToken;
        this.userid = userid;
    }

    @JsonCreator
    public Session(
            @JsonProperty("id") T id,
            @JsonProperty("token") T token,
            @JsonProperty("type") String type,
            @JsonProperty("refreshToken") U refreshToken,
            @JsonProperty("userid") String userid,
            @JsonProperty("isAuthenticated") Boolean isAuthenticated,
            @JsonProperty("expired") Boolean expired,
            @JsonProperty("revoked") Boolean revoked) {
        this.id = id;
        this.token = token;
        this.type = type;
        this.refreshToken = refreshToken;
        this.userid = userid;
        this.isAuthenticated = isAuthenticated != null ? isAuthenticated : false;
        this.expired = expired != null ? expired : false;
        this.revoked = revoked != null ? revoked : false;
    }
}
