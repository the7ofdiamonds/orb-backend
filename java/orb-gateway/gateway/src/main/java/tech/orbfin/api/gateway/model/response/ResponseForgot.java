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
public class ResponseForgot {
    private String successMessage;
    private String email;
    private String errorMessage;
    private Integer statusCode;

    public ResponseForgot(String email){
        this.successMessage = "Check your email at " + email + " for a link to change your password.";
    }
}
