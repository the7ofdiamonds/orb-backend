package tech.orbfin.api.gateway.repositories;

import tech.orbfin.api.gateway.entities.user.UserEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryUser extends JpaRepository<UserEntity, Integer> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);
}