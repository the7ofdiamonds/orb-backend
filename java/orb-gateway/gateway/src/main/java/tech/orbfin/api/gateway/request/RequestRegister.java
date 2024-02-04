package tech.orbfin.api.gateway.request;

import lombok.*;
import org.springframework.stereotype.Component;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class RequestRegister {
    private String username;
    private String email;
    private String password;
    private String phone;
    private String firstname;
    private String lastname;
    private Object location;
}