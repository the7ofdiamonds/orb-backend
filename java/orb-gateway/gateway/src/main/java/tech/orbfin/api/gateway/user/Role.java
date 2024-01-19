package tech.orbfin.api.gateway.user;

import static tech.orbfin.api.gateway.user.Permission.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE
            )
    ),
    MANAGER(
            Set.of(
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            )
    ),
    EDITOR(
        Set.of(
                EDITOR_READ,
                EDITOR_UPDATE,
                EDITOR_DELETE,
                EDITOR_CREATE
        )
    ),
    CONTRIBUTOR(
           Set.of(
               CONTRIBUTOR_READ,
               CONTRIBUTOR_UPDATE,
               CONTRIBUTOR_CREATE
           )
    ),
    SUBSCRIBER(
            Set.of(
                SUBSCRIBER_READ
            )
    ),
    USER(
            Set.of(USER_READ)
    );

    private final Set<Permission> permissions;

    @Bean
    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}