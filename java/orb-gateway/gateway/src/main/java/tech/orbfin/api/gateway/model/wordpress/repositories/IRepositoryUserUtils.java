package tech.orbfin.api.gateway.model.wordpress.repositories;

import tech.orbfin.api.gateway.model.wordpress.User;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface IRepositoryUserUtils extends JpaRepository<User, Long> {
    @Transactional
    @Query(value = "CALL existsByEmail(:p_user_email)", nativeQuery = true)
    public boolean existsByEmail(
            @Param("p_user_email") String email);

    @Transactional
    @Query(value = "CALL existsByUsername(:p_display_name)", nativeQuery = true)
    public boolean existsByUsername(
            @Param("p_display_name") String username);

    @Transactional
    @Query(value = "CALL findUserByEmail(:p_email)", nativeQuery = true)
    public Optional<User> findUserByEmail(
            @Param("p_email") String email);

    @Transactional
    @Query(value = "CALL findUserByUsername(:p_username)", nativeQuery = true)
    public Optional<User> findUserByUsername(
            @Param("p_username") String username);
}