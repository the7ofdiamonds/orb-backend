package tech.orbfin.api.gateway.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
@Setter
@Getter
@Component
public class ResponseLogin {
    private String successMessage;
    private String username;
    private String accessToken;
    private String refreshToken;
    private String errorMessage;

    public ResponseLogin(String username, String accessToken, String refreshToken){
        this.successMessage = "You have been successfully logged in as " + username + ".";
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}