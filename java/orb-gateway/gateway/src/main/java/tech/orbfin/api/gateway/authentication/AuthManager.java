//package tech.orbfin.api.gateway.authorization;
//
//import tech.orbfin.api.gateway.authorization.providers.ProviderJWT;
//
//import reactor.core.publisher.Mono;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//@AllArgsConstructor
//public class AuthManager implements ReactiveAuthenticationManager {
//    private final ProviderJWT providerJwt;
//
//    @Override
//    public Mono<Authentication> authenticate(Authentication authentication) throws AuthenticationException {
//        try {
//            Authentication result = providerJwt.authenticate(authentication);
//            return Mono.just(result);
//        } catch (AuthenticationException ex) {
//            log.error("Authentication failed: {}", ex.getMessage());
//            return Mono.error(ex);
//        }
//    }
//}
