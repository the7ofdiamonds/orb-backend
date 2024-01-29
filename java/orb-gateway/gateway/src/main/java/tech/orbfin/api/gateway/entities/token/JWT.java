package tech.orbfin.api.gateway.entities.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JWT extends Token {
    private String header;
    private String payload;
    private String Signature;

//    private final Oauth oauth;
}
