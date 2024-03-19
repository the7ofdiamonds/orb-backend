//package tech.orbfin.api.gateway.services;
//
//import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;
//
//import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
//import tech.orbfin.api.gateway.exceptions.ExceptionMessages;
//
//import tech.orbfin.api.gateway.model.response.*;
//
//import tech.orbfin.api.gateway.model.wordpress.User;
//
//import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUserChange;
//import tech.orbfin.api.gateway.repositories.RepositoryUser;
//
//import tech.orbfin.api.gateway.services.firebase.ServiceUserFirebase;
//
//import java.util.Arrays;
//
//import java.util.stream.Collectors;
//
//import jakarta.transaction.Transactional;
//
//import lombok.RequiredArgsConstructor;
//
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.context.annotation.Bean;
//
//import org.springframework.stereotype.Service;
//
//import org.springframework.kafka.core.KafkaTemplate;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import com.google.firebase.auth.UserInfo;
//import com.google.firebase.auth.UserProvider;
//import com.google.firebase.auth.UserRecord;
//
//@Slf4j
//@RequiredArgsConstructor
//@Transactional
//@Service
//public class ServiceUserChange {
//    private final IRepositoryUserChange iRepositoryUserChange;
//    private final RepositoryUser repositoryUser;
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//    private final ServiceUserFirebase serviceUserFirebase;
//    private final ServiceUserUtils serviceUserUtils;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    //Needs more work
//    public ResponseAdd addEmail(String username, String password, String newEmail, String token) throws Exception {
//        try {
//            boolean emailIsValid = serviceUserUtils.validEmail(newEmail);
//
//            if (!emailIsValid) {
//                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
//            }
//
//            User userCredentials = serviceUserUtils.validateCredentials(username, password);
//
//            if (userCredentials == null) {
//                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
//            }
//
//            String email = userCredentials.getEmail();
//
//            boolean emailAdded = iRepositoryUserChange.addNewEmail(email, username, newEmail);
//
//            if (!emailAdded) {
//                throw new Exception(ExceptionMessages.EMAIL_ADD_ERROR);
//            }
//// Get provider id based on the new way to login
//            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);
//
//            String uid = firebaseUser.getUid();
//            var providerId = firebaseUser.getProviderId();
//            var provider = UserProvider.builder()
//                    .setUid(uid)
//                    .setEmail(newEmail)
//                    .setProviderId(providerId)
//                    .build();
//
//            serviceUserFirebase.addNewEmail(uid, provider);
//
//            kafkaTemplate.send(ConfigKafkaTopics.EMAIL_ADDED, email);
//
//            return new ResponseAdd("email", email);
//        } catch (BadCredentialsException e) {
//            throw new BadCredentialsException(e.getMessage());
//        } catch (Exception e) {
//            throw new Exception(ExceptionMessages.EMAIL_ADD_ERROR + e.getMessage());
//        }
//    }
//
//    public ResponseChange changeUsername(String username, String password, String newUsername) throws Exception {
//        try {
//            boolean usernameIsValid = serviceUserUtils.validUsername(newUsername);
//
//            if (!usernameIsValid) {
//                throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_VALID);
//            }
//
//            User userCredentials = serviceUserUtils.validateCredentials(username, password);
//
//            if (userCredentials == null) {
//                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
//            }
//
//            String email = userCredentials.getEmail();
//
//            boolean usernameChanged = iRepositoryUserChange.changeUsername(email, username, newUsername);
//
//            if (!usernameChanged) {
//                throw new Exception(ExceptionMessages.USERNAME_CHANGE_ERROR);
//            }
//
//            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);
//
//            String uid = firebaseUser.getUid();
//
//            serviceUserFirebase.changeUsername(uid, newUsername);
//
//            kafkaTemplate.send(ConfigKafkaTopics.USERNAME_CHANGED, email);
//
//            return new ResponseChange("username", email);
//        } catch (BadCredentialsException e) {
//            throw new BadCredentialsException(e.getMessage());
//        } catch (Exception e) {
//            throw new Exception(ExceptionMessages.USERNAME_CHANGE_ERROR + e.getMessage());
//        }
//    }
//
//    public ResponseChange changePassword(String username, String password, String newPassword, String confirmPassword) throws Exception {
//        try {
//            boolean passwordsMatch = serviceUserUtils.passwordsMatch(newPassword, confirmPassword);
//
//            if (!passwordsMatch) {
//                throw new BadCredentialsException(ExceptionMessages.PASSWORDS_DO_NOT_MATCH);
//            }
//
//            User userCredentials = serviceUserUtils.validateCredentials(username, password);
//
//            if (userCredentials == null) {
//                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
//            }
//
//            String email = userCredentials.getEmail();
//
//            log.info("User with the email {} is attempting to change their password.", email);
//
//            boolean passwordChanged = iRepositoryUserChange.changePassword(email, username, passwordEncoder().encode(newPassword));
//
//            if (!passwordChanged) {
//                throw new Exception(ExceptionMessages.PASSWORD_CHANGE_ERROR);
//            }
//
//            kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_CHANGED, email);
//
//            return new ResponseChange("password", email);
//        } catch (BadCredentialsException e) {
//            throw new BadCredentialsException(e.getMessage());
//        } catch (Exception e) {
//            throw new Exception(ExceptionMessages.PASSWORD_CHANGE_ERROR);
//        }
//    }
//
//    public ResponseUpdate updatePassword(String username, String confirmationCode, String newPassword) throws Exception {
//        try {
//            boolean passwordValid = serviceUserUtils.validPassword(newPassword);
//
//            if (!passwordValid) {
//                throw new BadCredentialsException(ExceptionMessages.PASSWORD_NOT_VALID);
//            }
//
//            User userCredentials = serviceUserUtils.validateConfirmationCode(username, confirmationCode);
//
//            if (userCredentials == null) {
//                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
//            }
//
//            String email = userCredentials.getEmail();
//
//            boolean passwordUpdated = iRepositoryUserChange.changePassword(email, username, newPassword);
//
//            if (!passwordUpdated) {
//                throw new Exception(ExceptionMessages.PASSWORD_UPDATE_ERROR);
//            }
//
//            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);
//
//            String uid = firebaseUser.getUid();
//
//            boolean passwordUpdatedFirebase = serviceUserFirebase.passwordChanged(uid, newPassword);
//
//            if (!passwordUpdatedFirebase) {
//                throw new Exception(ExceptionMessages.PASSWORD_CHANGE_ERROR);
//            }
//
//            kafkaTemplate.send(ConfigKafkaTopics.USERNAME_CHANGED, email);
//
//            return new ResponseUpdate("username", email);
//        } catch (BadCredentialsException e) {
//            throw new BadCredentialsException(e.getMessage());
//        } catch (Exception e) {
//            throw new Exception(ExceptionMessages.PASSWORD_UPDATE_ERROR + e.getMessage());
//        }
//    }
//
//    public ResponseChange changeName(String username, String password, String newFirstName, String newLastName) throws Exception {
//        try {
//            if (newFirstName == null && newLastName == null) {
//                throw new BadCredentialsException(ExceptionMessages.NAME_NULL);
//            }
//
//            User userCredentials = serviceUserUtils.validateCredentials(username, password);
//
//            if (userCredentials == null) {
//                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
//            }
//
//            boolean validFirstName = serviceUserUtils.validName(newFirstName);
//
//            if (!validFirstName) {
//                throw new BadCredentialsException(ExceptionMessages.NAME_NOT_VALID);
//            }
//
//            boolean validLastName = serviceUserUtils.validName(newLastName);
//
//            if (!validLastName) {
//                throw new BadCredentialsException(ExceptionMessages.NAME_NOT_VALID);
//            }
//
//            String email = userCredentials.getEmail();
//
//            if (newFirstName != null) {
//                boolean firstNameChanged = iRepositoryUserChange.changeFirstName(email, username, newFirstName);
//
//                if (!firstNameChanged) {
//                    throw new Exception(ExceptionMessages.NAME_CHANGE_ERROR);
//                }
//            }
//
//            if (newLastName != null) {
//                boolean lastNameChanged = iRepositoryUserChange.changeLastName(email, username, newLastName);
//
//                if (!lastNameChanged) {
//                    throw new Exception(ExceptionMessages.NAME_CHANGE_ERROR);
//                }
//            }
//
//            kafkaTemplate.send(ConfigKafkaTopics.NAME_CHANGED, email);
//
//            return new ResponseChange("name", email);
//        } catch (BadCredentialsException e) {
//            throw new BadCredentialsException(e.getMessage());
//        } catch (Exception e) {
//            throw new Exception(ExceptionMessages.NAME_CHANGE_ERROR);
//        }
//    }
//
//    public ResponseChange changePhone(String username, String password, String newPhone) throws Exception {
//        try {
//            boolean phoneIsValid = serviceUserUtils.validPhone(newPhone);
//
//            if (!phoneIsValid) {
//                throw new BadCredentialsException(ExceptionMessages.PHONE_ERROR);
//            }
//
//            User userCredentials = serviceUserUtils.validateCredentials(username, password);
//
//            if (userCredentials == null) {
//                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
//            }
//
//            String email = userCredentials.getEmail();
//
//            boolean phoneChanged = iRepositoryUserChange.changePhoneNumber(email, username, newPhone);
//
//            if (!phoneChanged) {
//                throw new Exception(ExceptionMessages.PHONE_ERROR);
//            }
//
//            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);
//            String uid = firebaseUser.getUid();
//
//            serviceUserFirebase.changePhone(uid, newPhone);
//
//            kafkaTemplate.send(ConfigKafkaTopics.PHONE_CHANGED, email);
//
//
//            return new ResponseChange("phone", email);
//        } catch (BadCredentialsException e) {
//            throw new BadCredentialsException(e.getMessage());
//        } catch (Exception e) {
//            throw new Exception(ExceptionMessages.PHONE_ERROR + e.getMessage());
//        }
//    }
//
//    public ResponseRemove removeEmail(String username, String password, String removeEmail) throws Exception {
//        try {
//            boolean emailIsValid = serviceUserUtils.validEmail(removeEmail);
//
//            if (!emailIsValid) {
//                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_VALID);
//            }
//
//            User userCredentials = serviceUserUtils.validateCredentials(username, password);
//
//            if (userCredentials == null) {
//                throw new BadCredentialsException(ExceptionMessages.CREDENTIALS_BAD);
//            }
//
//            String email = userCredentials.getEmail();
//
//            boolean emailRemoved = repositoryUser.removeEmail(email, username, removeEmail);
//
//            if (!emailRemoved) {
//                throw new Exception(ExceptionMessages.EMAIL_REMOVE_ERROR);
//            }
//
//            UserRecord firebaseUser = serviceUserFirebase.getUserByEmail(email);
//
//            String uid = firebaseUser.getUid();
//            Iterable<String> providerIds = Arrays.asList(firebaseUser.getProviderData()).stream().map(UserInfo::getProviderId).collect(Collectors.toList());
//            serviceUserFirebase.removeEmail(uid, providerIds);
//
//            kafkaTemplate.send(ConfigKafkaTopics.EMAIL_REMOVED, email);
//
//            return new ResponseRemove(removeEmail, email);
//        } catch (BadCredentialsException e) {
//            throw new BadCredentialsException(e.getMessage());
//        } catch (Exception e) {
//            throw new Exception(ExceptionMessages.EMAIL_REMOVE_ERROR + e.getMessage());
//        }
//    }
//}
