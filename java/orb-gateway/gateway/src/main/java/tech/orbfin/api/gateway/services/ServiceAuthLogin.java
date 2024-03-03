package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;

import tech.orbfin.api.gateway.repositories.IRepositoryUser;
import tech.orbfin.api.gateway.repositories.IRepositorySession;

import tech.orbfin.api.gateway.model.Session;
import tech.orbfin.api.gateway.model.user.User;
import tech.orbfin.api.gateway.model.request.RequestLogin;
import tech.orbfin.api.gateway.model.response.ResponseLogin;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.firebase.auth.UserRecord;

import org.springframework.stereotype.Service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.core.context.SecurityContextHolder;

import org.jetbrains.annotations.NotNull;

import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceAuthLogin {
    private final ServiceUserFirebase serviceUserFirebase;
    private final ServiceUser serviceUser;
    private final ServiceTokenJW serviceTokenJW;
    private final IRepositoryUser iRepositoryUser;
    private final IRepositorySession iRepositorySession;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ResponseLogin login(@NotNull RequestLogin request) {
        try {
            log.info("Login function has been called.");

            var username = request.getUsername();
            var password = request.getPassword();
            Object location = request.getLocation();

            log.info("User {} is attempting to login", username);
            boolean usernameExists = iRepositoryUser.existsByUsername(username);

            if (!usernameExists) {
                return ResponseLogin.builder()
                        .errorMessage("The username " + username + " can not be found.")
                        .build();
            }

            User user = serviceUser.findUserByUsername(username);
            String savedPassword = user.getPassword();
            String email = user.getEmail();

            if (savedPassword.startsWith("$P")) {
                kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_UPDATE, email);
                return ResponseLogin.builder()
                        .errorMessage("Password needs to be updated check your email inbox.")
                        .build();
            }

            if (!passwordEncoder.matches(password, savedPassword)) {
                return ResponseLogin.builder()
                        .errorMessage("The username " + username + " and the password provided do not match.")
                        .build();
            }

            username = user.getUsername();
            var role = user.getRoles();

            log.info("{} {} is attempting to login.", role, username);

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", location);

            UserRecord userRecord = serviceUserFirebase.getUserByEmail(email);

            if (userRecord == null) {
                log.info("Firebase User with the email {} does not exists.", email);
                log.info("Adding user with the email {} to firebase", email);

                serviceUserFirebase.createUser(email, username, password, null);
            }

            log.info("Username {} is recorded in the Firebase Users Database with the email {}.", username, email);

            String accessToken = serviceTokenJW.generateToken(extraClaims, user);
            String refreshToken = serviceTokenJW.refreshToken(user);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            SecurityContextHolder.getContext().setAuthentication(token);

            iRepositorySession.save(new Session(accessToken, serviceTokenJW.ALGORITHM, refreshToken, user.getId(), true, false, false));

            log.info("Session created successfully for {}", username);

            return ResponseLogin.builder()
                    .username(username)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            return ResponseLogin.builder()
                    .errorMessage("Internal server error: " + e.getMessage())
                    .build();
        }
    }
}