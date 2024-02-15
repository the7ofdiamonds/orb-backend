package tech.orbfin.api.gateway.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.json.JSONArray;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tech.orbfin.api.gateway.model.user.Role;
import tech.orbfin.api.gateway.model.user.UserEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
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

    public UserEntity loginUser(String username, String password) {
        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("loginUser")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .setParameter(1, username)
                .setParameter(2, password);

        try {
            storedProcedure.execute();

            String jsonResult = (String) storedProcedure.getSingleResult();

            if (jsonResult != null) {
                JSONObject jsonObject = new JSONObject(jsonResult);

                JSONArray rolesArray = jsonObject.getJSONArray("roles");

                Collection<Role> roles = new ArrayList<>();
                for (int i = 0; i < rolesArray.length(); i++) {
                    String roleString = rolesArray.getString(i);
                    Role roleEnum = mapToRoleEnum(roleString);
                    if (roleEnum != null) {
                        roles.add(roleEnum);
                    }
                }

                boolean isAuthenticated = jsonObject.getBoolean("isAuthenticated");

                return UserEntity.builder()
                        .id(String.valueOf(jsonObject.getInt("id")))
                        .username(jsonObject.getString("username"))
                        .password(jsonObject.getString("password"))
                        .email(jsonObject.getString("email"))
                        .roles(roles)
                        .firstname(jsonObject.getString("firstname"))
                        .lastname(jsonObject.getString("lastname"))
                        .phone(jsonObject.getString("phone"))
                        .isAuthenticated(isAuthenticated)
                        .providerGivenID(jsonObject.getString("providerGivenID"))
                        .build();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to execute stored procedure. Rolling back transaction.", e);
        }
    }

    private Role mapToRoleEnum(String roleString) {
        for (Role roleEnum : Role.values()) {
            if (roleString.toLowerCase().contains(roleEnum.name().toLowerCase())) {
                return roleEnum;
            }
        }
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