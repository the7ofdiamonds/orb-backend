package tech.orbfin.api.gateway.model.wordpress.repositories;

import tech.orbfin.api.gateway.model.wordpress.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IRepositoryUserRoles extends JpaRepository<Role, Long> {

    @Query(value = "SELECT * FROM wp_user_roles_view", nativeQuery = true)
    String getWPUserRoles();
}
