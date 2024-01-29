package tech.orbfin.api.gateway.entities;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("Token")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session<T,U> {
    @Id
    private String id;
    private T token;
    private String type;
    private U refreshToken;
    private String userid;
    private boolean isAuthenticated;
    private boolean expired;
    private boolean revoked;

    public Session(T token, String type, U refreshToken, String userid){
        this.token = token;
        this.type = type;
        this.refreshToken = refreshToken;
        this.userid = userid;
    }
}
