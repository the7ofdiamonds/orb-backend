package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.model.user.Role;
import tech.orbfin.api.gateway.model.Session;
import tech.orbfin.api.gateway.model.user.UserEntity;

import tech.orbfin.api.gateway.repositories.RepositoryUser;
import tech.orbfin.api.gateway.repositories.RepositorySession;

import tech.orbfin.api.gateway.request.RequestRegister;
import tech.orbfin.api.gateway.request.RequestLogin;
import tech.orbfin.api.gateway.request.RequestChange;
import tech.orbfin.api.gateway.request.RequestLogout;
import tech.orbfin.api.gateway.request.RequestForgot;

import tech.orbfin.api.gateway.response.ResponseRegister;
import tech.orbfin.api.gateway.response.ResponseLogin;
import tech.orbfin.api.gateway.response.ResponseChange;
import tech.orbfin.api.gateway.response.ResponseLogout;
import tech.orbfin.api.gateway.response.ResponseForgot;

import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.auth.UserRecord;

import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;

import org.springframework.stereotype.Service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Service
@AllArgsConstructor
public class ServiceAuth {
    private RepositoryUser repositoryUser;
    private RepositorySession repositorySession;
    private ServiceTokenJW serviceTokenJW;
    private final ServiceTokenFirebase serviceTokenFirebase;
    private final ServiceUserFirebase serviceUserFirebase;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Transactional
    public ResponseRegister register(@NotNull RequestRegister request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String password = passwordEncoder().encode(request.getPassword());
            String firstname = request.getFirstname();
            String lastname = request.getLastname();
            String phone = request.getPhone();
            Object location = request.getLocation();

            log.info("Registering user with the email {} .....", email);

            UserRecord userRecord = serviceUserFirebase.createUser(email, username, request.getPassword(), phone);
            var firebaseID = userRecord.getUid();
            var firebaseEmail = userRecord.getEmail();

            log.info("{} has been saved to Firebase with the email {} and User ID {}", username, firebaseEmail, firebaseID);

            var emailUsed = repositoryUser.existsByEmail(firebaseEmail);

            if (emailUsed) {
//                Send email
                throw new Exception("This Email is already in our records. Check your email.");
            }

            var userExist = repositoryUser.existsByUsername(username);

            if (userExist) {
//                Send email
                throw new Exception("This Username is already in our records. Check your email.");
            }

            var user = UserEntity.builder()
                    .email(email)
                    .username(username)
                    .password(password)
                    .firstname(firstname)
                    .lastname(lastname)
                    .phone(phone)
                    .roles(Collections.singleton(Role.SUBSCRIBER))
                    .providerGivenID(firebaseID)
                    .build();
            var savedUser = repositoryUser.signupUser(user);

            log.info("Username {} has been signed up successfully", username);
            log.info("Creating a session for {} ....", username);

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", location);

            String accessToken = serviceTokenJW.generateToken(extraClaims, savedUser);
            String refreshToken = serviceTokenJW.refreshToken(user);

            var session = new Session<>(accessToken, "JWT", refreshToken, savedUser.getId());

            repositorySession.save(session).subscribe();

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            SecurityContextHolder.getContext().setAuthentication(token);

            log.info("Session created successfully for {}", username);

            return new ResponseRegister(savedUser.getUsername(), savedUser.getEmail());
        } catch (Exception e){
            System.err.println("Error while signing up user : " + e.getMessage());

            return ResponseRegister.builder()
                    .success(null)
                    .accessToken(null)
                    .refreshToken(null)
                    .error(e.getMessage())
                    .build();
        }
    }

    public ResponseLogin login(@NotNull RequestLogin request){
        try {
            log.info("Login function has been called.");

            var username = request.getUsername();
            var password = request.getPassword();
            Object location = request.getLocation();

            boolean userExists = repositoryUser.existsByUsername(username);

            if (!userExists) {
                return ResponseLogin.builder()
                                .success(null)
                                .error("The username " + username + " can not be found.")
                                .build();
            }

            boolean usernamePasswordMatches = repositoryUser.usernamePasswordMatches(username, password);

            if (!usernamePasswordMatches) {
                return ResponseLogin.builder()
                        .success(null)
                        .error("The username " + username + " and the password provided do not match.")
                        .build();
            }

            UserEntity userEntity = repositoryUser.loginUser(username, password);

            var email = userEntity.getEmail();
            username = userEntity.getUsername();
            var role = userEntity.getRoles();

            log.info("{} {} is attempting to login.", role, username);

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", location);

            UserRecord userRecord = serviceUserFirebase.getUserByEmail(email);

            if(userRecord == null){
                log.info("Firebase User with the email {} does not exists.", email);
            }

            log.info("Username {} is recorded in the Firebase Users Database with the email {}.", username, email);

            String accessToken = serviceTokenJW.generateToken(extraClaims, userEntity);
            String refreshToken = serviceTokenJW.refreshToken(userEntity);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            SecurityContextHolder.getContext().setAuthentication(token);

            repositorySession.save(new Session<>(accessToken, "Firebase Token", refreshToken, userEntity.getId()))
                    .doOnError(e -> {
                        throw new RuntimeException(e);
                    })
                    .subscribe();

            log.info("Session created successfully for {}", username);

            return new ResponseLogin(username, accessToken, refreshToken);
        } catch(Exception e) {
            return ResponseLogin.builder()
                    .success(null)
                    .accessToken(null)
                    .refreshToken(null)
                    .error("Internal server error: " + e.getMessage())
                    .build();
        }
    }
    
    public ResponseChange changePassword(@NotNull RequestChange request) {
        try {
            String email = request.getEmail();
            String password = request.getPassword();
            String newPassword = request.getNewPassword();
            String confirmationPassword = request.getConfirmationPassword();

            UserEntity user = repositoryUser.findUserByEmail(email);

            log.info("User with the email {} is attempting to change their password.", email);

            if(user == null){
                return ResponseChange.builder()
                                .error("A user could not be found with this email. Check your inbox.")
                                .build();
            }

            if (!passwordEncoder().matches(password, user.getPassword())) {
                return ResponseChange.builder()
                                .error("Wrong password. If you have forgot your password click the FORGOT button.")
                                .build();
            }

            if (!newPassword.equals(confirmationPassword)) {
                return ResponseChange.builder()
                                .error("You need to enter the new password twice, ensuring they match exactly.")
                                .build();
            }

            user.setPassword(passwordEncoder().encode(newPassword));

            UserEntity savedUser = repositoryUser.signupUser(user);
//  Send Password Changed Email
            return new ResponseChange(savedUser.getEmail());
        } catch (Exception e) {
            return ResponseChange.builder()
                    .success(null)
                    .error("Internal server error: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public ResponseLogout logout(@NotNull RequestLogout request) {
        try {
            String username = serviceTokenJW.extractUsername(request.getToken());
            UserEntity user = repositoryUser.findUserByUsername(username);

            log.info("service auth logout");

            if (user == null) {
                return ResponseLogout.builder()
                        .error("The username " + username + " can not be found.")
                        .build();
            }

            var userid = user.getId();
            var sessions = repositorySession.findAllValidSessionsByUserId(userid);

            sessions.collectList().subscribe(sessionsList -> {
                if (sessionsList.isEmpty()) {
                    SecurityContextHolder.clearContext();
                }

                for (Session session : sessionsList) {
                    session.setExpired(true);
                    session.setRevoked(true);
                    repositorySession.save(session).subscribe();
                }
            });

            return new ResponseLogout(username);
        } catch (Exception e){
            return ResponseLogout.builder()
                    .success(null)
                    .error("Internal server error: " + e.getMessage())
                    .build();
        }
    }


    public ResponseForgot forgotPassword(@NotNull RequestForgot request ){
        try {
            String email = request.getEmail();
            String username = request.getUsername();

            if(email == null && username == null){
                return ResponseForgot.builder()
                        .success(null)
                        .error("Either a username or email is required to restore your account.")
                        .build();
            }

            if(email != null) {
                UserEntity user = repositoryUser.findUserByEmail(email);

                if(user != null) {
                    log.info("Forgot password email sent");
                    //                        emailAuth.sendForgotPasswordEmail(email);

                    return new ResponseForgot(user.getEmail());
                } else {
                    return ResponseForgot.builder()
                            .success(null)
                            .error("This email is not in use check your inbox.")
                            .build();
                }
            }

            boolean userExist = repositoryUser.existsByUsername(username);

            if(userExist) {
                UserEntity user = repositoryUser.findUserByUsername(username);
                log.info("user exist");

                if (user != null) {
                    log.info("Forgot password email sent");
//                       emailAuth.sendForgotPasswordEmail(user.get().getEmail());

                    return new ResponseForgot(user.getEmail());
                } else {
                    return ResponseForgot.builder()
                            .success(null)
                            .error("This user could not be found. Please provide your email.")
                            .build();
                }
            } else {
                return ResponseForgot.builder()
                        .success(null)
                        .error("This username is not in use. Please provide your email.")
                        .build();
            }
        } catch (Exception e) {
            return ResponseForgot.builder()
                    .success(null)
                    .error("Internal server error")
                    .build();
        }
    }
}