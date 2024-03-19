//package tech.orbfin.api.gateway.authentication;
//
//import java.util.Collection;
//
//import lombok.*;
//
//import org.springframework.stereotype.Component;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//
//@Getter
//@Setter
//@Component
//public class AuthJWT implements Authentication {
//    private final boolean authenticated;
//    private final Object principal;
//
//    public AuthJWT() {
//        this.authenticated = true;
//        this.principal = null;
//    }
//
//    public AuthJWT(boolean authenticated, Object principal) {
//        this.authenticated = authenticated;
//        this.principal = principal;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return null;
//    }
//
//    @Override
//    public Object getCredentials() {
//        return null;
//    }
//
//    @Override
//    public Object getDetails() {
//        return null;
//    }
//
//    @Override
//    public Object getPrincipal() {
//        return this.principal;
//    }
//
//    @Override
//    public boolean isAuthenticated() {
//        return authenticated;
//    }
//
//    @Override
//    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
//        // This method is typically handled by the authentication manager or provider.
//        throw new UnsupportedOperationException("setAuthenticated should not be called directly");
//    }
//
//    @Override
//    public String getName() {
//        return null;
//    }
//}
//
