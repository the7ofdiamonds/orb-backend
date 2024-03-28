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


    public ResponseChange changeUsername(String email, String password, String username) throws Exception {
        try {
            boolean usernameChanged = iRepositoryUserChange.changeUsername(email, password, username);

            if (!usernameChanged) {
                throw new Exception(ExceptionMessages.USERNAME_CHANGE_ERROR);
            }

            kafkaTemplate.send(ConfigKafkaTopics.USERNAME_CHANGED, email);

            return ResponseChange.builder()
                    .statusCode(200)
                    .successMessage("Your username has been changed to " + username + " an email to confirm this was sent to " + email + ".")
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.USERNAME_CHANGE_ERROR + e.getMessage());
        }
    }

    public ResponseChange changeName(String email, String password, String newFirstName, String newLastName) throws Exception {
        try {

            boolean validFirstName = serviceUserUtils.validName(newFirstName);

            if (!validFirstName) {
                throw new BadCredentialsException(ExceptionMessages.NAME_NOT_VALID);
            }

            boolean validLastName = serviceUserUtils.validName(newLastName);

            if (!validLastName) {
                throw new BadCredentialsException(ExceptionMessages.NAME_NOT_VALID);
            }

            if (newFirstName != null) {
                boolean firstNameChanged = iRepositoryUserChange.changeFirstName(email, password, newFirstName);

                if (!firstNameChanged) {
                    throw new Exception(ExceptionMessages.NAME_CHANGE_ERROR);
                }
            }

            if (newLastName != null) {
                boolean lastNameChanged = iRepositoryUserChange.changeLastName(email, password, newLastName);

                if (!lastNameChanged) {
                    throw new Exception(ExceptionMessages.NAME_CHANGE_ERROR);
                }
            }

            kafkaTemplate.send(ConfigKafkaTopics.NAME_CHANGED, email);

            return ResponseChange.builder()
                    .successMessage("Your name has been changed to " + newFirstName + " " + newLastName + " an email to confirm this was sent to " + email + ".")
                    .statusCode(200)
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.NAME_CHANGE_ERROR);
        }
    }

    public ResponseChange changePhone(String email, String password, String newPhone) throws Exception {
        try {
            boolean phoneIsValid = serviceUserUtils.validPhone(newPhone);

            if (!phoneIsValid) {
                throw new BadCredentialsException(ExceptionMessages.PHONE_ERROR);
            }

            boolean phoneChanged = iRepositoryUserChange.changePhoneNumber(email, password, newPhone);

            if (!phoneChanged) {
                throw new Exception(ExceptionMessages.PHONE_ERROR);
            }

            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);
            String uid = firebaseUser.getUid();

            serviceUserFirebase.changePhone(uid, newPhone);

            kafkaTemplate.send(ConfigKafkaTopics.PHONE_CHANGED, email);

            return ResponseChange.builder()
                    .successMessage("Your phone number has been changed to " + newPhone + " an email to confirm this was sent to " + email + ".")
                    .statusCode(200)
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.PHONE_ERROR + e.getMessage());
        }
    }
}
