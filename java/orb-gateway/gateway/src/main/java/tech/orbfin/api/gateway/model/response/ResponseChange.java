package tech.orbfin.api.gateway.model.response;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
@Setter
@Getter
@Component
public class ResponseChange {
    private String successMessage;
    private String email;
    private String errorMessage;

    public ResponseChange(String email){
        this.successMessage = "Your password has been changed an email to confirm this was sent to " + email + ".";
    }
}
