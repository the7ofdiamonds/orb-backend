//package tech.orbfin.api.gateway.authorization.providers;
//
//import jakarta.security.enterprise.credential.RememberMeCredential;
//import org.springframework.context.annotation.Configuration;
//import tech.orbfin.api.gateway.authorization.AuthJwt;
//import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
//import tech.orbfin.api.gateway.services.ServiceToken;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//
//@Configuration
//@Slf4j
//@AllArgsConstructor
//public class ProviderJWT implements AuthenticationProvider {
//
//    private final ServiceToken serviceToken;
//
//    private final AuthJwt authJwt;
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//
//        String token = authJwt.getToken();
//
//
//        String secretKey = serviceToken.getSecretKey();
//
//        if(secretKey.equalsIgnoreCase(token)) {
//            log.info("Request have valid key");
//            return new AuthJwt(true, token);
//        }
//        throw new BadCredentialsException("Secret key in header did not match Application Secret key...");
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return AuthJwt.class.equals(authentication);
//    }
//}