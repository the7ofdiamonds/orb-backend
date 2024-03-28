package tech.orbfin.api.gateway.model.wordpress.repositories;

import tech.orbfin.api.gateway.model.wordpress.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IRepositoryUserPassword extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = "CALL changePassword(:p_user_email, :p_user_pass, :p_user_pass_new)")
    public boolean changePassword(
            @Param("p_user_email") String email,
            @Param("p_user_pass") String password,
            @Param("p_user_pass_new") String newPassword);
}
