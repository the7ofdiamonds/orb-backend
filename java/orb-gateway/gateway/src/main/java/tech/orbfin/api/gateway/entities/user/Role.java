package tech.orbfin.api.gateway.entities.user;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import lombok.Setter;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Setter
@Getter
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String authority;

//    ADMIN(
//            Set.of(
//                    ADMIN_READ,
//                    ADMIN_UPDATE,
//                    ADMIN_DELETE,
//                    ADMIN_CREATE
//            )
//    ),
//    MANAGER(
//            Set.of(
//                    MANAGER_READ,
//                    MANAGER_UPDATE,
//                    MANAGER_DELETE,
//                    MANAGER_CREATE
//            )
//    ),
//    EDITOR(
//        Set.of(
//                EDITOR_READ,
//                EDITOR_UPDATE,
//                EDITOR_DELETE,
//                EDITOR_CREATE
//        )
//    ),
//    CONTRIBUTOR(
//           Set.of(
//               CONTRIBUTOR_READ,
//               CONTRIBUTOR_UPDATE,
//               CONTRIBUTOR_CREATE
//           )
//    ),
//    SUBSCRIBER(
//            Set.of(
//                SUBSCRIBER_READ
//            )
//    ),
//    USER(
//            Set.of(USER_READ)
//    );

//    private final Set<Permission> permissions;
//

    @Override
    public String getAuthority() {
        return this.authority;
    }
}