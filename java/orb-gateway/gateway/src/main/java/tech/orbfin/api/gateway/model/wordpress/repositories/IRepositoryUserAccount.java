package tech.orbfin.api.gateway.model.wordpress.repositories;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import tech.orbfin.api.gateway.model.wordpress.User;

import java.util.Optional;

@Repository
public interface IRepositoryUserAccount extends JpaRepository<User, Long> {

    @Query(value = "CALL addNewUser(" +
            ":p_user_email, " +
            ":p_display_name, " +
            ":p_user_pass, " +
            ":p_first_name, " +
            ":p_last_name, " +
            ":p_phone_number, " +
            ":p_roles, " +
            ":p_provider_given_id, " +
            ":p_is_authenticated, " +
            ":p_is_account_non_expired, " +
            ":p_is_account_non_locked, " +
            ":p_is_credentials_non_expired, " +
            ":p_is_enabled, " +
            ":p_confirmation_code)", nativeQuery = true)
    public Optional<User> signupUser(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username,
            @Param("p_user_pass") String password,
            @Param("p_first_name") String firstName,
            @Param("p_last_name") String lastName,
            @Param("p_phone_number") String phone,
            @Param("p_roles") String roles,
            @Param("p_provider_given_id") String providerGivenID,
            @Param("p_is_authenticated") boolean isAuthenticated,
            @Param("p_is_account_non_expired") boolean isAccountNonExpired,
            @Param("p_is_account_non_locked") boolean isAccountNonLocked,
            @Param("p_is_credentials_non_expired") boolean isCredentialsNonExpired,
            @Param("p_is_enabled") boolean isEnabled,
            @Param("p_confirmation_code") String confirmationCode);

    @Transactional
    @Query(nativeQuery = true, value = "CALL deleteAccount(:p_user_email, :p_display_name, :p_confirmation_code)")
    public boolean removeAccount(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username);

}