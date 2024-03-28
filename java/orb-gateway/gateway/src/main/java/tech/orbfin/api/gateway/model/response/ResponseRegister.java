package tech.orbfin.api.gateway.model.response;

import lombok.*;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
@Setter
@Getter
@Component
public class ResponseRegister {
    private String successMessage;
    private String username;
    private String accessToken;
    private String refreshToken;
    private String errorMessage;

    public ResponseRegister(String username, String email){
        this.successMessage = "You have been successfully signed up and logged in as " + username + ". An email has also been sent to "+ email + " check your inbox.";
    }
}
