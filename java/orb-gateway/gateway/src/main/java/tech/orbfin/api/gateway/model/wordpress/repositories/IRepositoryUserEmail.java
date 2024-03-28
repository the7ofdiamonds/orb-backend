package tech.orbfin.api.gateway.model.wordpress.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.orbfin.api.gateway.model.wordpress.User;

public interface IRepositoryUserEmail extends JpaRepository<User, Long> {

    @Transactional
    @Query(nativeQuery = true, value = "CALL addNewEmail(:p_user_email, :p_display_name, :p_user_email_new)")
    public boolean addNewEmail(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username,
            @Param("p_user_email_new") String newEmail);

    @Transactional
    @Query(nativeQuery = true, value = "CALL addNewEmail(:p_user_email, :p_display_name, :p_user_email_new)")
    public boolean removeEmail(
            @Param("p_user_email") String email,
            @Param("p_display_name") String username,
            @Param("p_user_email_new") String newEmail);
}
