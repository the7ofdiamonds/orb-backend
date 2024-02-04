package tech.orbfin.api.gateway.request;

import lombok.Getter;
import lombok.Setter;

import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class RequestChangePassword {
    private String email;
    private String password;
    private String newPassword;
    private String confirmationPassword;

    public RequestChangePassword() {
        this.email = null;
        this.password = null;
        this.newPassword = null;
        this.confirmationPassword = null;
    }
}
