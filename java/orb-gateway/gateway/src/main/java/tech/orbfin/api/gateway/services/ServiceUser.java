package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;

import tech.orbfin.api.gateway.exceptions.*;

import tech.orbfin.api.gateway.model.request.*;
import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.model.user.Role;
import tech.orbfin.api.gateway.model.user.User;

import tech.orbfin.api.gateway.repositories.IRepositoryUser;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;

import org.springframework.stereotype.Service;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserProvider;
import com.google.firebase.auth.UserInfo;
import tech.orbfin.api.gateway.repositories.RepositoryUser;
import tech.orbfin.api.gateway.services.firebase.ServiceUserFirebase;
import tech.orbfin.api.gateway.utils.Patterns;
import tech.orbfin.api.gateway.utils.Validator;

import static java.lang.Boolean.TRUE;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ServiceUser {
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

    public User findUserByEmail(String email) throws Exception {
        try {
            boolean emailIsValid = validEmail(email);

            if (!emailIsValid) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
            }

            boolean userExistByEmail = iRepositoryUser.existsByEmail(email);

            if (!userExistByEmail) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_FOUND);
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

    @Transactional
    public ResponseRegister register(@NotNull RequestRegister request) throws Exception {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String password = request.getPassword();
            String confirmPassword = request.getConfirmPassword();
            String firstname = request.getFirstname();
            String lastname = request.getLastname();
            String phone = request.getPhone();
            Object location = request.getLocation();

            boolean emailIsValid = validEmail(email);

            if (!emailIsValid) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
            }

            boolean usernameIsValid = validUsername(username);

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

            boolean passwordsMatch = passwordsMatch(password, confirmPassword);

            if (!passwordsMatch) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORDS_DO_NOT_MATCH);
            }

            if (firstname != null) {
                boolean validFirstName = validName(firstname);

                if (!validFirstName) {
                    throw new BadCredentialsException(ExceptionMessages.NAME_NOT_VALID);
                }
            }

            if (lastname != null) {
                boolean validLastName = validName(lastname);

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

    public boolean verifyAccount(String username, String password, String confirmationCode) throws Exception {
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

            return true;
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

            boolean accountVerified = verifyAccount(username, password, confirmationCode);

            if (!accountVerified) {
                throw new BadCredentialsException(ExceptionMessages.ACCOUNT_VERIFY_ERROR);
            }

            User user = findUserByUsername(username);

            boolean accountEnabled = user.getIsEnabled();

            if (!accountEnabled) {
                throw new BadCredentialsException(ExceptionMessages.ACCOUNT_ENABLED_ERROR);
            }

            return ResponseVerify.builder()
                    .item("email")
                    .email(user.getEmail())
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.EMAIL_VERIFIED_ERROR + e.getMessage());
        }
    }

    public ResponseUnlocked unlockAccount(RequestVerify request) throws Exception {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String confirmationCode = request.getConfirmationCode();

            boolean accountVerified = verifyAccount(username, password, confirmationCode);

            if (!accountVerified) {
                throw new Exception(ExceptionMessages.ACCOUNT_VERIFY_ERROR);
            }

            User user = findUserByUsername(username);

            if (user == null) {
                throw new UserNotFoundException();
            }

            String email = user.getEmail();

            boolean accountUnlocked = serviceUserDetails.setAccountUnlocked(email, confirmationCode);

            if (!accountUnlocked) {
                throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
            }

            boolean accountEnabled = user.getIsEnabled();

            if (!accountEnabled) {
                throw new Exception(ExceptionMessages.ACCOUNT_ENABLED_ERROR);
            }

            boolean accountLocked = user.getIsAccountNonLocked();

            if (!accountLocked) {
                throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
            }

            return ResponseUnlocked.builder()
                    .username(username)
                    .email(email)
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR + e.getMessage());
        }
    }

    public ResponseAdd addEmail(RequestAddEmail request) throws Exception {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String newEmail = request.getNewEmail();
            String token = request.getToken();

            boolean emailIsValid = validEmail(newEmail);

            if (!emailIsValid) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
            }

            User userCredentials = validateCredentials(username, password);

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

            return ResponseAdd.builder()
                    .item("email")
                    .email(email)
                    .build();
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

            boolean usernameIsValid = validUsername(newUsername);

            if (!usernameIsValid) {
                throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_VALID);
            }

            User userCredentials = validateCredentials(username, password);

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

            return ResponseChange.builder()
                    .item("username")
                    .email(email)
                    .build();
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

            boolean passwordsMatch = passwordsMatch(newPassword, confirmPassword);

            if (!passwordsMatch) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORDS_DO_NOT_MATCH);
            }

            User userCredentials = validateCredentials(username, password);

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

            boolean passwordValid = validPassword(newPassword);

            if (!passwordValid) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_NOT_VALID);
            }

            User userCredentials = validateConfirmationCode(username, confirmationCode);

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

            return ResponseUpdate.builder()
                    .item("username")
                    .email(email)
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.PASSWORD_UPDATE_ERROR + e.getMessage());
        }
    }

    public ResponseForgot forgotPassword(@NotNull RequestForgot request) throws Exception {
        try {
            String email = request.getEmail();
            String username = request.getUsername();

            User user;

            if (email == null && username == null) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_CHANGE_NULL);
            }

            if (email == null) {
                user = findUserByUsername(username);
                email = user.getEmail();
            }

            user = findUserByEmail(email);

            if (username != null && user == null) {
                boolean userExistByUsername = iRepositoryUser.existsByUsername(username);

                if (!userExistByUsername) {
                    throw new BadCredentialsException(ExceptionMessages.USER_NOT_FOUND);
                }

                user = findUserByUsername(username);
            }

            if (user == null) {
                throw new UserNotFoundException();
            }

            email = user.getEmail();

            kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_RECOVERY, email);

            return ResponseForgot.builder()
                    .successMessage("Check your email at " + email + " for further instructions")
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.PASSWORD_CHANGE_ERROR + e.getMessage());
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

            User userCredentials = validateCredentials(username, password);

            if (userCredentials == null) {
                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
            }

            boolean validFirstName = validName(newFirstName);

            if (!validFirstName) {
                throw new BadCredentialsException(ExceptionMessages.NAME_NOT_VALID);
            }

            boolean validLastName = validName(newLastName);

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

            return ResponseChange.builder()
                    .item("name")
                    .email(email)
                    .build();
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

            boolean phoneIsValid = validPhone(newPhone);

            if (!phoneIsValid) {
                throw new BadCredentialsException(ExceptionMessages.PHONE_ERROR);
            }

            User userCredentials = validateCredentials(username, password);

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

            return ResponseChange.builder()
                    .item("phone")
                    .email(email)
                    .build();
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

            boolean emailIsValid = validEmail(removeEmail);

            if (!emailIsValid) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
            }

            User userCredentials = validateCredentials(username, password);

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

            return ResponseRemove.builder()
                    .removeEmail(removeEmail)
                    .email(email)
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.EMAIL_REMOVE_ERROR + e.getMessage());
        }
    }

//    public ResponseDelete deleteAccount(RequestDeleteAccount request) {
//        return ResponseDelete.builder()
//                .item("username")
//                .email(email)
//                .build();
//    }
}
