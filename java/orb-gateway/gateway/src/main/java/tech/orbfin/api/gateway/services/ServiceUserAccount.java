package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;
import tech.orbfin.api.gateway.exceptions.UserCreationException;

import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.model.wordpress.User;

import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUserAccount;

import tech.orbfin.api.gateway.services.firebase.ServiceUserFirebase;

import java.util.*;

import static java.lang.Boolean.TRUE;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;

import org.springframework.stereotype.Service;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.firebase.auth.UserRecord;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ServiceUserAccount {
    private final IRepositoryUserAccount iRepositoryUserAccount;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ServiceUserFirebase serviceUserFirebase;
    private final ServiceUserDetails serviceUserDetails;
    private final ServiceUserUtils serviceUserUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Transactional
    public ResponseRegister registerAccount(String email,
                                            String username,
                                            String password,
                                            String confirmPassword,
                                            String firstname,
                                            String lastname,
                                            String phone,
                                            Object location) throws Exception {
        try {
            boolean emailIsValid = serviceUserUtils.validEmail(email);

            if (!emailIsValid) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
            }

            boolean usernameIsValid = serviceUserUtils.validUsername(username);

            if (!usernameIsValid) {
                throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_VALID);
            }

            boolean userExist = serviceUserUtils.userExist(email, username, phone);

            if (userExist) {
                throw new BadCredentialsException(ExceptionMessages.USER_EXISTS);
            }

            boolean passwordsMatch = serviceUserUtils.passwordsMatch(password, confirmPassword);

            if (!passwordsMatch) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORDS_DO_NOT_MATCH);
            }

            if (firstname != null) {
                boolean validFirstName = serviceUserUtils.validName(firstname);

                if (!validFirstName) {
                    throw new BadCredentialsException(ExceptionMessages.NAME_NOT_VALID);
                }
            }

            if (lastname != null) {
                boolean validLastName = serviceUserUtils.validName(lastname);

                if (!validLastName) {
                    throw new BadCredentialsException(ExceptionMessages.NAME_NOT_VALID);
                }
            }

            UserRecord firebaseUser = serviceUserFirebase.createUser(email, username, password, phone);

            String providerGivenID = (firebaseUser.getUid() != null) ? firebaseUser.getUid() : null;

            if (providerGivenID == null) {
                throw new UserCreationException(ExceptionMessages.USER_CREATION_ERROR_FIREBASE);
            }

            String confirmationCode = UUID.randomUUID().toString();

            Optional<User> user = iRepositoryUserAccount.signupUser(
                    email,
                    username,
                    passwordEncoder().encode(password),
                    firstname,
                    lastname,
                    phone,
                    "subscriber",
                    providerGivenID,
                    TRUE,
                    TRUE,
                    TRUE,
                    TRUE,
                    TRUE,
                    confirmationCode
            );

            if (user.isEmpty()) {
                throw new UserCreationException(ExceptionMessages.USER_CREATION_ERROR);
            }

            var savedUser = user.get();

            kafkaTemplate.send(ConfigKafkaTopics.USER_REGISTER, email);

            return ResponseRegister.builder()
                    .successMessage("You have been successfully signed up as " + savedUser.getUsername() + ". An email has also been sent to " + savedUser.getEmail() + " check your inbox.")
                    .statusCode(201)
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (UserCreationException e) {
            throw new UserCreationException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.USER_SIGNUP_ERROR);
        }
    }

    public User verifyAccount(String email, String password, String confirmationCode) throws Exception {
        try {
            User userCredentials = serviceUserUtils.validateConfirmationCode(email, confirmationCode);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CONFIRMATION_CODE_ERROR);
            }

            boolean passwordIsValid = serviceUserUtils.validPassword(password);

            if (!passwordIsValid) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_NOT_VALID);
            }

            if (!passwordEncoder().matches(password, userCredentials.getPassword())) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_WRONG);
            }

            boolean emailVerified = userCredentials.getIsEnabled();

            if (!emailVerified) {
                boolean setEmailVerified = serviceUserDetails.enableAccount(email, confirmationCode);

                if (!setEmailVerified) {
                    throw new Exception(ExceptionMessages.EMAIL_VERIFIED_ERROR);
                }
            }

            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);
            String uid = firebaseUser.getUid();
            boolean emailVerifiedFirebase = firebaseUser.isEmailVerified();

            if (!emailVerifiedFirebase) {
                boolean setEmailVerifiedFirebase = serviceUserFirebase.setEmailVerified(uid, true);

                if (!setEmailVerifiedFirebase) {
                    throw new Exception(ExceptionMessages.EMAIL_VERIFIED_ERROR);
                }
            }

            return userCredentials;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.ACCOUNT_VERIFY_ERROR + e.getMessage());
        }
    }

    public ResponseUnlocked unlockAccount(String email, String password, String confirmationCode) throws Exception {
        try {
            User verifiedAccount = verifyAccount(email, password, confirmationCode);

            boolean accountUnlocked = serviceUserDetails.setAccountNonLocked(email, confirmationCode);

            if (!accountUnlocked) {
                throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
            }

            return new ResponseUnlocked(verifiedAccount.getUsername(), verifiedAccount.getEmail());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR + e.getMessage());
        }
    }

    public ResponseRemoveAccount removeAccount(String email, String password, String confirmationCode) throws Exception {
        try {
            User verifiedAccount = verifyAccount(email, password, confirmationCode);

            boolean accountLocked = serviceUserDetails.setAccountLocked(email, verifiedAccount.getPassword());

            if (!accountLocked) {
                throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
            }

            kafkaTemplate.send(ConfigKafkaTopics.ACCOUNT_REMOVED, verifiedAccount.getEmail());

            return new ResponseRemoveAccount(verifiedAccount.getEmail());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
        }
    }
}
