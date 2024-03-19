//package tech.orbfin.api.gateway.authentication;
//
//import tech.orbfin.api.gateway.authentication.providers.ProviderJWT;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import reactor.core.publisher.Mono;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
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
