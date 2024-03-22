package tech.orbfin.api.gateway.model.wordpress.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.orbfin.api.gateway.model.wordpress.User;

public interface IRepositoryUserPassword extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = "CALL changePassword(:p_user_email, :p_display_name, :p_user_pass_new)")
    public boolean changePassword(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username,
            @Param("p_user_pass_new") String newPassword);
}
