package tech.orbfin.api.gateway.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestForgotPassword {
    private String email;
}
