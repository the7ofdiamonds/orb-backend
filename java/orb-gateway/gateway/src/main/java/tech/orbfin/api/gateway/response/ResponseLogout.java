package tech.orbfin.api.gateway.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseLogout {
    private Map<String,String> headers;
    private String username;
    private String success;
    private String error;

    public ResponseLogout(String username){
        this.success = "You have been successfully logged out as " + username + ". You are welcome back anytime thanks for stopping by.";
    }

    private Map<String, String> setHeaders() {
        return Map.of(HttpHeaders.LOCATION, "/login");
    }
}
