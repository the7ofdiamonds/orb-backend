package tech.orbfin.api.gateway.model.user;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import lombok.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Data
@Builder
@Setter
@Getter
public class UserEntity implements UserDetails {
    private String id;
    private String email;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String phone;
    private boolean isAuthenticated;
    private Collection<Role> roles;
    private String providerGivenID;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}