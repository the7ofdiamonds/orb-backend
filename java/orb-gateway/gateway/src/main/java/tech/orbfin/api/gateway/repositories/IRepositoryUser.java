package tech.orbfin.api.gateway.repositories;

import tech.orbfin.api.gateway.model.user.UserEntity;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface IRepositoryUser extends JpaRepository<UserEntity, Long> {

    @Query(value = "CALL existsByEmail(:p_user_email)", nativeQuery = true)
    public boolean existsByEmail(
            @Param("p_user_email") String email);

    @Query(value = "CALL existsByUsername(:p_display_name)", nativeQuery = true)
    public boolean existsByUsername(
            @Param("p_display_name") String username);

    @Query(value = "CALL signupUser(:p_user_email, :p_display_name, :p_user_pass, :p_first_name, :p_last_name, :p_phone_number)", nativeQuery = true)
    public UserEntity signupUser(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username,
            @Param("p_user_pass") String password,
            @Param("p_first_name") String firstName,
            @Param("p_last_name") String lastName,
            @Param("p_phone_number") String phone
    );

    @Query(value = "CALL loginUser(:p_display_name, :p_user_pass)", nativeQuery = true)
    UserEntity loginUser(
            @Param("p_display_name") String username,
            @Param("p_user_pass") String password);

    @Query(nativeQuery = true, value = "CALL usernamePasswordMatches(:p_display_name, :p_user_pass)")
    Boolean usernamePasswordMatches(
            @Param("p_display_name") String username,
            @Param("p_user_pass") String password);

    @Query(nativeQuery = true, value = "CALL findUserByEmail(:p_user_email)")
    public UserEntity findUserByEmail(
            @Param("p_user_email") String email);

    @Query(nativeQuery = true, value = "CALL findUserByUsername(:p_display_name)")
    public UserEntity findUserByUsername(
            @Param("p_display_name") String email);

    @Query(nativeQuery = true, value = "CALL changePassword(:p_user_email, :p_display_name, :p_user_pass, :p_user_pass_new)")
    public boolean changePassword(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username,
            @Param("p_user_pass") String password,
            @Param("p_user_pass_new") String newPassword);

//
//    public void update();
//
//    public void delete();
}