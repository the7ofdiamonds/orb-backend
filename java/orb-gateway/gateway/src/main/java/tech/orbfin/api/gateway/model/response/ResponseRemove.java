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
public class ResponseRemove {
    private String email;
    private String removeEmail;
    private String successMessage;
    private String errorMessage;

    public ResponseRemove(String email, String removeEmail){
        this.successMessage = removeEmail + " was removed from your account an email has been sent to " + email + ".";
    }
}
