package tech.orbfin.api.gateway.request;

import lombok.Getter;
import lombok.Setter;

import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class RequestChangePassword {
    private final String email;
    private final String password;
    private final String newPassword;
    private final String confirmationPassword;

    public RequestChangePassword() {
        this.email = null;
        this.password = null;
        this.newPassword = null;
        this.confirmationPassword = null;
    }
}
