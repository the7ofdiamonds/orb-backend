package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;

import tech.orbfin.api.gateway.model.request.*;
import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.model.user.Role;
import tech.orbfin.api.gateway.model.user.User;

import tech.orbfin.api.gateway.repositories.IRepositoryUser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public User findUserByEmail(String email) throws Exception {
        try {
            log.info("Loading user by email: " + email);

            Optional<User> user = iRepositoryUser.findUserByEmail(email);

            if (user.isEmpty()) {
                return null;
            }

            log.info("Loaded user details: " + user);

            return user.get();
        } catch (Exception e) {
            System.err.println("Error while loading user by username: " + e.getMessage());
            throw new Exception("Error while loading user by username", e);
        }
    }

    public User findUserByUsername(String username) throws Exception {
        try {
            log.info("Loading user by username: " + username);

            Optional<User> user = iRepositoryUser.findUserByUsername(username);

            if (user.isEmpty()) {
                log.info("User could not be found");
                return new User();
            }

            log.info("Loaded user details for username {}: {}", username, user.get());

            return user.get();
        } catch (Exception e) {
            System.err.println("Error while loading user by username: " + e.getMessage());
            throw new Exception("Error while loading user by username", e);
        }
    }

    @Transactional
    public ResponseRegister register(@NotNull RequestRegister request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String password = request.getPassword();
            String confirmPassword = request.getConfirmPassword();
            String firstname = request.getFirstname();
            String lastname = request.getLastname();
            String phone = request.getPhone();
            Object location = request.getLocation();

            log.info("Registering user with the email {} .....", email);
//            Check the location

            boolean emailUsed = iRepositoryUser.existsByEmail(email);

            if (emailUsed) {
                kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_RECOVERY, email);
                return ResponseRegister.builder()
                        .errorMessage("This Email is already in our records. Check your email.")
                        .build();
            }

            boolean usernameExist = iRepositoryUser.existsByUsername(username);

            if (usernameExist) {
                kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_RECOVERY, email);
                return ResponseRegister.builder()
                        .errorMessage("This Username is already in our records. Check your email inbox.")
                        .build();
            }

            if (!password.equals(confirmPassword)) {
                return ResponseRegister.builder()
                        .errorMessage("Passwords do not match.")
                        .build();
            }

            UserRecord firebaseUser = serviceUserFirebase.createUser(email, username, password, phone);

            Optional<User> user = iRepositoryUser.signupUser(
                    email,
                    username,
                    passwordEncoder().encode(password),
                    firstname,
                    lastname,
                    phone,
                    String.valueOf(Role.USER),
                    firebaseUser.getProviderId(),
                    TRUE,
                    TRUE,
                    TRUE,
                    TRUE,
                    TRUE
            );

            if (user.isEmpty()) {
                return ResponseRegister.builder()
                        .errorMessage("There was an error signing up please try again at another time.")
                        .build();
            }
            ;

            var savedUser = user.get();

            log.info("Username {} has been signed up successfully", username);
            log.info("Creating a session for {} ....", username);

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", location);

            kafkaTemplate.send(ConfigKafkaTopics.USER_REGISTER, email);

            return new ResponseRegister(savedUser.getUsername(), savedUser.getEmail());
        } catch (Exception e) {
            System.err.println("Error while signing up user : " + e.getMessage());

            return ResponseRegister.builder()
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    public User validateCredentials(String email, String username, String password) throws Exception {
        try {
            if (email == null) {
                return null;
            }

            if (username == null) {
                return null;
            }

            User user = findUserByEmail(email);

            if (user == null) {
                return null;
            }

            if (password == null) {
                return null;
            }

            if (!passwordEncoder().matches(password, user.getPassword())) {
                return null;
            }

            return user;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public User validateConfirmationCode(String email, String username, String confirmationCode) throws Exception {
        try {
            if (email == null) {
                return null;
            }

            if (username == null) {
                return null;
            }

            User user = findUserByEmail(email);

            if (user == null) {
                return null;
            }

            if (confirmationCode == null) {
                return null;
            }

            String savedConfirmationCode = user.getConfirmationCode();

            if (!confirmationCode.equals(savedConfirmationCode)) {
                return null;
            }

            return user;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseVerify verifyEmail(RequestVerifyEmail request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String password = request.getPassword();
            String confirmationCode = request.getConfirmationCode();

            User userCredentials = validateConfirmationCode(email, username, confirmationCode);

            if (!(userCredentials instanceof User)) {
                return ResponseVerify.builder()
                        .errorMessage(String.valueOf(userCredentials))
                        .build();
            }

            if (!passwordEncoder().matches(password, userCredentials.getPassword())) {
                return ResponseVerify.builder()
                        .errorMessage("Wrong password. If you have forgot your password click the FORGOT button.")
                        .build();
            }

            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);
            String uid = firebaseUser.getUid();

            log.info(String.valueOf(firebaseUser.isEmailVerified()));

            if (firebaseUser.isEmailVerified()) {
                return ResponseVerify.builder()
                        .errorMessage("This email has already been verified.")
                        .build();
            }

            boolean emailVerified = serviceUserFirebase.emailVerified(uid, true);

            if (!emailVerified) {
                return ResponseVerify.builder()
                        .errorMessage("There was an error verifying your email with Google Firebase try again at another time.")
                        .build();
            }

            return ResponseVerify.builder()
                    .item("username")
                    .email(email)
                    .build();
        } catch (Exception e) {
            return ResponseVerify.builder()
                    .errorMessage("There was an error trying to add an additional email to your account: " + e.getLocalizedMessage())
                    .build();
        }
    }

//    public ResponseRemove unlockAccount(RequestUnlockAccount request) {
//        return ResponseRemove.builder()
//                .item("username")
//                .email(email)
//                .build();
//    }

    public ResponseAdd addEmail(RequestAddEmail request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String password = request.getPassword();
            String newEmail = request.getNewEmail();

            User userCredentials = validateCredentials(email, username, password);

            if (!(userCredentials instanceof User)) {
                return ResponseAdd.builder()
                        .errorMessage(String.valueOf(userCredentials))
                        .build();
            }

            boolean emailAdded = iRepositoryUser.addNewEmail(email, username, newEmail);

            if (!emailAdded) {
                return ResponseAdd.builder()
                        .errorMessage("There was an error changing your username please try again at another time.")
                        .build();
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
        } catch (Exception e) {
            return ResponseAdd.builder()
                    .errorMessage("There was an error trying to add an additional email to your account: " + e.getLocalizedMessage())
                    .build();
        }
    }

    public ResponseChange changeUsername(RequestChangeUsername request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String password = request.getPassword();
            String newUsername = request.getNewUsername();
            String encryptedPassword = passwordEncoder().encode(password);

            boolean usernameChanged = iRepositoryUser.changeUsername(email, username, encryptedPassword, newUsername);

            if (!usernameChanged) {
                return ResponseChange.builder()
                        .errorMessage("There was an error changing your username please try again at another time.")
                        .build();
            }

            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);

            String uid = firebaseUser.getUid();

            serviceUserFirebase.changeUsername(uid, newUsername);

            kafkaTemplate.send(ConfigKafkaTopics.USERNAME_CHANGED, email);

            return ResponseChange.builder()
                    .item("username")
                    .email(email)
                    .build();
        } catch (Exception e) {
            return ResponseChange.builder()
                    .errorMessage("There was an error trying to change your username: " + e.getLocalizedMessage())
                    .build();
        }
    }

    public ResponseChange changePassword(@NotNull RequestChangePassword request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String newPassword = request.getNewPassword();
            String confirmationPassword = request.getConfirmationPassword();

            boolean userExistsByUsername = iRepositoryUser.existsByUsername(username);

            if (!userExistsByUsername) {
                return ResponseChange.builder()
                        .errorMessage("A user could not be found with this Username. Check your inbox.")
                        .build();
            }

            Optional<User> user = iRepositoryUser.findUserByUsername(username);

            if (!passwordEncoder().matches(password, user.get().getPassword())) {
                return ResponseChange.builder()
                        .errorMessage("Wrong password. If you have forgot your password click the FORGOT button.")
                        .build();
            }

            if (!newPassword.equals(confirmationPassword)) {
                return ResponseChange.builder()
                        .errorMessage("You need to enter the new password twice, ensuring they match exactly.")
                        .build();
            }

            String email = user.get().getEmail();

            log.info("User with the email {} is attempting to change their password.", email);

            boolean passwordChanged = iRepositoryUser.changePassword(email, username, passwordEncoder().encode(newPassword));

            if (!passwordChanged) {
                return ResponseChange.builder()
                        .errorMessage("There was na error changing your password.")
                        .build();
            }

            kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_CHANGED, email);

            return new ResponseChange("password", email);
        } catch (Exception e) {
            return ResponseChange.builder()
                    .errorMessage("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    public ResponseUpdate updatePassword(RequestUpdatePassword request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String confirmationCode = request.getConfirmationCode();
            String newPassword = request.getNewPassword();

            User userCredentials = validateConfirmationCode(email, username, confirmationCode);

            if (!(userCredentials instanceof User)) {
                return ResponseUpdate.builder()
                        .errorMessage(String.valueOf(userCredentials))
                        .build();
            }

            boolean passwordUpdated = iRepositoryUser.changePassword(email, username, newPassword);

            if (!passwordUpdated) {
                return ResponseUpdate.builder()
                        .errorMessage("There was an error updating your password please try again at another time.")
                        .build();
            }

            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);

            String uid = firebaseUser.getUid();

            serviceUserFirebase.changePassword(uid, newPassword);

            kafkaTemplate.send(ConfigKafkaTopics.USERNAME_CHANGED, email);

            return ResponseUpdate.builder()
                    .item("username")
                    .email(email)
                    .build();
        } catch (Exception e) {
            return ResponseUpdate.builder()
                    .errorMessage("There was an error trying to add an additional email to your account: " + e.getLocalizedMessage())
                    .build();
        }
    }

    public ResponseForgot forgotPassword(@NotNull RequestForgot request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();

            Optional<User> user;

            if (email == null && username == null) {
                return ResponseForgot.builder()
                        .errorMessage("Either a username or email is required to restore your account.")
                        .build();
            }

            if (email == null) {
                boolean userExistByUsername = iRepositoryUser.existsByUsername(username);

                if (!userExistByUsername) {
                    return ResponseForgot.builder()
                            .errorMessage("This username " + username + " is not in use. Please provide your email.")
                            .build();
                }

                user = iRepositoryUser.findUserByUsername(username);

                email = user.get().getEmail();
            }

            boolean userExistByEmail = iRepositoryUser.existsByEmail(email);

            if (!userExistByEmail) {
                return ResponseForgot.builder()
                        .errorMessage("This email is not in use check your inbox.")
                        .build();
            }

            user = iRepositoryUser.findUserByEmail(email);

            if (username != null && user.isEmpty()) {
                boolean userExistByUsername = iRepositoryUser.existsByUsername(username);

                if (!userExistByUsername) {
                    return ResponseForgot.builder()
                            .errorMessage("This username " + username + " is not in use. Please provide your email.")
                            .build();
                }

                user = iRepositoryUser.findUserByUsername(username);
            }

            email = user.get().getEmail();

            kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_RECOVERY, email);

            return ResponseForgot.builder()
                    .successMessage("Check your email at " + email + " for further instructions")
                    .build();
        } catch (Exception e) {
            return ResponseForgot.builder()
                    .errorMessage("Internal server error: " + e)
                    .build();
        }
    }

    public ResponseChange changeName(RequestChangeName request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String password = request.getPassword();
            String newFirstName = request.getNewFirstName();
            String newLastName = request.getNewLastName();

            if (newFirstName == null && newLastName == null) {
                return ResponseChange.builder()
                        .errorMessage("Enter the first and/or last name you would like to change.")
                        .build();
            }

            if (email == null) {
                return ResponseChange.builder()
                        .errorMessage("Your email is required to change your name please check your inbox.")
                        .build();
            }

            if (username == null) {
                return ResponseChange.builder()
                        .errorMessage("Your username is required to change your name please check your inbox.")
                        .build();
            }

            User user = findUserByEmail(email);

            if (user == null) {
                return ResponseChange.builder()
                        .errorMessage("Could not find user with the username " + username + ".")
                        .build();
            }

            if (password == null) {
                return ResponseChange.builder()
                        .errorMessage("Please enter your password to change your name.")
                        .build();
            }

            log.info(user.getUsername());
            if (!passwordEncoder().matches(password, user.getPassword())) {
                return ResponseChange.builder()
                        .errorMessage("Wrong password. If you have forgot your password click the FORGOT button.")
                        .build();
            }

            if (newFirstName != null) {
                boolean firstNameChanged = iRepositoryUser.changeFirstName(email, username, newFirstName);

                if (!firstNameChanged) {
                    return ResponseChange.builder()
                            .errorMessage("There was an error changing your first name please try again at another time.")
                            .build();
                }
            }

            if (newLastName != null) {
                boolean lastNameChanged = iRepositoryUser.changeLastName(email, username, newLastName);
                log.info(String.valueOf(lastNameChanged));

                if (!lastNameChanged) {
                    return ResponseChange.builder()
                            .errorMessage("There was an error changing your last name please try again at another time.")
                            .build();
                }
            }

            kafkaTemplate.send(ConfigKafkaTopics.NAME_CHANGED, email);

            return ResponseChange.builder()
                    .item("name")
                    .email(email)
                    .build();
        } catch (Exception e) {
            return ResponseChange.builder()
                    .errorMessage("There was an error trying to change your name: " + e)
                    .build();
        }
    }

    public ResponseChange changePhone(RequestChangePhone request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String password = request.getPassword();
            String newPhone = request.getNewPhone();

            if (newPhone == null) {
                return ResponseChange.builder()
                        .errorMessage("Enter the new phone number to make the change.")
                        .build();
            }

            if (email == null) {
                return ResponseChange.builder()
                        .errorMessage("Your email is required to change your name please check your inbox.")
                        .build();
            }

            if (username == null) {
                return ResponseChange.builder()
                        .errorMessage("Your username is required to change your name please check your inbox.")
                        .build();
            }

            if (password == null) {
                return ResponseChange.builder()
                        .errorMessage("Please enter your password to change your name.")
                        .build();
            }

            boolean phoneChanged = iRepositoryUser.changePhoneNumber(email, username, newPhone);

            if (!phoneChanged) {
                return ResponseChange.builder()
                        .errorMessage("There was an error changing your phone number please try again at another time.")
                        .build();
            }

            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);
            String uid = firebaseUser.getUid();

            serviceUserFirebase.changePhone(uid, newPhone);

            kafkaTemplate.send(ConfigKafkaTopics.PHONE_CHANGED, email);

            return ResponseChange.builder()
                    .item("phone")
                    .email(email)
                    .build();
        } catch (Exception e) {
            return ResponseChange.builder()
                    .errorMessage("There was an error trying to change your username: " + e)
                    .build();
        }
    }

    public ResponseRemove removeEmail(RequestRemoveEmail request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String password = request.getPassword();
            String removeEmail = request.getRemoveEmail();

            User userCredentials = validateCredentials(email, username, password);

            if (!(userCredentials instanceof User)) {
                return ResponseRemove.builder()
                        .errorMessage(String.valueOf(userCredentials))
                        .build();
            }

            boolean emailRemoved = repositoryUser.removeEmail(email, username, removeEmail);

            if (!emailRemoved) {
                return ResponseRemove.builder()
                        .errorMessage("There was an error removing your username please try again at another time.")
                        .build();
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
        } catch (Exception e) {
            return ResponseRemove.builder()
                    .errorMessage("There was an error trying to remove an email to your account: " + e.getLocalizedMessage())
                    .build();
        }
    }

//    public ResponseDelete deleteAccount(RequestDeleteAccount request) {
//        return ResponseDelete.builder()
//                .item("username")
//                .email(email)
//                .build();
//    }
}
