package tech.orbfin.api.gateway.auth.providers;

import org.springframework.context.annotation.Configuration;
import tech.orbfin.api.gateway.auth.AuthJwt;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.service.ServiceToken;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Configuration
@Slf4j
@AllArgsConstructor
public class ProviderJWT implements AuthenticationProvider {

    private final ServiceToken serviceToken;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AuthJwt authJwt = (AuthJwt) authentication;
        String token = authJwt.getToken();
        String secretKey = serviceToken.getSecretKey();

        if(secretKey.equalsIgnoreCase(token)) {
            log.info("Request have valid key");
            return new AuthJwt(true, token);
        }
        throw new BadCredentialsException("Secret key in header did not match Application Secret key...");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AuthJwt.class.equals(authentication);
    }
}