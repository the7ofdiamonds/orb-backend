package tech.orbfin.api.gateway.services;

import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.security.core.userdetails.UserDetails;
import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.exceptions.UserNotFoundException;

import tech.orbfin.api.gateway.model.wordpress.User;

import tech.orbfin.api.gateway.model.UserEntity;

import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUserUtils;
import tech.orbfin.api.gateway.services.firebase.ServiceUserFirebase;
import tech.orbfin.api.gateway.utils.Patterns;
import tech.orbfin.api.gateway.utils.Validator;

import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.kafka.core.KafkaTemplate;

import com.google.firebase.auth.UserRecord;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceUserUtils {
    private final IRepositoryUserUtils iRepositoryUserUtils;
    private final ServiceUserFirebase serviceUserFirebase;
    private final KafkaTemplate<String, Object> kafkaTemplate;

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

    public boolean validateEmail(String email) {
        try {
            boolean emailValid = validEmail(email);

            if (!emailValid) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
            }

            boolean emailUsed = iRepositoryUserUtils.existsByEmail(email);
            boolean emailExist = serviceUserFirebase.userExistByEmail(email);

            if (!emailUsed || !emailExist) {
                kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_RECOVERY, email);
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_FOUND);
            }

            return true;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateUsername(String username) throws Exception {
        try {
            boolean usernameValid = validUsername(username);

            if (!usernameValid) {
                throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_VALID);
            }

            boolean usernameExist = iRepositoryUserUtils.existsByUsername(username);

            if (!usernameExist) {
                throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_FOUND);
            }

            return true;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e);
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
        } catch (Exception e){
            throw new Exception(e);
        }
    }

    public boolean userExist(String email, String username, String phone) throws FirebaseAuthException {
        boolean emailUsed = iRepositoryUserUtils.existsByEmail(email);
        boolean emailExist = serviceUserFirebase.userExistByEmail(email);

        if (emailUsed || emailExist) {
            kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_RECOVERY, email);
            throw new BadCredentialsException(ExceptionMessages.EMAIL_USED);
        }

        boolean usernameExist = iRepositoryUserUtils.existsByUsername(username);

        if (usernameExist) {
            kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_RECOVERY, email);
            throw new BadCredentialsException(ExceptionMessages.USERNAME_USED);
        }

        boolean phoneExist = serviceUserFirebase.userExistByPhone(phone);

        if (phoneExist) {
            kafkaTemplate.send(ConfigKafkaTopics.PHONE_RECOVERY, email);
            throw new BadCredentialsException(ExceptionMessages.PHONE_USED);
        }

        return false;
    }

    public User findUserByEmail(String email) throws Exception {
        try {
            boolean emailIsValid = validEmail(email);

            if (!emailIsValid) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
            }

            boolean userExistByEmail = iRepositoryUserUtils.existsByEmail(email);

            if (!userExistByEmail) {
                return null;
            }

            log.info("Loading user by email: " + email);

            Optional<User> user = iRepositoryUserUtils.findUserByEmail(email);

            if (user.isEmpty()) {
                throw new UserNotFoundException();
            }

            log.info("Loaded user details: " + user);
            log.info(String.valueOf(user.get()));
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
            boolean usernameValid = validateUsername(username);

            if (!usernameValid) {
                return null;
            }

            log.info("Loading user by username: " + username);

            Optional<User> user = iRepositoryUserUtils.findUserByUsername(username);

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

    public boolean validateAccount(UserDetails user) {
        boolean enabled = user.isEnabled();

        if (!enabled) {
            log.info("Your account is not yet verified.");
            return false;
        }

        boolean accountLocked = user.isAccountNonLocked();

        if (!accountLocked) {
            log.info("Your account is locked.");
            return false;
        }

        return true;
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

            String savedPassword = user.getPassword();
            String email = user.getEmail();

            if (savedPassword.startsWith("$P")) {
                kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_UPDATE, user.getEmail());
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_UPDATE);
            }

            if (!passwordEncoder().matches(password, savedPassword)) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_WRONG);
            }

            UserRecord userRecord = serviceUserFirebase.getUserByEmail(email);

            if (userRecord == null) {
                log.info("Firebase User with the email {} does not exists.", email);
                log.info("Adding user with the email {} to firebase", email);

                serviceUserFirebase.createUser(email, username, password, null);
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

    public UserDetails loadUserByEmail(String email) {
        try {
            User user = findUserByEmail(email);

            if (user == null) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_FOUND);
            }

            return new UserEntity(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
