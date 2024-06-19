package tech.orbfin.api.gateway.services.token;

import org.springframework.security.core.userdetails.UserDetails;

public interface IServiceToken {
    boolean isAccessTokenValid(String accessToken);
    String getUsernameFromAccessToken(String accessToken);
    UserDetails getValidUserFromAccessToken(String accessToken);
}
