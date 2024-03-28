package tech.orbfin.api.gateway.model.wordpress.repositories;

import tech.orbfin.api.gateway.model.wordpress.User;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface IRepositoryUserChange extends JpaRepository<User, Long> {

    @Transactional
    @Query(nativeQuery = true, value = "CALL addNewEmail(:p_user_email, :p_user_pass, :p_user_email_new)")
    public boolean addNewEmail(
            @Param("p_user_email") String email,
            @Param("p_user_pass") String password,
            @Param("p_user_email_new") String newEmail);

    @Query(nativeQuery = true, value = "CALL changeUsername(:p_user_email, :p_user_pass, :p_username)")
    public boolean changeUsername(
            @Param("p_user_email") String email,
            @Param("p_user_pass") String password,
            @Param("p_username") String username);

    @Transactional
    @Query(nativeQuery = true, value = "CALL changeFirstName(:p_user_email, :p_user_pass, :p_first_name_new)")
    public boolean changeFirstName(
            @Param("p_user_email") String email,
            @Param("p_user_pass") String password,
            @Param("p_first_name_new") String newFirstName);

    @Transactional
    @Query(nativeQuery = true, value = "CALL changeLastName(:p_user_email, :p_user_pass, :p_last_name_new)")
    public boolean changeLastName(
            @Param("p_user_email") String email,
            @Param("p_user_pass") String password,
            @Param("p_last_name_new") String newLastName);

    @Query(nativeQuery = true, value = "CALL changePhoneNumber(:p_user_email, :p_user_pass, :p_new_phone_number)")
    public boolean changePhoneNumber(
            @Param("p_user_email") String email,
            @Param("p_user_pass") String password,
            @Param("p_new_phone_number") String newPhoneNumber);

    @Query(nativeQuery = true, value = "CALL changeRoles(:p_user_email, :p_user_pass, :p_wp_capabilities)")
    public boolean changeRoles(
            @Param("p_user_email") String email,
            @Param("p_user_pass") String password,
            @Param("p_wp_capabilities") String roles);


}