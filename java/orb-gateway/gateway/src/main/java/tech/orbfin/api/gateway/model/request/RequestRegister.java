package tech.orbfin.api.gateway.model.request;

import lombok.*;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
@Setter
@Component
public class RequestRegister {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String phone;
    private String firstname;
    private String lastname;
    private String nicename;
    private Object location;
}