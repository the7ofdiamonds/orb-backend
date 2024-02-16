package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.repositories.RepositoryUser;
import tech.orbfin.api.gateway.model.user.UserEntity;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceUser {
    private final RepositoryUser userRepository;

    public UserEntity findUserByEmail(String email) throws Exception {
        try{
            log.info("Loading user by email: " + email);

            UserEntity user = userRepository.findUserByEmail(email);

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

    public UserEntity findUserByUsername(String username) throws Exception {
        try{
            log.info("Loading user by username: " + username);

            UserEntity user = userRepository.findUserByUsername(username);

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