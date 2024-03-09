package tech.orbfin.api.gateway.model.user;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@RequiredArgsConstructor
public class UserEntity implements UserDetails {
    private final User user;

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        if (user.getRoles() == null) {
            return Collections.emptyList();
        }

        Capabilities capabilities = new Capabilities();

        return user.getRoles().stream()
                .flatMap(role -> capabilities.getCapabilitiesForRole(role).keySet().stream())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        Boolean nonExpired = user.getIsAccountNonExpired();

        if (nonExpired != null && nonExpired == Boolean.FALSE) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        Boolean accountNonLocked = user.getIsAccountNonLocked();

        if (accountNonLocked != null && accountNonLocked == Boolean.FALSE) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        Boolean credentialsNonExpired = user.getIsCredentialsNonExpired();

        if (credentialsNonExpired != null && credentialsNonExpired == Boolean.FALSE) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isEnabled() {
        Boolean enabled = user.getIsEnabled();

        if (enabled != null && enabled == Boolean.FALSE) {
            return false;
        }

        return true;
    }
}