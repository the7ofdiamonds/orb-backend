package tech.orbfin.api.gateway.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import tech.orbfin.api.gateway.model.user.UserEntity;

@AllArgsConstructor
@Repository
public class RepositoryUser {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity save(UserEntity user){
        return null;
    }

    public boolean existsByEmail(String email){
        return false;
    }

    public boolean existsByUsername(String username){
        return false;
    }

    public UserEntity findUserByEmail(String email){
        return null;
    }

    public UserEntity findUserByUsername(String username) {
        String query = "SELECT u.user_login, u.user_email, m.meta_key, m.meta_value " +
                "FROM wp_users u " +
                "JOIN wp_usermeta m ON u.ID = m.user_id " +
                "WHERE u.user_login = :username";

        try {
            return (UserEntity) entityManager
                    .createNativeQuery(query, "UserDetailsWithMetaMapping")
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void update(){}

    public void delete(){}
}