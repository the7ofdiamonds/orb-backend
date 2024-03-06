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
public class ResponseDeleteAccount {
    private String email;
    private String successMessage;
    private String errorMessage;

    public ResponseDeleteAccount(String email) {
        this.successMessage = "Your account with the email " + email + " was removed.";
    }
}
