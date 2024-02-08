package tech.orbfin.api.gateway.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.orbfin.api.gateway.model.user.UserEntity;

@AllArgsConstructor
@Repository
@Transactional
public class RepositoryUser {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity save(UserEntity user) {
        // Assuming user has fields like username, password, and email
        String storedProcedure = "{CALL addNewUser(?, ?, ?, ?, ?, ?)}";

        try {
           entityManager.createNativeQuery(storedProcedure)
                    .setParameter(1, user.getEmail())
                    .setParameter(2, user.getPassword())
                    .setParameter(3, user.getEmail())
                    .setParameter(4, user.getFirstname())
                    .setParameter(5, user.getLastname())
                    .setParameter(6, user.getPhone())
                    .executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save user. Rolling back transaction.", e);
        }

        return user;
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