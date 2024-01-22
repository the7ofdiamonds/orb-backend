package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.repositories.RepositoryUser;
import tech.orbfin.api.gateway.entities.user.UserEntity;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ServiceUser implements UserDetailsService {
    private final RepositoryUser userRepository;

    public UserEntity loadUserByEmail(String email) throws Exception {
        try{
            System.out.println("Loading user by email: " + email);

            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                       new Exception("User not found with email: " + email);
                        return null;
                    });

            // Log user details
            System.out.println("Loaded user details: " + user);

            return user;

        } catch (Exception e) {
            // Log the exception
            System.err.println("Error while loading user by username: " + e.getMessage());

            // Rethrow the exception
            throw new Exception("Error while loading user by username", e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // Log the username being queried
            System.out.println("Loading user by username: " + username);

            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        // Log the error
                        System.err.println("User not found with username: " + username);
                        return new UsernameNotFoundException("User not found with username: " + username);
                    });

            // Log user details
            System.out.println("Loaded user details: " + user);

            return User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .roles(String.valueOf(Arrays.asList(user.getRoles())))  // Assuming roles is a comma-separated string
                    .build();
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error while loading user by username: " + e.getMessage());

            // Rethrow the exception
            throw new UsernameNotFoundException("Error while loading user by username", e);
        }
    }
}