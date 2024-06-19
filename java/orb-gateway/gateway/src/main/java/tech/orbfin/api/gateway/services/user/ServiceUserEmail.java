package tech.orbfin.api.gateway.services.user;

import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;
import tech.orbfin.api.gateway.model.response.ResponseAdd;
import tech.orbfin.api.gateway.model.response.ResponseRemove;
import tech.orbfin.api.gateway.model.response.ResponseVerify;
import tech.orbfin.api.gateway.model.wordpress.User;
import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUser;
import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUserChange;
import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUserEmail;
import tech.orbfin.api.gateway.services.firebase.ServiceUserFirebase;

import java.util.Arrays;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProvider;
import com.google.firebase.auth.UserRecord;

import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ServiceUserEmail {
    private final IRepositoryUser iRepositoryUser;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ServiceUserUtils serviceUserUtils;
    private final ServiceUserAccount serviceUserAccount;
    private final ServiceUserFirebase serviceUserFirebase;
    private final IRepositoryUserChange iRepositoryUserChange;
    private final IRepositoryUserEmail iRepositoryUserEmail;

    public ResponseVerify verifyEmail(String email, String confirmationCode) throws Exception {
        try {
            User userCredentials = serviceUserUtils.validateConfirmationCode(email, confirmationCode);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.ACCOUNT_VERIFY_ERROR);
            }

            boolean accountEnabled = userCredentials.getIsEnabled();

            if (!accountEnabled) {
                throw new BadCredentialsException(ExceptionMessages.ACCOUNT_ENABLED_ERROR);
            }

            return new ResponseVerify("email", userCredentials.getEmail());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.EMAIL_VERIFIED_ERROR + e.getMessage());
        }
    }

    //Needs more work
    public ResponseAdd addEmail(String username, String password, String newEmail, String token) throws Exception {
        try {
            boolean emailIsValid = serviceUserUtils.validEmail(newEmail);

            if (!emailIsValid) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
            }

            User userCredentials = serviceUserUtils.validateCredentials(username, password);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            String email = userCredentials.getEmail();

            boolean emailAdded = iRepositoryUserChange.addNewEmail(email, username, newEmail);

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

    public ResponseRemove removeEmail(String username, String password, String removeEmail) throws Exception {
        try {
            boolean emailIsValid = serviceUserUtils.validEmail(removeEmail);

            if (!emailIsValid) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
            }

            User userCredentials = serviceUserUtils.validateCredentials(username, password);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            String email = userCredentials.getEmail();

            boolean emailRemoved = iRepositoryUserEmail.removeEmail(email, username, removeEmail);

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
