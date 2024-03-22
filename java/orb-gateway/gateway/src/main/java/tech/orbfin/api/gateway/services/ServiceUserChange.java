package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.model.wordpress.User;

import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUserChange;

import tech.orbfin.api.gateway.services.firebase.ServiceUserFirebase;

import java.util.Arrays;

import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

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
    private final IRepositoryUserChange iRepositoryUserChange;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ServiceUserFirebase serviceUserFirebase;
    private final ServiceUserUtils serviceUserUtils;


    public ResponseChange changeUsername(String username, String password, String newUsername) throws Exception {
        try {
            boolean usernameIsValid = serviceUserUtils.validUsername(newUsername);

            if (!usernameIsValid) {
                throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_VALID);
            }

            User userCredentials = serviceUserUtils.validateCredentials(username, password);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            String email = userCredentials.getEmail();

            boolean usernameChanged = iRepositoryUserChange.changeUsername(email, username, newUsername);

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

    public ResponseChange changeName(String username, String password, String newFirstName, String newLastName) throws Exception {
        try {
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
                boolean firstNameChanged = iRepositoryUserChange.changeFirstName(email, username, newFirstName);

                if (!firstNameChanged) {
                    throw new Exception(ExceptionMessages.NAME_CHANGE_ERROR);
                }
            }

            if (newLastName != null) {
                boolean lastNameChanged = iRepositoryUserChange.changeLastName(email, username, newLastName);

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

    public ResponseChange changePhone(String username, String password, String newPhone) throws Exception {
        try {
            boolean phoneIsValid = serviceUserUtils.validPhone(newPhone);

            if (!phoneIsValid) {
                throw new BadCredentialsException(ExceptionMessages.PHONE_ERROR);
            }

            User userCredentials = serviceUserUtils.validateCredentials(username, password);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            String email = userCredentials.getEmail();

            boolean phoneChanged = iRepositoryUserChange.changePhoneNumber(email, username, newPhone);

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
}
