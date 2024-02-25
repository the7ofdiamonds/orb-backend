package tech.orbfin.api.gateway.request;

import lombok.Getter;
import lombok.Setter;

import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class RequestChange {
    private String username;
    private String password;
    private String newPassword;
    private String confirmationPassword;

    public RequestChange() {
        this.username = null;
        this.password = null;
        this.newPassword = null;
        this.confirmationPassword = null;
    }
}
