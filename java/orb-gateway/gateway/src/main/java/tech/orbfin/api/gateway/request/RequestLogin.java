package tech.orbfin.api.gateway.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class RequestLogin {
    private String username;
    private String password;
}
