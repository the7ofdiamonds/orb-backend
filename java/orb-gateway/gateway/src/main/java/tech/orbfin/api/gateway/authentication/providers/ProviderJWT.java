package tech.orbfin.api.gateway.authentication.providers;

import tech.orbfin.api.gateway.authentication.AuthJWT;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.services.ServiceToken;

import org.springframework.context.annotation.Configuration;

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
    private final AuthJWT authJwt;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            return new AuthJWT(true, authentication.getPrincipal());
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Secret key in header did not match Application Secret key...");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AuthJWT.class.equals(authentication);
    }
}