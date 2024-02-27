package tech.orbfin.api.gateway.model.request;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import tech.orbfin.api.gateway.exceptions.LogoutException;

@Getter
@Setter
public class RequestLogout {
    private String token;

    public RequestLogout(String token) {
        this.token = token;
    }

    public static RequestLogout fromHttpServletRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new LogoutException("Invalid Authorization header");
        }

        String jwt = authHeader.substring(7);

        return new RequestLogout(jwt);
    }
}
