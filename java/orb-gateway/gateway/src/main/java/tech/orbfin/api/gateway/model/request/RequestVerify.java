package tech.orbfin.api.gateway.model.request;

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
public class RequestVerify {
    private String username;
    private String password;
    private String email;
    private String confirmationCode;
}
