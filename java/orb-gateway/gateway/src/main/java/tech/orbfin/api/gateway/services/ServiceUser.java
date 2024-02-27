package tech.orbfin.api.gateway.services;

import lombok.AllArgsConstructor;
import tech.orbfin.api.gateway.repositories.IRepositoryUser;
import tech.orbfin.api.gateway.model.user.UserEntity;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ServiceUser {
    private final IRepositoryUser iRepositoryUser;

    public boolean userExistsByEmail(String email) throws Exception {
        try {
            log.info("Checking if user exists by email: " + email);

            boolean userExists = iRepositoryUser.existsByEmail(email);

            return userExists;
        } catch (Exception e) {
            System.err.println("Error while searching for user by email: " + e.getMessage());
            throw new Exception("Error while searching for user by email", e);
        }
    }

    public UserEntity findUserByEmail(String email) throws Exception {
        try{
            log.info("Loading user by email: " + email);

            UserEntity user = iRepositoryUser.findUserByEmail(email);

            log.info("Loaded user details: " + user);

            return UserEntity.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .roles(user.getRoles())
                    .build();
        } catch (Exception e) {
            System.err.println("Error while loading user by username: " + e.getMessage());
            throw new Exception("Error while loading user by username", e);
        }
    }

    public boolean userExistsByUsername(String username) throws Exception {
        try {
            log.info("Checking if user exists by username: " + username);

            boolean userExists = iRepositoryUser.existsByUsername(username);

            return userExists;
        } catch (Exception e) {
            System.err.println("Error while searching for a user by username: " + e.getMessage());
            throw new Exception("Error while searching for a user by username", e);
        }
    }

    public UserEntity findUserByUsername(String username) throws Exception {
        try{
            log.info("Loading user by username: " + username);

            UserEntity user = iRepositoryUser.findUserByUsername(username);

            log.info("Loaded user details for username {}: {}", username, user);

            return UserEntity.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .roles(user.getRoles())
                    .build();
        } catch (Exception e) {
            System.err.println("Error while loading user by username: " + e.getMessage());
            throw new Exception("Error while loading user by username", e);
        }
    }
}