package tech.orbfin.api.gateway.services;

import com.google.firebase.auth.UserRecord;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;
import tech.orbfin.api.gateway.exceptions.UserNotFoundException;
import tech.orbfin.api.gateway.model.response.ResponseChange;
import tech.orbfin.api.gateway.model.response.ResponseForgot;
import tech.orbfin.api.gateway.model.response.ResponseUpdate;
import tech.orbfin.api.gateway.model.response.ResponseVerify;
import tech.orbfin.api.gateway.model.wordpress.User;
import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUser;
import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUserPassword;
import tech.orbfin.api.gateway.services.firebase.ServiceUserFirebase;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ServiceUserPassword {
    private final IRepositoryUser iRepositoryUser;
    private final IRepositoryUserPassword iRepositoryUserPassword;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ServiceUserUtils serviceUserUtils;
    private final ServiceUserFirebase serviceUserFirebase;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public ResponseForgot forgotPassword(String email, String username) throws Exception {
        try {
            User user;

            if (email == null && username == null) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_CHANGE_NULL);
            }

            if (email == null) {
                user = serviceUserUtils.findUserByUsername(username);
                email = user.getEmail();
            }

            user = serviceUserUtils.findUserByEmail(email);

            if (username != null && user == null) {
                boolean userExistByUsername = iRepositoryUser.existsByUsername(username);

                if (!userExistByUsername) {
                    throw new BadCredentialsException(ExceptionMessages.USER_NOT_FOUND);
                }

                user = serviceUserUtils.findUserByUsername(username);
            }

            if (user == null) {
                throw new UserNotFoundException();
            }

            email = user.getEmail();

            kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_RECOVERY, email);

            return new ResponseForgot(email);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.PASSWORD_CHANGE_ERROR + e.getMessage());
        }
    }

    public ResponseChange changePassword(String email, String password, String newPassword, String confirmPassword) throws Exception {
        try {
            boolean passwordsMatch = serviceUserUtils.passwordsMatch(newPassword, confirmPassword);

            if (!passwordsMatch) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORDS_DO_NOT_MATCH);
            }

            log.info("User with the email {} is attempting to change their password.", email);

            boolean passwordChanged = iRepositoryUserPassword.changePassword(email, password, passwordEncoder().encode(newPassword));

            if (!passwordChanged) {
                throw new Exception(ExceptionMessages.PASSWORD_CHANGE_ERROR);
            }

            kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_CHANGED, email);

            return ResponseChange.builder()
                    .successMessage("Your password has been changed successfully an email to confirm this was sent to " + email + ".")
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.PASSWORD_CHANGE_ERROR);
        }
    }

    public ResponseUpdate updatePassword(String username, String confirmationCode, String newPassword) throws Exception {
        try {
            boolean passwordValid = serviceUserUtils.validPassword(newPassword);

            if (!passwordValid) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_NOT_VALID);
            }

            User userCredentials = serviceUserUtils.validateConfirmationCode(username, confirmationCode);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            String email = userCredentials.getEmail();

            boolean passwordUpdated = iRepositoryUserPassword.changePassword(email, username, newPassword);

            if (!passwordUpdated) {
                throw new Exception(ExceptionMessages.PASSWORD_UPDATE_ERROR);
            }

            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);

            String uid = firebaseUser.getUid();

            boolean passwordUpdatedFirebase = serviceUserFirebase.passwordChanged(uid, newPassword);

            if (!passwordUpdatedFirebase) {
                throw new Exception(ExceptionMessages.PASSWORD_CHANGE_ERROR);
            }

            kafkaTemplate.send(ConfigKafkaTopics.USERNAME_CHANGED, email);

            return new ResponseUpdate("username", email);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.PASSWORD_UPDATE_ERROR + e.getMessage());
        }
    }
}
