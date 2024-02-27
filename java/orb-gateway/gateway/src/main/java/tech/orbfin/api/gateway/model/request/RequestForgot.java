package tech.orbfin.api.gateway.model.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class RequestForgot {
    private String email;
    private String username;
}
