package tech.orbfin.api.gateway.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import tech.orbfin.api.gateway.entities.token.Token;

@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseLogin {
    private String success;
    private String username;
    @JsonProperty("access_token")
    private Token<String> accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String error;

    public ResponseLogin(String username, Token<String> accessToken, String refreshToken){
        this.success = "You have been successfully logged in as " + username + ".";
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}