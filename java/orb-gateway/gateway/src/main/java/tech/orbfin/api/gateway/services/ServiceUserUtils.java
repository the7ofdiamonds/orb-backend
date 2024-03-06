package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.exceptions.UserNotFoundException;
import tech.orbfin.api.gateway.model.request.*;
import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.model.user.User;

import tech.orbfin.api.gateway.repositories.IRepositoryUser;
import tech.orbfin.api.gateway.repositories.RepositoryUser;
import tech.orbfin.api.gateway.services.firebase.ServiceUserFirebase;
import tech.orbfin.api.gateway.utils.Patterns;
import tech.orbfin.api.gateway.utils.Validator;

import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import org.springframework.context.annotation.Bean;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.firebase.auth.UserRecord;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ServiceUserUtils {
    private final IRepositoryUser iRepositoryUser;
    private final RepositoryUser repositoryUser;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ServiceUserFirebase serviceUserFirebase;
    private final ServiceUserDetails serviceUserDetails;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public boolean validEmail(String email) {
        try {
            if (email == null) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NULL);
            }

            boolean emailValid = Validator.validate(email, Patterns.EMAIL_PATTERN);

            if (!emailValid) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
            }

            return true;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

    public boolean validUsername(String username) {
        try {
            if (username == null) {
                throw new BadCredentialsException(ExceptionMessages.USERNAME_NULL);
            }

            if (username.length() > Patterns.USERNAME_MAX_LENGTH) {
                throw new BadCredentialsException(ExceptionMessages.USERNAME_TOO_LONG);
            }

            boolean usernameValid = Validator.validate(username, Patterns.USERNAME_PATTERN);

            if (!usernameValid) {
                throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_VALID);
            }

            return true;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

    public boolean validPassword(String password) {
        try {
            if (password == null) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_NULL);
            }

            if (password.length() > Patterns.PASSWORD_MAX_LENGTH) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_TOO_LONG);
            }

            boolean passwordValid = Validator.validate(password, Patterns.PASSWORD_PATTERN);

            if (!passwordValid) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_NOT_VALID);
            }

            return true;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

    public boolean validPhone(String phone) {
        try {
            if (phone == null) {
                throw new BadCredentialsException(ExceptionMessages.PHONE_NULL);
            }

            if (phone.length() > Patterns.PHONE_MAX_LENGTH) {
                throw new BadCredentialsException(ExceptionMessages.PHONE_TOO_LONG);
            }

            boolean phoneValid = Validator.validate(phone, Patterns.PHONE_PATTERN);

            if (!phoneValid) {
                throw new BadCredentialsException(ExceptionMessages.PHONE_NOT_VALID);
            }

            return true;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

    public boolean validName(String name) {
        try {
            if (name == null) {
                throw new BadCredentialsException(ExceptionMessages.NAME_NULL);
            }

            if (name.length() > Patterns.NAME_MAX_LENGTH) {
                throw new BadCredentialsException(ExceptionMessages.NAME_TOO_LONG);
            }

            boolean nameValid = Validator.validate(name, Patterns.NAME_PATTERN);

            if (!nameValid) {
                throw new BadCredentialsException(ExceptionMessages.NAME_NOT_VALID);
            }

            return true;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

    public boolean passwordsMatch(String password, String confirmPassword) throws Exception {
        try {
            boolean passwordIsValid = validPassword(password);

            if (!passwordIsValid) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_NOT_VALID);
            }

            if (confirmPassword == null) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_CONFIRM_NULL);
            }

            if (!password.equals(confirmPassword)) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORDS_DO_NOT_MATCH);
            }

            return true;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

    public User findUserByEmail(String email) throws Exception {
        try {
            boolean emailIsValid = validEmail(email);

            if (!emailIsValid) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
            }

            boolean userExistByEmail = iRepositoryUser.existsByEmail(email);

            if (!userExistByEmail) {
                return null;
            }

            log.info("Loading user by email: " + email);

            Optional<User> user = iRepositoryUser.findUserByEmail(email);

            if (user.isEmpty()) {
                throw new UserNotFoundException();
            }

            log.info("Loaded user details: " + user);

            return user.get();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException();
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.EMAIL_LOAD_ERROR + e);
        }
    }

    public User findUserByUsername(String username) throws Exception {
        try {
            boolean usernameIsValid = validEmail(username);

            if (!usernameIsValid) {
                throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_VALID);
            }

            boolean usernameExists = iRepositoryUser.existsByUsername(username);

            if (!usernameExists) {
                return null;
            }

            log.info("Loading user by username: " + username);

            Optional<User> user = iRepositoryUser.findUserByUsername(username);

            if (user.isEmpty()) {
                throw new UserNotFoundException();
            }

            log.info("Loaded user details for username {}: {}", username, user.get());

            return user.get();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException();
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.USERNAME_LOAD_ERROR + e);
        }
    }

    public User validateCredentials(String username, String password) throws Exception {
        try {
            boolean usernameIsValid = validUsername(username);

            if (!usernameIsValid) {
                throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_VALID);
            }

            boolean passwordIsValid = validPassword(password);

            if (!passwordIsValid) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_NOT_VALID);
            }

            User user = findUserByUsername(username);

            if (user == null) {
                throw new UserNotFoundException();
            }

            if (!passwordEncoder().matches(password, user.getPassword())) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_WRONG);
            }

            return user;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException();
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.CREDENTIALS_BAD + e);
        }
    }

    public User validateConfirmationCode(String username, String confirmationCode) throws Exception {
        try {
            if (confirmationCode == null) {
                throw new BadCredentialsException(ExceptionMessages.CONFIRMATION_CODE_NULL);
            }

            User user = findUserByUsername(username);

            if (user == null) {
                throw new UserNotFoundException();
            }

            String savedConfirmationCode = user.getConfirmationCode();

            if (!confirmationCode.equals(savedConfirmationCode)) {
                throw new BadCredentialsException(ExceptionMessages.CONFIRMATION_CODE_WRONG);
            }

            return user;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException();
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.CONFIRMATION_CODE_ERROR + e);
        }
    }

    public User verifyAccount(String username, String password, String confirmationCode) throws Exception {
        try {
            User userCredentials = validateConfirmationCode(username, confirmationCode);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            boolean passwordIsValid = validPassword(password);

            if (!passwordIsValid) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_NOT_VALID);
            }

            if (!passwordEncoder().matches(password, userCredentials.getPassword())) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_WRONG);
            }

            String email = userCredentials.getEmail();
            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);
            String uid = firebaseUser.getUid();

            boolean emailVerified = userCredentials.getIsEnabled();
            boolean emailVerifiedFirebase = firebaseUser.isEmailVerified();

            if (emailVerified && emailVerifiedFirebase) {
                throw new Exception(ExceptionMessages.EMAIL_VERIFIED);
            }

            boolean setEmailVerified = serviceUserDetails.setEmailVerified(email, confirmationCode);

            if (!setEmailVerified) {
                throw new Exception(ExceptionMessages.EMAIL_VERIFIED_ERROR);
            }

            boolean setEmailVerifiedFirebase = serviceUserFirebase.setEmailVerified(uid, true);

            if (!setEmailVerifiedFirebase) {
                throw new Exception(ExceptionMessages.EMAIL_VERIFIED_ERROR);
            }

            return userCredentials;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.ACCOUNT_VERIFY_ERROR + e.getMessage());
        }
    }

    public ResponseVerify verifyEmail(RequestVerify request) throws Exception {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String confirmationCode = request.getConfirmationCode();

            User verifiedAccount = verifyAccount(username, password, confirmationCode);

            if (verifiedAccount == null) {
                throw new BadCredentialsException(ExceptionMessages.ACCOUNT_VERIFY_ERROR);
            }

            boolean accountEnabled = verifiedAccount.getIsEnabled();

            if (!accountEnabled) {
                throw new BadCredentialsException(ExceptionMessages.ACCOUNT_ENABLED_ERROR);
            }

            return new ResponseVerify("email", verifiedAccount.getEmail());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.EMAIL_VERIFIED_ERROR + e.getMessage());
        }
    }
}
