package tech.orbfin.api.gateway.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tech.orbfin.api.gateway.response.ResponseAuth;
import tech.orbfin.api.gateway.user.User;

import java.util.Collection;

@Component
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestRegister {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}