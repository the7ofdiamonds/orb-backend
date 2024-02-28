package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.configurations.ConfigTopics;

import tech.orbfin.api.gateway.model.request.RequestChange;
import tech.orbfin.api.gateway.model.request.RequestForgot;

import tech.orbfin.api.gateway.model.response.ResponseChange;
import tech.orbfin.api.gateway.model.response.ResponseForgot;

import tech.orbfin.api.gateway.model.user.User;

import tech.orbfin.api.gateway.repositories.IRepositoryUser;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@AllArgsConstructor
@Service
public class ServiceUser {
    private final IRepositoryUser iRepositoryUser;

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @Autowired
    public final PasswordEncoder passwordEncoder;

    public User signupUser(
            String email,
            String username,
            String password,
            String firstName,
            String lastName,
            String phone
    ) throws Exception {
        try {
            boolean emailUsed = iRepositoryUser.existsByEmail(email);

            if (emailUsed) {
                kafkaTemplate.send(ConfigTopics.PASSWORD_RECOVERY, email);
                throw new Exception("This Email is already in our records. Check your email.");
            }

            boolean usernameExist = iRepositoryUser.existsByUsername(username);

            if (usernameExist) {
                kafkaTemplate.send(ConfigTopics.PASSWORD_RECOVERY, email);
                throw new Exception("This Username is already in our records. Check your email.");
            }

            Optional<User> user = iRepositoryUser.signupUser(
                    email,
                    username,
                    passwordEncoder.encode(password),
                    firstName,
                    lastName,
                    phone
            );

            return user.orElseThrow();
        } catch (Exception e){
            throw new Exception("There was an error during the login process: " + e);
        }
    }

    public User loginUser(
            String username,
            String password
    ) throws Exception {
        try {
            boolean usernameExists = iRepositoryUser.existsByUsername(username);

            if (!usernameExists) {
               throw new Exception("The username " + username + " can not be found.");
            }

            User user = findUserByUsername(username);

            if(!passwordEncoder.matches(password, user.getPassword())){
                throw new Exception("The username " + username + " and the password provided do not match.");
            }

            return user;
        } catch (Exception e){
            throw new Exception("There was an error logging in user: " + e);
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

            if(!userExistsByUsername){
                return ResponseChange.builder()
                        .error("A user could not be found with this Username. Check your inbox.")
                        .build();
            }

//// Password needs to match check for how wordpress does this
            User user = findUserByUsername(username);

            if (!passwordEncoder.matches(password, user.getPassword())) {
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

            boolean passwordChanged = iRepositoryUser.changePassword(email, username, passwordEncoder.encode(newPassword));

            if(!passwordChanged){
                return ResponseChange.builder()
                        .error("There was na error changing your password.")
                        .build();
            }

            kafkaTemplate.send(ConfigTopics.PASSWORD_CHANGED, email);

            return new ResponseChange(email);
        } catch (Exception e) {
            return ResponseChange.builder()
                    .success(null)
                    .error("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    public ResponseForgot forgotPassword(@NotNull RequestForgot request ){
        try {
            String email = request.getEmail();
            String username = request.getUsername();

            if(email == null && username == null){
                return ResponseForgot.builder()
                        .success(null)
                        .error("Either a username or email is required to restore your account.")
                        .build();
            }

            User user = null;

            if(email != null) {
                boolean userExistByEmail = iRepositoryUser.existsByEmail(email);

                if(!userExistByEmail){
                    return ResponseForgot.builder()
                            .success(null)
                            .error("This email is not in use check your inbox.")
                            .build();
                }

                user = findUserByEmail(email);
            }

            if(username != null && user == null){
                boolean userExistByUsername = iRepositoryUser.existsByUsername(username);

                if(!userExistByUsername){
                    return ResponseForgot.builder()
                            .success(null)
                            .error("This username " + username + " is not in use. Please provide your email.")
                            .build();
                }

                user = findUserByUsername(username);
            }

            email = user.getEmail();

            kafkaTemplate.send(ConfigTopics.PASSWORD_RECOVERY, email);

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
