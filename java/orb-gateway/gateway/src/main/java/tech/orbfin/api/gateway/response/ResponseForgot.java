package tech.orbfin.api.gateway.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseForgot {
    private String success;
    private String email;
    private String error;

    public ResponseForgot(String email){
        this.success = "Check your email at " + email + " for a link to change your password.";
//        Send Forgot Password email
    }
}
