package tech.orbfin.api.gateway.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.orbfin.api.gateway.entities.user.Role;

import java.util.Optional;

public interface RepositoryRoles extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
    Role findByAuthority(String authority);
}
