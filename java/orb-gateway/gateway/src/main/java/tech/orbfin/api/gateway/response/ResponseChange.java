package tech.orbfin.api.gateway.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseChange {
    private String success;
    private String email;
    private String error;

    public ResponseChange(String email){
        this.success = "Your password has been changed an email to confirm this was sent to " + email + ".";
//        Send Password Changed email
    }
}
