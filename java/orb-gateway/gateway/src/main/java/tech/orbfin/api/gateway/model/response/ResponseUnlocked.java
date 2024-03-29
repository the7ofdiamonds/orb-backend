package tech.orbfin.api.gateway.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ResponseUnlocked {
    private String successMessage;
    private String username;
    private String email;
    private String errorMessage;
    private Integer statusCode;

    public ResponseUnlocked(String username, String email) {
        this.successMessage = "Your account with the username " + username + " has been unlocked. Check your email at " + email + " for a link to change your password.";
        this.statusCode = 200;
    }
}
