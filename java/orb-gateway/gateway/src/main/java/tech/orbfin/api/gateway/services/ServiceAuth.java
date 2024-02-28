package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.configurations.ConfigTopics;

import tech.orbfin.api.gateway.repositories.RepositorySession;

import tech.orbfin.api.gateway.model.user.Role;
import tech.orbfin.api.gateway.model.Session;
import tech.orbfin.api.gateway.model.user.User;

import tech.orbfin.api.gateway.model.request.RequestRegister;
import tech.orbfin.api.gateway.model.request.RequestLogin;
import tech.orbfin.api.gateway.model.request.RequestLogout;

import tech.orbfin.api.gateway.model.response.ResponseRegister;
import tech.orbfin.api.gateway.model.response.ResponseLogin;
import tech.orbfin.api.gateway.model.response.ResponseLogout;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.firebase.auth.UserRecord;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;

import org.jetbrains.annotations.NotNull;

import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@Service
@AllArgsConstructor
public class ServiceAuth {
    private final ServiceUserFirebase serviceUserFirebase;
    private final ServiceUser serviceUser;
    private final ServiceTokenJW serviceTokenJW;
    private final RepositorySession repositorySession;

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

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
            log.info(password);
//            Check the location

            UserRecord firebaseUser = serviceUserFirebase.createUser(email, username, password, phone);

            User savedUser = serviceUser.signupUser(
                    email, username, password, firstname, lastname, phone);

            log.info("Username {} has been signed up successfully", username);
            log.info("Creating a session for {} ....", username);

            User user = User.builder()
                    .email(email)
                    .username(username)
                    .password(password)
                    .firstname(firstname)
                    .lastname(lastname)
                    .phone(phone)
                    .roles(Collections.singleton(Role.SUBSCRIBER))
                    .providerGivenID(firebaseUser.getUid())
                    .build();

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", location);

            String accessToken = serviceTokenJW.generateToken(extraClaims, user);
            String refreshToken = serviceTokenJW.refreshToken(user);

            Session<String, Object> session = new Session<>(accessToken, "JWT", refreshToken, savedUser.getId());

            repositorySession.save(session).subscribe();

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            SecurityContextHolder.getContext().setAuthentication(token);

            log.info("Session created successfully for {}", username);

            kafkaTemplate.send(ConfigTopics.USER_SIGN_UP, email);

            return new ResponseRegister(user.getUsername(), user.getEmail());
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

    @Transactional
    public ResponseLogin login(@NotNull RequestLogin request){
        try {
            log.info("Login function has been called.");

            var username = request.getUsername();
            var password = passwordEncoder().encode(request.getPassword());
            Object location = request.getLocation();

            User userEntity = serviceUser.loginUser(username, password);

            var email = userEntity.getEmail();
            username = userEntity.getUsername();
            var role = userEntity.getRoles();

            log.info("{} {} is attempting to login.", role, username);

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", location);

            UserRecord userRecord = serviceUserFirebase.getUserByEmail(email);

            if(userRecord == null){
                log.info("Firebase User with the email {} does not exists.", email);
                log.info("Adding user with the email {} to firebase", email);

                serviceUserFirebase.createUser(email, username, password, null);
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

    @Transactional
    public ResponseLogout logout(@NotNull RequestLogout request) {
        try {
            String username = serviceTokenJW.extractUsername(request.getToken());
            User user = serviceUser.findUserByUsername(username);

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
}