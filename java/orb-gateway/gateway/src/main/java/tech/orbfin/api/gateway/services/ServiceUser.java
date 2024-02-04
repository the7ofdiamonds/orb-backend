package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.repositories.RepositoryUser;
import tech.orbfin.api.gateway.model.user.UserEntity;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceUser {
    private final RepositoryUser userRepository;

    public void loadUserByEmail(String email) throws Exception {
        try{
            System.out.println("Loading user by email: " + email);

            Object user = userRepository.findUserByEmail(email);

            System.out.println("Loaded user details: " + user);

//            return UserEntity.builder()
//                    .username(user.getUsername())
//                    .email(user.getEmail())
//                    .firstname(user.getFirstname())
//                    .lastname(user.getLastname())
//                    .role(user.getRole())
//                    .build();
        } catch (Exception e) {
            System.err.println("Error while loading user by username: " + e.getMessage());
            throw new Exception("Error while loading user by username", e);
        }
    }

//    @Override
//    public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
//        try {
//            System.out.println("Loading user by username: " + username);
//
//            UserEntity user = userRepository.findByUsername(username)
//                    .orElseThrow(() -> {
//                        System.err.println("User not found with username: " + username);
//                        return new UsernameNotFoundException("User not found with username: " + username);
//                    });
//
//            System.out.println("Loaded user details: " + user);
//
//            return UserEntity.builder()
//                    .username(user.getUsername())
//                    .email(user.getEmail())
//                    .firstname(user.getFirstname())
//                    .lastname(user.getLastname())
//                    .role(user.getRole())
//                    .build();
//        } catch (Exception e) {
//            System.err.println("Error while loading user by username: " + e.getMessage());
//            throw new UsernameNotFoundException("Error while loading user by username", e);
//        }
//    }
//
//    public Optional<UserEntity> findUserByUserLogin(String userLogin) {
//        return userRepository.findByUserLogin(userLogin);
//    }
}