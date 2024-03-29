package tech.orbfin.api.gateway.model.wordpress.repositories;

import tech.orbfin.api.gateway.model.wordpress.User;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface IRepositoryUserDetails extends JpaRepository<User, Long> {

    @Transactional
    @Query(nativeQuery = true, value = "CALL enableAccount(:p_user_email, :p_user_pass, :p_confirmation_code)")
    public boolean enableAccount(
            @Param("p_user_email") String email,
            @Param("p_user_pass") String password,
            @Param("p_confirmation_code") String confirmationCode);

    @Transactional
    @Query(nativeQuery = true, value = "CALL disableAccount(:p_user_email, :p_user_pass)")
    public boolean disableAccount(
            @Param("p_user_email") String email,
            @Param("p_user_pass") String password);

    @Transactional
    @Query(nativeQuery = true, value = "CALL unexpireAccount(:p_user_email, :p_display_name, :p_confirmation_code)")
    public boolean unexpireAccount(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username,
            @Param("p_confirmation_code") String confirmationCode);

    @Transactional
    @Query(nativeQuery = true, value = "CALL expireAccount(:p_user_email, :p_display_name)")
    public boolean expireAccount(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username);

    @Transactional
    @Query(nativeQuery = true, value = "CALL unexpireCredentials(:p_user_email, :p_display_name)")
    public boolean unexpireCredentials(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username);

    @Transactional
    @Query(nativeQuery = true, value = "CALL expireCredentials(:p_user_email, :p_display_name)")
    public boolean expireCredentials(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username);

    @Transactional
    @Query(nativeQuery = true, value = "CALL unlockAccount(:p_user_email, :p_display_name, :p_confirmation_code)")
    public boolean unlockAccount(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username,
            @Param("p_confirmation_code") String confirmationCode);

    @Transactional
    @Query(nativeQuery = true, value = "CALL lockAccount(:p_user_email, :p_user_pass)")
    public boolean lockAccount(
            @Param("p_user_email") String email,
            @Param("p_user_pass") String password);

}