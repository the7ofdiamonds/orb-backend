package tech.orbfin.api.gateway.entities.token;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true, columnDefinition = "TEXT")
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    @Column(unique = true, columnDefinition = "TEXT")
    private String refreshToken;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "id")
    private Integer userid;

    private boolean isAuthenticated = true;

    private boolean expired = false;

    private boolean revoked = false;

    public Token(String token, TokenType type, String refreshToken, Integer userid){
        this.token = token;
        this.type = type;
        this.refreshToken = refreshToken;
        this.userid = userid;
    }
}
