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
public class ResponseUpdate {
    private String item;
    private String email;
    private String successMessage;
    private String errorMessage;
    private Integer statusCode;

    public ResponseUpdate(String item, String email) {
        this.successMessage = "Your " + item + " has been updated an email to confirm this was sent to " + email + ".";
    }
}
