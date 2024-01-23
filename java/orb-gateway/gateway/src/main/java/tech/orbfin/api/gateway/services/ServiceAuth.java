package tech.orbfin.api.gateway.services;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tech.orbfin.api.gateway.request.*;
import tech.orbfin.api.gateway.response.*;
import tech.orbfin.api.gateway.entities.user.Role;
import tech.orbfin.api.gateway.entities.token.Token;
import tech.orbfin.api.gateway.entities.token.TokenType;
import tech.orbfin.api.gateway.entities.user.UserEntity;
import tech.orbfin.api.gateway.repositories.RepositoryUser;
import tech.orbfin.api.gateway.repositories.RepositoryToken;

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
    private final RepositoryToken repositoryToken;
    private final ServiceToken serviceToken;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Transactional
    public ResponseEntity<ResponseRegister> register(@NotNull RequestRegister request) {
        try {
            var userExist = repositoryUser.existsByUsername(request.getUsername());

            if (userExist) {
                throw new Exception("This Username is currently in use.");
            }

            var emailUsed = repositoryUser.existsByEmail(request.getEmail());

            if (emailUsed) {
                throw new Exception("This Email is currently in use.");
            }

            String username = request.getUsername();
            String password = passwordEncoder().encode(request.getPassword());

            var user = new UserEntity(username, password, request.getEmail(), request.getFirstname(), request.getLastname(), Role.USER);
            var savedUser = repositoryUser.save(user);

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", request.getLocation());

            String accessToken = serviceToken.generateToken(extraClaims, savedUser);
            var refreshToken = serviceToken.refreshToken(user);

            var jwt = new Token(accessToken, TokenType.BEARER, refreshToken, savedUser.getId());

            log.info(String.valueOf(jwt));
            repositoryToken.save(jwt);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            SecurityContextHolder.getContext().setAuthentication(token);

            return ResponseEntity.ok()
                    .body(new ResponseRegister(username, savedUser.getEmail()));
        } catch (Exception e){
            System.err.println("Error while loading user by username: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(ResponseRegister.builder()
                    .success(null)
                    .accessToken(null)
                    .refreshToken(null)
                    .error("Internal server error: " + e.getMessage())
                    .build());
        }
    }

    public ResponseEntity<ResponseLogin> login(@NotNull RequestLogin request){
        try {
            var username = request.getUsername();
            var password = request.getPassword();

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            SecurityContextHolder.getContext().setAuthentication(token);

            Optional<UserEntity> userEntity = repositoryUser.findByUsername(username);

            if (userEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                        .body(ResponseLogin.builder()
                                .success(null)
                                .error("The username " + username + " can not be found.")
                                .build()
                        );
            }

            var user = userEntity.get();
//       Add location plus other details to extra claims
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", "test location");

            String accessToken = serviceToken.generateToken(extraClaims, user);
            String refreshToken = serviceToken.refreshToken(user);

            return ResponseEntity.ok()
                    .body(new ResponseLogin(username, accessToken, refreshToken));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(ResponseLogin.builder()
                    .success(null)
                    .accessToken(null)
                    .refreshToken(null)
                    .error("Internal server error: " + e.getMessage())
                    .build());
        }
    }
    
    public ResponseEntity<ResponseChange> changePassword(@NotNull RequestChangePassword request) {
        try {
            Optional<UserEntity> user = repositoryUser.findByEmail(request.getEmail());

            if(user.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                        .body(ResponseChange.builder()
                                .error("A user could not be found with this email. Check your inbox.")
                                .build());
            }

            if (!passwordEncoder().matches(request.getPassword(), user.get().getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                        .body(ResponseChange.builder()
                                .error("Wrong password. If you have forgot your password click the FORGOT button.")
                                .build());
            }

            if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                        .body(ResponseChange.builder()
                                .error("You need to enter the new password twice, ensuring they match exactly.")
                                .build());
            }

            user.get().setPassword(passwordEncoder().encode(request.getNewPassword()));

            repositoryUser.save(user.get());

            return ResponseEntity.ok().body(new ResponseChange(user.get().getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(ResponseChange.builder()
                    .success(null)
                    .error("Internal server error: " + e.getMessage())
                    .build());
        }
    }

    @Transactional
    public ResponseEntity<ResponseLogout> logout(@NotNull RequestLogout request) {
        log.info("service auth logout");
        try {
            String username = serviceToken.extractUsername(request.getToken());
            Optional<UserEntity> user = repositoryUser.findByUsername(username);

            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseLogout.builder()
                                .error("The username " + username + " can not be found.")
                                .build()
                        );
            }

            var userid = user.get().getId();
            var tokens = repositoryToken.findAllValidTokenByUserid(userid);

            if (tokens.isEmpty()) {
                SecurityContextHolder.clearContext();
            }

            for (Token token : tokens) {
                token.setExpired(true);
                token.setRevoked(true);
                repositoryToken.save(token);
            }

            return ResponseEntity.ok()
                    .body(new ResponseLogout(username));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(ResponseLogout.builder()
                            .success(null)
                            .error("Internal server error: " + e.getMessage())
                            .build());
        }
    }

    public ResponseEntity<ResponseForgot> forgotPassword(@NotNull RequestForgotPassword request ){
        try {
            String email = request.getEmail();
            String username = request.getUsername();

            if(email == null && username == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                        .body(ResponseForgot.builder()
                        .success(null)
                        .error("Either a username or email is required to restore your account.")
                        .build());
            }

            if(email != null) {
                Optional<UserEntity> user = repositoryUser.findByEmail(email);

                if(user.isPresent()) {
                    log.info("Forgot password email sent");
                    //                        emailAuth.sendForgotPasswordEmail(email);

                    return ResponseEntity.ok()
                            .body(new ResponseForgot(user.get().getEmail()));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                            .body(ResponseForgot.builder()
                            .success(null)
                            .error("This email is not in use check your inbox.")
                            .build());
                }
            }

            boolean userExist = repositoryUser.existsByUsername(username);

            if(userExist) {
                Optional<UserEntity> user = repositoryUser.findByUsername(username);
                log.info("user exist");
                if (user.isPresent()) {
                    log.info("Forgot password email sent");
//                       emailAuth.sendForgotPasswordEmail(user.get().getEmail());

                    return ResponseEntity.ok()
                            .body(new ResponseForgot(user.get().getEmail()));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                            .body(ResponseForgot.builder()
                            .success(null)
                            .error("This user could not be found. Please provide your email.")
                            .build());
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                        .body(ResponseForgot.builder()
                        .success(null)
                        .error("This username is not in use. Please provide your email.")
                        .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(ResponseForgot.builder()
                    .success(null)
                    .error("Internal server error")
                    .build());
        }
    }
}