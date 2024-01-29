package tech.orbfin.api.gateway.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestRegister {
    private String username;
    private String email;
    private String password;
    private String phone;
    private String firstname;
    private String lastname;
    private Object location;
}