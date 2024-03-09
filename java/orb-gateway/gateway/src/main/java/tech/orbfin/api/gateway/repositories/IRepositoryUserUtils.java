package tech.orbfin.api.gateway.repositories;

import tech.orbfin.api.gateway.model.user.User;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface IRepositoryUserUtils extends JpaRepository<User, Long> {

    @Query(value = "CALL existsByEmail(:p_user_email)", nativeQuery = true)
    public boolean existsByEmail(
            @Param("p_user_email") String email);

    @Query(value = "CALL existsByUsername(:p_display_name)", nativeQuery = true)
    public boolean existsByUsername(
            @Param("p_display_name") String username);

    @Transactional
    @Procedure(name = "findUserByEmail")
    public Optional<User> findUserByEmail(
            @Param("p_email") String email);

    @Transactional
    @Procedure(name = "findUserByUsername")
    public Optional<User> findUserByUsername(
            @Param("p_username") String username);
}