//package tech.orbfin.api.gateway.authorization;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//import tech.orbfin.api.gateway.authorization.providers.ProviderJWT;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//
//@Component
//@Slf4j
//@AllArgsConstructor
//public class AuthManager implements ReactiveAuthenticationManager {
//    private final ProviderJWT ProviderJwt;
//
//    @Override
//    public Mono<Authentication> authenticate(Authentication authentication) throws AuthenticationException {
//        ProviderJwt.authenticate(authentication);
//        return null;
//    }
//}
