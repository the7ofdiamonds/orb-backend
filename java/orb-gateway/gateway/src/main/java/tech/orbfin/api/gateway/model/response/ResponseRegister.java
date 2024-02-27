package tech.orbfin.api.gateway.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRegister {
    private String success;
    private String username;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String error;

    public ResponseRegister(String username, String email){
        this.success = "You have been successfully signed up and logged in as " + username + ". An email has also been sent to "+ email + " check your inbox.";
//      Send Register Email
    }
}
