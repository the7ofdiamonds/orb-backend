package tech.orbfin.api.gateway.authentication.providers;

import tech.orbfin.api.gateway.authentication.AuthJWT;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;

import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Configuration
public class ProviderJWT implements AuthenticationProvider {

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