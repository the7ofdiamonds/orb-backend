package tech.orbfin.api.gateway.services;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.transaction.Transactional;

import org.jetbrains.annotations.NotNull;

import org.springframework.stereotype.Service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceAuth {
    private final RepositoryUser repositoryUser;
    private final RepositoryToken repositoryToken;
    private final ServiceToken serviceToken;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Transactional
    public ResponseRegister register(@NotNull RequestRegister request) {
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

            String success = "You have been successfully signed up and logged in as " + username;

            return ResponseRegister.builder()
                    .success(success)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .error(null)
                    .build();
        } catch (Exception e){
            System.err.println("Error while loading user by username: " + e.getMessage());

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
            var username = request.getUsername();
            var password = request.getPassword();

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            SecurityContextHolder.getContext().setAuthentication(token);

            Optional<UserEntity> userEntity = repositoryUser.findByUsername(username);

            if (userEntity.isEmpty()) {
                throw new UsernameNotFoundException("The username " + username + " can not be found.");
            }

            var user = userEntity.get();
//       Add location plus other details to extra claims
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", "test location");

            String accessToken = serviceToken.generateToken(extraClaims, user);
            String refreshToken = serviceToken.refreshToken(user);

            String success = "You have been successfully logged in as " + username;

            return ResponseLogin.builder()
                    .success(success)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .error(null)
                    .build();
        } catch(Exception e) {
            return ResponseLogin.builder()
                    .success(null)
                    .accessToken(null)
                    .refreshToken(null)
                    .error(e.getMessage())
                    .build();
        }
    }
    
    public ResponseChange changePassword(@NotNull RequestChangePassword request) {
        try {
            Optional<UserEntity> optionalUser = repositoryUser.findByEmail(request.getEmail());
            UserEntity user = optionalUser.orElseThrow();

            if (!passwordEncoder().matches(request.getPassword(), user.getPassword())) {
                throw new IllegalStateException("Wrong password");
            }

            if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
                throw new IllegalStateException("Password are not the same");
            }

            user.setPassword(passwordEncoder().encode(request.getNewPassword()));

            repositoryUser.save(user);

            String success = "Your password has been changed.";

            return ResponseChange.builder()
                    .success(success)
                    .error(null)
                    .build();
        } catch (Exception e) {
            return ResponseChange.builder()
                    .success(null)
                    .error(e.getMessage())
                    .build();
        }
    }

    @Transactional
    public ResponseLogout logout(@NotNull RequestLogout request) {
        log.info("service auth logout");
        try {
            String username = serviceToken.extractUsername(request.getToken());
            Optional<UserEntity> user = repositoryUser.findByUsername(username);

            if (user.isEmpty()) {
                throw new UsernameNotFoundException("The username " + username + " can not be found.");
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

            return ResponseLogout.builder()
                    .error(null)
                    .username(username)
                    .build();
        } catch (Exception e){
            return ResponseLogout.builder()
                    .error(e.getMessage())
                    .build();
        }
    }

    public ResponseForgot forgotPassword(@NotNull RequestForgotPassword request ){
        try {
            String email = request.getEmail();
            String username = request.getUsername();

            if(email.isEmpty() && username.isEmpty()){
                throw new Exception("Either a username or email is required to restore your account.");
            }

            if(!username.isEmpty()){
               boolean userExist = repositoryUser.existsByUsername(username);

               if(userExist){
                   Optional<UserEntity> user = repositoryUser.findByUsername(username);

                   if(user.isPresent()) {
                       log.info("Forgot password email sent");
//                       emailAuth.sendForgotPasswordEmail(user.get().getEmail());
                   }
               }
            }

            if(!email.isEmpty()) {
//                        emailAuth.sendForgotPasswordEmail(email);
                log.info("Forgot password email sent");
            }

            String success = "Check your email at " + email + " for a link to change your password.";

            return ResponseForgot.builder()
                    .success(success)
                    .error(null)
                    .build();
        } catch (Exception e) {
            return ResponseForgot.builder()
                    .success(null)
                    .error(e.getMessage())
                    .build();
        }
    }
}