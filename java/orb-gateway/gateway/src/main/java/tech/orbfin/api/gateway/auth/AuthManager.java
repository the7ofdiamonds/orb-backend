package tech.orbfin.api.gateway.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;
import tech.orbfin.api.gateway.auth.providers.ProviderJWT;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Component
@Slf4j
@AllArgsConstructor
public class AuthManager implements AuthenticationManager {
    @Autowired
    private final ProviderJWT ProviderJwt;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        ProviderJwt.authenticate(authentication);
        return null;
    }
}
