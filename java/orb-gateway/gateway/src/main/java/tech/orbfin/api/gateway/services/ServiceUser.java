package tech.orbfin.api.gateway.services;

import com.google.firebase.auth.UserRecord;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;

import tech.orbfin.api.gateway.model.request.RequestChange;
import tech.orbfin.api.gateway.model.request.RequestForgot;

import tech.orbfin.api.gateway.model.request.RequestRegister;
import tech.orbfin.api.gateway.model.response.ResponseChange;
import tech.orbfin.api.gateway.model.response.ResponseForgot;

import tech.orbfin.api.gateway.model.response.ResponseRegister;
import tech.orbfin.api.gateway.model.user.User;

import tech.orbfin.api.gateway.repositories.IRepositoryUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;

import org.springframework.stereotype.Service;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@AllArgsConstructor
@Service
public class ServiceUser {
    private final IRepositoryUser iRepositoryUser;
    private KafkaTemplate<String, Object> kafkaTemplate;
    private final ServiceUserFirebase serviceUserFirebase;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Transactional
    public ResponseRegister register(@NotNull RequestRegister request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String password = request.getPassword();
            String confirmPassword = request.getConfirmPassword();
            String firstname = request.getFirstname();
            String lastname = request.getLastname();
            String phone = request.getPhone();
            Object location = request.getLocation();

            log.info("Registering user with the email {} .....", email);
//            Check the location

            boolean emailUsed = iRepositoryUser.existsByEmail(email);

            if (emailUsed) {
                kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_RECOVERY, email);
                return ResponseRegister.builder()
                        .error("This Email is already in our records. Check your email.")
                        .build();
            }

            boolean usernameExist = iRepositoryUser.existsByUsername(username);

            if (usernameExist) {
                kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_RECOVERY, email);
                return ResponseRegister.builder()
                        .error("This Username is already in our records. Check your email inbox.")
                        .build();
            }

            if (!password.equals(confirmPassword)) {
                return ResponseRegister.builder()
                        .error("Passwords do not match.")
                        .build();
            }

            Optional<User> user = iRepositoryUser.signupUser(
                    email,
                    username,
                    passwordEncoder().encode(password),
                    firstname,
                    lastname,
                    phone
            );

            if(user.isEmpty()){
                return ResponseRegister.builder()
                        .error("There was an error signing up please try again at another time.")
                        .build();
            };
            var savedUser = user.get();

            UserRecord firebaseUser = serviceUserFirebase.createUser(savedUser.getEmail(), savedUser.getUsername(), savedUser.getPassword(), savedUser.getPhone());

            log.info("Username {} has been signed up successfully", username);
            log.info("Creating a session for {} ....", username);

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", location);

            kafkaTemplate.send(ConfigKafkaTopics.USER_REGISTER, email);

            return new ResponseRegister(firebaseUser.getDisplayName(), firebaseUser.getEmail());
        } catch (Exception e) {
            System.err.println("Error while signing up user : " + e.getMessage());

            return ResponseRegister.builder()
                    .error(e.getMessage())
                    .build();
        }
    }

    public User findUserByEmail(String email) throws Exception {
        try {
            log.info("Loading user by email: " + email);

            Optional<User> user = iRepositoryUser.findUserByEmail(email);

            log.info("Loaded user details: " + user);

            return user.orElseThrow();
        } catch (Exception e) {
            System.err.println("Error while loading user by username: " + e.getMessage());
            throw new Exception("Error while loading user by username", e);
        }
    }

    public User findUserByUsername(String username) throws Exception {
        try {
            log.info("Loading user by username: " + username);

            Optional<User> user = iRepositoryUser.findUserByUsername(username);

            log.info("Loaded user details for username {}: {}", username, user);

            return user.orElseThrow();
        } catch (Exception e) {
            System.err.println("Error while loading user by username: " + e.getMessage());
            throw new Exception("Error while loading user by username", e);
        }
    }

    public ResponseChange changePassword(@NotNull RequestChange request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String newPassword = request.getNewPassword();
            String confirmationPassword = request.getConfirmationPassword();

            boolean userExistsByUsername = iRepositoryUser.existsByUsername(username);

            if (!userExistsByUsername) {
                return ResponseChange.builder()
                        .error("A user could not be found with this Username. Check your inbox.")
                        .build();
            }

//// Password needs to match check for how wordpress does this
            User user = findUserByUsername(username);

            if (!passwordEncoder().matches(password, user.getPassword())) {
                return ResponseChange.builder()
                        .error("Wrong password. If you have forgot your password click the FORGOT button.")
                        .build();
            }

            if (!newPassword.equals(confirmationPassword)) {
                return ResponseChange.builder()
                        .error("You need to enter the new password twice, ensuring they match exactly.")
                        .build();
            }

            String email = user.getEmail();

            log.info("User with the email {} is attempting to change their password.", email);

            boolean passwordChanged = iRepositoryUser.changePassword(email, username, passwordEncoder().encode(newPassword));

            if (!passwordChanged) {
                return ResponseChange.builder()
                        .error("There was na error changing your password.")
                        .build();
            }

            kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_CHANGED, email);

            return new ResponseChange(email);
        } catch (Exception e) {
            return ResponseChange.builder()
                    .success(null)
                    .error("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    public ResponseForgot forgotPassword(@NotNull RequestForgot request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();

            if (email == null && username == null) {
                return ResponseForgot.builder()
                        .success(null)
                        .error("Either a username or email is required to restore your account.")
                        .build();
            }

            User user = null;

            if (email != null) {
                boolean userExistByEmail = iRepositoryUser.existsByEmail(email);

                if (!userExistByEmail) {
                    return ResponseForgot.builder()
                            .success(null)
                            .error("This email is not in use check your inbox.")
                            .build();
                }

                user = findUserByEmail(email);
            }

            if (username != null && user == null) {
                boolean userExistByUsername = iRepositoryUser.existsByUsername(username);

                if (!userExistByUsername) {
                    return ResponseForgot.builder()
                            .success(null)
                            .error("This username " + username + " is not in use. Please provide your email.")
                            .build();
                }

                user = findUserByUsername(username);
            }

            email = user.getEmail();

            kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_RECOVERY, email);

            return ResponseForgot.builder()
                    .success("Check your email at " + email + " for further instructions")
                    .build();
        } catch (Exception e) {
            return ResponseForgot.builder()
                    .success(null)
                    .error("Internal server error: " + e)
                    .build();
        }
    }
}
