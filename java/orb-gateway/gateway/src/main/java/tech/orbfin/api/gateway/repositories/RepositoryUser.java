package tech.orbfin.api.gateway.repositories;

import java.util.ArrayList;
import java.util.Collection;

import tech.orbfin.api.gateway.model.user.Role;
import tech.orbfin.api.gateway.model.user.UserEntity;

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

@Slf4j
@AllArgsConstructor
@Repository
@Transactional
public class RepositoryUser {

    @PersistenceContext
    private EntityManager entityManager;

    public boolean existsByEmail(String email){
        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("existsByEmail")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .setParameter(1, email);

        // Execute the stored procedure
        storedProcedure.execute();

        // Retrieve the result from the stored procedure
        Object result = storedProcedure.getSingleResult();

        // Cast the result to the appropriate type (assuming it's a count)
        long userCount = ((Number) result).longValue();

        // Check if the user exists based on the count
        return userCount > 0;
    }

    public boolean existsByUsername(String username){
        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("existsByUsername")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .setParameter(1, username);

        storedProcedure.execute();

        Object result = storedProcedure.getSingleResult();

        long userCount = ((Number) result).longValue();

        return userCount > 0;
    }

    public UserEntity signupUser(UserEntity user) {
        try {
            String storedProcedure = "{CALL addNewUser(?, ?, ?, ?, ?, ?)}";

            entityManager.createNativeQuery(storedProcedure)
                .setParameter(1, user.getEmail())
                .setParameter(2, user.getUsername())
                .setParameter(3, user.getPassword())
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

    public boolean usernamePasswordMatches(String username, String password) {
        try {
            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("usernamePasswordMatches")
                    .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                    .setParameter(1, username)
                    .setParameter(2, password);

            storedProcedure.execute();

            Object result = storedProcedure.getSingleResult();

            if (result instanceof Number) {
                long userCount = ((Number) result).longValue();
                return userCount > 0;
            } else {
                // Handle the case when the result is not a Number (e.g., it might be a String)
                // You might want to log an error, throw an exception, or handle it based on your requirements.
                return false;
            }
        } catch (Exception e) {
            // Handle any exceptions that might occur during the execution of the stored procedure
            // You might want to log an error, throw an exception, or handle it based on your requirements.
            return false;
        }
    }

    public UserEntity loginUser(String username, String password) {
        try {
            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("loginUser")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .setParameter(1, username)
                .setParameter(2, password);

            storedProcedure.execute();

            String jsonResult = (String) storedProcedure.getSingleResult();

            log.info("JSON Result: {}", jsonResult);

            if (jsonResult == null) {
                return new UserEntity();
            }

            JSONObject jsonObject = new JSONObject(jsonResult);

            JSONArray rolesArray = jsonObject.getJSONArray("roles");

            Collection<Role> roles = new ArrayList<>();
            for (int i = 0; i < rolesArray.length(); i++) {
                String roleString = rolesArray.getString(i);
                Role roleEnum = Role.mapToRoleEnum(roleString);
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to execute stored procedure. Rolling back transaction.", e);
        }
    }

    public UserEntity findUserByEmail(String email){
        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("findUserByEmail")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .setParameter(1, email);

        try {
            storedProcedure.execute();

            String jsonResult = (String) storedProcedure.getSingleResult();

            if (jsonResult != null) {
                JSONObject jsonObject = new JSONObject(jsonResult);

                JSONArray rolesArray = jsonObject.getJSONArray("roles");

                Collection<Role> roles = new ArrayList<>();
                for (int i = 0; i < rolesArray.length(); i++) {
                    String roleString = rolesArray.getString(i);
                    Role roleEnum = Role.mapToRoleEnum(roleString);
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

    public UserEntity findUserByUsername(String username) {
        StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("findUserByUsername")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .setParameter(1, username);

        try {
            storedProcedure.execute();

            String jsonResult = (String) storedProcedure.getSingleResult();

            if (jsonResult != null) {
                JSONObject jsonObject = new JSONObject(jsonResult);

                JSONArray rolesArray = jsonObject.getJSONArray("roles");

                Collection<Role> roles = new ArrayList<>();
                for (int i = 0; i < rolesArray.length(); i++) {
                    String roleString = rolesArray.getString(i);
                    Role roleEnum = Role.mapToRoleEnum(roleString);
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

    public void update(){}

    public void delete(){}
}