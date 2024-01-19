package tech.orbfin.api.gateway.token;

import org.springframework.stereotype.Component;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Component
public class TokenJW extends Token {
    @Enumerated(EnumType.STRING)
    public TokenType type = TokenType.BEARER;
}
