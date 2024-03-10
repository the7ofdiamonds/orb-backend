package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;
import tech.orbfin.api.gateway.exceptions.UserCreationException;

import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.model.user.Role;
import tech.orbfin.api.gateway.model.user.User;

import tech.orbfin.api.gateway.repositories.IRepositoryUserAccount;

import tech.orbfin.api.gateway.repositories.IRepositoryUserDetails;
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
    private final IRepositoryUserDetails iRepositoryUserDetails;
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
            String confirmationCode = UUID.randomUUID().toString();

            Optional<User> user = iRepositoryUserAccount.signupUser(
                    email,
                    username,
                    passwordEncoder().encode(password),
                    firstname,
                    lastname,
                    phone,
                    Role.SUBSCRIBER,
                    providerGivenID,
                    TRUE,
                    TRUE,
                    TRUE,
                    TRUE,
                    TRUE,
                    confirmationCode
            );

            if (user.isEmpty()) {
                throw new UserCreationException();
            }

            var savedUser = user.get();

            log.info("Username {} has been signed up successfully", username);
            log.info("Creating a session for {} ....", username);

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", location);

            kafkaTemplate.send(ConfigKafkaTopics.USER_REGISTER, email);

            return new ResponseRegister(savedUser.getUsername(), savedUser.getEmail());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (UserCreationException e) {
            throw new UserCreationException();
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.USER_SIGNUP_ERROR + e.getMessage());
        }
    }

    public User verifyAccount(String username, String password, String confirmationCode) throws Exception {
        try {
            User userCredentials = serviceUserUtils.validateConfirmationCode(username, confirmationCode);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            boolean passwordIsValid = serviceUserUtils.validPassword(password);

            if (!passwordIsValid) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_NOT_VALID);
            }

            if (!passwordEncoder().matches(password, userCredentials.getPassword())) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_WRONG);
            }

            String email = userCredentials.getEmail();
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

    public ResponseUnlocked unlockAccount(String username, String password, String confirmationCode) throws Exception {
        try {
            User verifiedAccount = verifyAccount(username, password, confirmationCode);

            String email = verifiedAccount.getEmail();

            boolean accountUnlocked = serviceUserDetails.setAccountNonLocked(email, confirmationCode);

            if (!accountUnlocked) {
                throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
            }

            return new ResponseUnlocked(username, email);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR + e.getMessage());
        }
    }

    public ResponseRemoveAccount removeAccount(String username, String password, String confirmationCode) throws Exception {
        try {
            User verifiedAccount = verifyAccount(username, password, confirmationCode);

            boolean accountLocked = serviceUserDetails.setAccountLocked(verifiedAccount.getEmail(), username);

            if (!accountLocked) {
                throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
            }

            kafkaTemplate.send(ConfigKafkaTopics.ACCOUNT_REMOVED, verifiedAccount.getEmail());

            return new ResponseRemoveAccount(verifiedAccount.getEmail());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR + e.getMessage());
        }
    }
}
