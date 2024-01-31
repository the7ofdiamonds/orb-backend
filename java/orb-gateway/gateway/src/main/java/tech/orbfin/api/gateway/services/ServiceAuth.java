package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.entities.token.Token;
import tech.orbfin.api.gateway.entities.user.Role;
import tech.orbfin.api.gateway.entities.Session;
import tech.orbfin.api.gateway.entities.user.UserEntity;
import tech.orbfin.api.gateway.repositories.RepositoryUser;
import tech.orbfin.api.gateway.repositories.RepositorySession;
import tech.orbfin.api.gateway.request.*;
import tech.orbfin.api.gateway.response.*;

import com.google.firebase.auth.UserRecord;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import jakarta.transaction.Transactional;

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
    private final RepositoryUser repositoryUser;
    private final RepositorySession repositorySession;
    private final ServiceTokenJW serviceTokenJW;
    @Autowired
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

//            Check on the roles
            var user = UserEntity.builder()
                    .username(username)
                    .password(password)
                    .firstname(firstname)
                    .lastname(lastname)
                    .phone(phone)
                    .role(Role.USER)
                    .providerGivenID(firebaseID)
                    .build();
            var savedUser = repositoryUser.save(user);

            log.info("{} has been signed up successfully", username);
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

            var password = request.getPassword();
            Object location = request.getLocation();

            Optional<UserEntity> userEntity = repositoryUser.findByUsername(request.getUsername());

            if (userEntity.isEmpty()) {
                return ResponseLogin.builder()
                                .success(null)
                                .error("The username " + request.getUsername() + " can not be found.")
                                .build();
            }

            var user = userEntity.get();
            var email = user.getEmail();
            var username = user.getUsername();
            var role = user.getRole();

            log.info("{} {} is attempting to login.", role, username);

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", location);

            UserRecord userRecord = serviceUserFirebase.getUserByEmail(email);

            if(userRecord == null){
                log.info("Firebase User with the email {} does not exists.", email);
            }

            log.info("Username {} is recorded in the Firebase Users Database with the email {}.", username, email);

            Token<String> accessToken = serviceTokenFirebase.buildToken(extraClaims, userRecord.getUid());
            String refreshToken = serviceTokenJW.refreshToken(user);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            SecurityContextHolder.getContext().setAuthentication(token);

            repositorySession.save(new Session<>(accessToken, "Firebase Token", refreshToken, user.getId()))
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
    
    public ResponseChange changePassword(@NotNull RequestChangePassword request) {
        try {
            String email = request.getEmail();
            String password = request.getPassword();
            String newPassword = request.getNewPassword();
            String confirmationPassword = request.getConfirmationPassword();

            Optional<UserEntity> user = repositoryUser.findByEmail(email);

            log.info("User with the email {} is attempting to change their password.", email);

            if(user.isEmpty()){
                return ResponseChange.builder()
                                .error("A user could not be found with this email. Check your inbox.")
                                .build();
            }

            if (!passwordEncoder().matches(password, user.get().getPassword())) {
                return ResponseChange.builder()
                                .error("Wrong password. If you have forgot your password click the FORGOT button.")
                                .build();
            }

            if (!newPassword.equals(confirmationPassword)) {
                return ResponseChange.builder()
                                .error("You need to enter the new password twice, ensuring they match exactly.")
                                .build();
            }

            UserEntity savedUser = user.get();

            savedUser.setPassword(passwordEncoder().encode(newPassword));

            repositoryUser.save(savedUser);
//  Send Password Changed Email
            return new ResponseChange(user.get().getEmail());
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
            Optional<UserEntity> user = repositoryUser.findByUsername(username);

            log.info("service auth logout");

            if (user.isEmpty()) {
                return ResponseLogout.builder()
                        .error("The username " + username + " can not be found.")
                        .build();
            }

            var userid = user.get().getId();
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


    public ResponseForgot forgotPassword(@NotNull RequestForgotPassword request ){
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
                Optional<UserEntity> user = repositoryUser.findByEmail(email);

                if(user.isPresent()) {
                    log.info("Forgot password email sent");
                    //                        emailAuth.sendForgotPasswordEmail(email);

                    return new ResponseForgot(user.get().getEmail());
                } else {
                    return ResponseForgot.builder()
                            .success(null)
                            .error("This email is not in use check your inbox.")
                            .build();
                }
            }

            boolean userExist = repositoryUser.existsByUsername(username);

            if(userExist) {
                Optional<UserEntity> user = repositoryUser.findByUsername(username);
                log.info("user exist");
                if (user.isPresent()) {
                    log.info("Forgot password email sent");
//                       emailAuth.sendForgotPasswordEmail(user.get().getEmail());

                    return new ResponseForgot(user.get().getEmail());
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