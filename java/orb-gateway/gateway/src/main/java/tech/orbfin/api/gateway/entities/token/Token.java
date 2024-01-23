package tech.orbfin.api.gateway.entities.token;

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
public class Token {
    @Id
    private Integer id;

    @Column(unique = true, columnDefinition = "TEXT")
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    @Column(unique = true, columnDefinition = "TEXT")
    private String refreshToken;

    private Integer userid;

    @Builder.Default
    private boolean isAuthenticated = true;

    @Builder.Default
    private boolean expired = false;

    @Builder.Default
    private boolean revoked = false;

    public Token(String token, TokenType type, String refreshToken, Integer userid){
        this.token = token;
        this.type = type;
        this.refreshToken = refreshToken;
        this.userid = userid;
    }
}
