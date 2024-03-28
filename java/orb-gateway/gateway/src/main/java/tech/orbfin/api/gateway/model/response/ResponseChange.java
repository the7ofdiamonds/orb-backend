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
    private String item;
    private String email;
    private String successMessage;
    private String errorMessage;
    private Integer statusCode;

    public ResponseChange(String item, String email){
        this.successMessage = "Your " + item + " has been changed an email to confirm this was sent to " + email + ".";
    }
}
