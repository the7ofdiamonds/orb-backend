package tech.orbfin.api.gateway.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
@Setter
@Getter
@Component
public class ResponseLogout {
    private String username;
    private String successMessage;
    private String errorMessage;

    public ResponseLogout(String username){
        this.successMessage = "You have been successfully logged out as " + username + ". You are welcome back anytime thanks for stopping by.";
    }
}
