package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;
import tech.orbfin.api.gateway.exceptions.UserCreationException;

import tech.orbfin.api.gateway.model.request.*;
import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.model.user.Role;
import tech.orbfin.api.gateway.model.user.User;
import tech.orbfin.api.gateway.model.user.UserEntity;

import tech.orbfin.api.gateway.repositories.IRepositoryUser;

import tech.orbfin.api.gateway.services.firebase.ServiceUserFirebase;

import java.util.*;

import static java.lang.Boolean.TRUE;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;

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
    private final IRepositoryUser iRepositoryUser;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ServiceUserFirebase serviceUserFirebase;
    private final ServiceUserDetails serviceUserDetails;
    private final ServiceUserUtils serviceUserUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Transactional
    public ResponseRegister registerAccount(@NotNull RequestRegister request) throws Exception {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String password = request.getPassword();
            String confirmPassword = request.getConfirmPassword();
            String firstname = request.getFirstname();
            String lastname = request.getLastname();
            String phone = request.getPhone();
            Object location = request.getLocation();

            boolean emailIsValid = serviceUserUtils.validEmail(email);

            if (!emailIsValid) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
            }

            boolean usernameIsValid = serviceUserUtils.validUsername(username);

            if (!usernameIsValid) {
                throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_VALID);
            }

            log.info("Registering user with the email {} .....", email);
//            Check the location

            boolean emailUsed = iRepositoryUser.existsByEmail(email);
            boolean emailExist = serviceUserFirebase.userExistByEmail(email);

            if (emailUsed || emailExist) {
                kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_RECOVERY, email);
                throw new BadCredentialsException(ExceptionMessages.EMAIL_USED);
            }

            boolean usernameExist = iRepositoryUser.existsByUsername(username);

            if (usernameExist) {
                kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_RECOVERY, email);
                throw new BadCredentialsException(ExceptionMessages.USERNAME_USED);
            }

            boolean phoneExist = serviceUserFirebase.userExistByPhone(phone);

            if (phoneExist) {
                kafkaTemplate.send(ConfigKafkaTopics.PHONE_RECOVERY, email);
                throw new BadCredentialsException(ExceptionMessages.PHONE_USED);
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

            Optional<User> user = iRepositoryUser.signupUser(
                    email,
                    username,
                    passwordEncoder().encode(password),
                    firstname,
                    lastname,
                    phone,
                    String.valueOf(Role.USER),
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

    public ResponseUnlocked unlockAccount(RequestVerify request) throws Exception {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String confirmationCode = request.getConfirmationCode();

            User verifiedAccount = serviceUserUtils.verifyAccount(username, password, confirmationCode);

            if (verifiedAccount == null) {
                throw new Exception(ExceptionMessages.ACCOUNT_VERIFY_ERROR);
            }

            String email = verifiedAccount.getEmail();

            boolean accountUnlocked = serviceUserUtils.setAccountNonLocked(email, confirmationCode);

            if (!accountUnlocked) {
                throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
            }

            boolean accountEnabled = verifiedAccount.getIsEnabled();

            if (!accountEnabled) {
                throw new Exception(ExceptionMessages.ACCOUNT_ENABLED_ERROR);
            }

            boolean accountLocked = verifiedAccount.getIsAccountNonLocked();

            if (!accountLocked) {
                throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
            }

            return new ResponseUnlocked(username, email);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR + e.getMessage());
        }
    }

    public ResponseRemoveAccount removeAccount(RequestRemoveAccount request) throws Exception {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String confirmationCode = request.getConfirmationCode();

            User verifiedAccount = serviceUserUtils.verifyAccount(username, password, confirmationCode);

            if (verifiedAccount == null) {
                throw new Exception(ExceptionMessages.ACCOUNT_VERIFY_ERROR);
            }

            String email = verifiedAccount.getEmail();

            var userEntity = new UserEntity(verifiedAccount);

            boolean accountExpired = userEntity.isAccountNonLocked();

            if (accountExpired) {
                throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
            }

            boolean accountLocked = userEntity.isAccountNonLocked();

            if (accountLocked) {
                throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
            }

            boolean accountCredentialsExpired = userEntity.isCredentialsNonExpired();

            if (accountCredentialsExpired) {
                throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
            }

            boolean accountEnabled = userEntity.isEnabled();

            if (accountEnabled) {
                throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
            }

            return new ResponseRemoveAccount(email);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR + e.getMessage());
        }
    }

    public boolean deleteAccount(RequestRemoveAccount request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();
        String confirmationCode = request.getConfirmationCode();

        User verifiedAccount = serviceUserUtils.verifyAccount(username, password, confirmationCode);

        var userEntity = new UserEntity(verifiedAccount);

        boolean accountExpired = userEntity.isAccountNonLocked();

        if (accountExpired) {
            throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
        }

        boolean accountLocked = userEntity.isAccountNonLocked();

        if (accountLocked) {
            throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
        }

        boolean accountCredentialsExpired = userEntity.isCredentialsNonExpired();

        if (accountCredentialsExpired) {
            throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
        }

        boolean accountEnabled = userEntity.isEnabled();

        if (accountEnabled) {
            throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
        }

        return iRepositoryUser.deleteAccount(verifiedAccount.getEmail(), username, confirmationCode);
    }
}
