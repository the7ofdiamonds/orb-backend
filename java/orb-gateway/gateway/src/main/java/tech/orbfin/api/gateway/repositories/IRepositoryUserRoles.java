package tech.orbfin.api.gateway.repositories;

import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

@Repository
public interface IRepositoryUserRoles {

    @Query(value = "SELECT * FROM wp_user_roles_view", nativeQuery = true)
    String getWPUserRoles();
}
