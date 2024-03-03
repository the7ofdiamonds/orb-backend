package tech.orbfin.api.gateway.repositories;

import tech.orbfin.api.gateway.model.user.Role;
import tech.orbfin.api.gateway.model.user.User;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface IRepositoryUser extends JpaRepository<User, Long> {

    @Query(value = "CALL existsByEmail(:p_user_email)", nativeQuery = true)
    public boolean existsByEmail(
            @Param("p_user_email") String email);

    @Query(value = "CALL existsByUsername(:p_display_name)", nativeQuery = true)
    public boolean existsByUsername(
            @Param("p_display_name") String username);

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
            ":p_is_enabled)", nativeQuery = true)
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
            @Param("p_is_enabled") boolean isEnabled
    );

    @Query(value = "CALL loginUser(:p_display_name, :p_user_pass)", nativeQuery = true)
    public Optional<User> loginUser(
            @Param("p_display_name") String username,
            @Param("p_user_pass") String password);

    @Query(nativeQuery = true, value = "CALL usernamePasswordMatches(:p_display_name, :p_user_pass)")
    Boolean usernamePasswordMatches(
            @Param("p_display_name") String username,
            @Param("p_user_pass") String password);

    @Query(nativeQuery = true, value = "CALL findUserByEmail(:p_user_email)")
    public Optional<User> findUserByEmail(
            @Param("p_user_email") String email);

    @Query(nativeQuery = true, value = "CALL findUserByUsername(:p_display_name)")
    public Optional<User> findUserByUsername(
            @Param("p_display_name") String p_display_name);

    @Query(nativeQuery = true, value = "CALL changeUsername(:p_user_email, :p_display_name, :p_user_pass, :p_new_display_name)")
    public boolean changeUsername(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username,
            @Param("p_user_pass") String password,
            @Param("p_new_display_name") String newUsername);

    @Query(nativeQuery = true, value = "CALL changePassword(:p_user_email, :p_display_name, :p_user_pass_new)")
    public boolean changePassword(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username,
            @Param("p_user_pass_new") String newPassword);

    @Query(nativeQuery = true, value = "CALL changeFirstName(:p_user_email, :p_display_name, :p_user_pass, :p_new_first_name)")
    public boolean changeFirstName(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username,
            @Param("p_user_pass") String password,
            @Param("p_new_first_name") String newFirstName);

    @Query(nativeQuery = true, value = "CALL changeLastName(:p_user_email, :p_display_name, :p_user_pass, :p_new_last_name)")
    public boolean changeLastName(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username,
            @Param("p_user_pass") String password,
            @Param("p_new_last_name") String newLastName);

    @Query(nativeQuery = true, value = "CALL changePhoneNumber(:p_user_email, :p_display_name, :p_user_pass, :p_new_phone_number)")
    public boolean changePhoneNumber(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username,
            @Param("p_user_pass") String password,
            @Param("p_new_phone_number") String newPhoneNumber);

    @Query(nativeQuery = true, value = "CALL changeRoles(:p_user_email, :p_display_name, :p_user_pass, :p_wp_capabilities)")
    public boolean changeRoles(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username,
            @Param("p_user_pass") String password,
            @Param("p_wp_capabilities") Collection<Role> roles);

//
//    public void update();
//
//    public void delete();
}