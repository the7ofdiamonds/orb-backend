package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.model.request.*;
import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.model.user.User;

import tech.orbfin.api.gateway.repositories.IRepositoryUser;
import tech.orbfin.api.gateway.repositories.RepositoryUser;

import tech.orbfin.api.gateway.services.firebase.ServiceUserFirebase;

import java.util.Arrays;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;

import org.springframework.context.annotation.Bean;

import org.springframework.stereotype.Service;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProvider;
import com.google.firebase.auth.UserRecord;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ServiceUserChange {
    private final IRepositoryUser iRepositoryUser;
    private final RepositoryUser repositoryUser;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ServiceUserFirebase serviceUserFirebase;
    private final ServiceUserUtils serviceUserUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public ResponseAdd addEmail(RequestAddEmail request) throws Exception {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String newEmail = request.getNewEmail();
            String token = request.getToken();

            boolean emailIsValid = serviceUserUtils.validEmail(newEmail);

            if (!emailIsValid) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
            }

            User userCredentials = serviceUserUtils.validateCredentials(username, password);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            String email = userCredentials.getEmail();

            boolean emailAdded = iRepositoryUser.addNewEmail(email, username, newEmail);

            if (!emailAdded) {
                throw new Exception(ExceptionMessages.EMAIL_ADD_ERROR);
            }
// Get provider id based on the new way to login
            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);

            String uid = firebaseUser.getUid();
            var providerId = firebaseUser.getProviderId();
            var provider = UserProvider.builder()
                    .setUid(uid)
                    .setEmail(newEmail)
                    .setProviderId(providerId)
                    .build();

            serviceUserFirebase.addNewEmail(uid, provider);

            kafkaTemplate.send(ConfigKafkaTopics.EMAIL_ADDED, email);

            return new ResponseAdd("email", email);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.EMAIL_ADD_ERROR + e.getMessage());
        }
    }

    public ResponseChange changeUsername(RequestChangeUsername request) throws Exception {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String newUsername = request.getNewUsername();

            boolean usernameIsValid = serviceUserUtils.validUsername(newUsername);

            if (!usernameIsValid) {
                throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_VALID);
            }

            User userCredentials = serviceUserUtils.validateCredentials(username, password);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            String email = userCredentials.getEmail();

            boolean usernameChanged = iRepositoryUser.changeUsername(email, username, newUsername);

            if (!usernameChanged) {
                throw new Exception(ExceptionMessages.USERNAME_CHANGE_ERROR);
            }

            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);

            String uid = firebaseUser.getUid();

            serviceUserFirebase.changeUsername(uid, newUsername);

            kafkaTemplate.send(ConfigKafkaTopics.USERNAME_CHANGED, email);

            return new ResponseChange("username", email);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.USERNAME_CHANGE_ERROR + e.getMessage());
        }
    }

    public ResponseChange changePassword(@NotNull RequestChangePassword request) throws Exception {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String newPassword = request.getNewPassword();
            String confirmPassword = request.getConfirmationPassword();

            boolean passwordsMatch = serviceUserUtils.passwordsMatch(newPassword, confirmPassword);

            if (!passwordsMatch) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORDS_DO_NOT_MATCH);
            }

            User userCredentials = serviceUserUtils.validateCredentials(username, password);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            String email = userCredentials.getEmail();

            log.info("User with the email {} is attempting to change their password.", email);

            boolean passwordChanged = iRepositoryUser.changePassword(email, username, passwordEncoder().encode(newPassword));

            if (!passwordChanged) {
                throw new Exception(ExceptionMessages.PASSWORD_CHANGE_ERROR);
            }

            kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_CHANGED, email);

            return new ResponseChange("password", email);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.PASSWORD_CHANGE_ERROR);
        }
    }

    public ResponseUpdate updatePassword(RequestUpdatePassword request) throws Exception {
        try {
            String username = request.getUsername();
            String confirmationCode = request.getConfirmationCode();
            String newPassword = request.getNewPassword();

            boolean passwordValid = serviceUserUtils.validPassword(newPassword);

            if (!passwordValid) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_NOT_VALID);
            }

            User userCredentials = serviceUserUtils.validateConfirmationCode(username, confirmationCode);

            if (userCredentials != null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            String email = userCredentials.getEmail();

            boolean passwordUpdated = iRepositoryUser.changePassword(email, username, newPassword);

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

    public ResponseChange changeName(RequestChangeName request) throws Exception {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String newFirstName = request.getNewFirstName();
            String newLastName = request.getNewLastName();

            if (newFirstName == null && newLastName == null) {
                throw new BadCredentialsException(ExceptionMessages.NAME_NULL);
            }

            User userCredentials = serviceUserUtils.validateCredentials(username, password);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            boolean validFirstName = serviceUserUtils.validName(newFirstName);

            if (!validFirstName) {
                throw new BadCredentialsException(ExceptionMessages.NAME_NOT_VALID);
            }

            boolean validLastName = serviceUserUtils.validName(newLastName);

            if (!validLastName) {
                throw new BadCredentialsException(ExceptionMessages.NAME_NOT_VALID);
            }

            String email = userCredentials.getEmail();

            if (newFirstName != null) {
                boolean firstNameChanged = iRepositoryUser.changeFirstName(email, username, newFirstName);

                if (!firstNameChanged) {
                    throw new Exception(ExceptionMessages.NAME_CHANGE_ERROR);
                }
            }

            if (newLastName != null) {
                boolean lastNameChanged = iRepositoryUser.changeLastName(email, username, newLastName);

                if (!lastNameChanged) {
                    throw new Exception(ExceptionMessages.NAME_CHANGE_ERROR);
                }
            }

            kafkaTemplate.send(ConfigKafkaTopics.NAME_CHANGED, email);

            return new ResponseChange("name", email);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.NAME_CHANGE_ERROR);
        }
    }

    public ResponseChange changePhone(RequestChangePhone request) throws Exception {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String newPhone = request.getNewPhone();

            boolean phoneIsValid = serviceUserUtils.validPhone(newPhone);

            if (!phoneIsValid) {
                throw new BadCredentialsException(ExceptionMessages.PHONE_ERROR);
            }

            User userCredentials = serviceUserUtils.validateCredentials(username, password);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            String email = userCredentials.getEmail();

            boolean phoneChanged = iRepositoryUser.changePhoneNumber(email, username, newPhone);

            if (!phoneChanged) {
                throw new Exception(ExceptionMessages.PHONE_ERROR);
            }

            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);
            String uid = firebaseUser.getUid();

            serviceUserFirebase.changePhone(uid, newPhone);

            kafkaTemplate.send(ConfigKafkaTopics.PHONE_CHANGED, email);


            return new ResponseChange("phone", email);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.PHONE_ERROR + e.getMessage());
        }
    }

    public ResponseRemove removeEmail(RequestRemoveEmail request) throws Exception {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String removeEmail = request.getRemoveEmail();

            boolean emailIsValid = serviceUserUtils.validEmail(removeEmail);

            if (!emailIsValid) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
            }

            User userCredentials = serviceUserUtils.validateCredentials(username, password);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            String email = userCredentials.getEmail();

            boolean emailRemoved = repositoryUser.removeEmail(email, username, removeEmail);

            if (!emailRemoved) {
                throw new Exception(ExceptionMessages.EMAIL_REMOVE_ERROR);
            }

            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);

            String uid = firebaseUser.getUid();
            Iterable<String> providerIds = Arrays.asList(firebaseUser.getProviderData()).stream().map(UserInfo::getProviderId).collect(Collectors.toList());
            serviceUserFirebase.removeEmail(uid, providerIds);

            kafkaTemplate.send(ConfigKafkaTopics.EMAIL_REMOVED, email);

            return new ResponseRemove(removeEmail, email);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.EMAIL_REMOVE_ERROR + e.getMessage());
        }
    }
}
