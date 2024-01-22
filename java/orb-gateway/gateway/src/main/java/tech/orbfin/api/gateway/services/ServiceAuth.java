package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.request.RequestForgotPassword;
import tech.orbfin.api.gateway.entities.token.Token;
import tech.orbfin.api.gateway.entities.token.TokenType;
import tech.orbfin.api.gateway.entities.user.UserEntity;
import tech.orbfin.api.gateway.repositories.RepositoryUser;
import tech.orbfin.api.gateway.repositories.RepositoryToken;
import tech.orbfin.api.gateway.response.ResponseAuth;
import tech.orbfin.api.gateway.request.RequestRegister;
import tech.orbfin.api.gateway.exceptions.LogoutException;
import tech.orbfin.api.gateway.request.RequestChangePassword;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.transaction.Transactional;
import jakarta.servlet.http.HttpServletRequest;

import org.jetbrains.annotations.NotNull;

import org.springframework.stereotype.Service;

import org.springframework.http.HttpHeaders;

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
    public ResponseAuth register(@NotNull RequestRegister request) {
        try {
            var userExist = repositoryUser.existsByUsername(request.getUsername());

            if (userExist) {
                throw new Exception("This Username is currently in use.");
            }

            var emailUsed = repositoryUser.existsByEmail(request.getEmail());

            if (emailUsed) {
                throw new Exception("This Email is currently in use.");
            }

            var user = new UserEntity(request.getUsername(), passwordEncoder().encode(request.getPassword()), request.getEmail(), request.getFirstname(), request.getLastname());
            var savedUser = repositoryUser.save(user);

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", request.getLocation());

            String token = serviceToken.generateToken(extraClaims, savedUser);
            var refreshToken = serviceToken.refreshToken(user);

            var jwt = new Token(token, TokenType.BEARER, refreshToken, savedUser.getId());

            log.info(String.valueOf(jwt));
            repositoryToken.saveAndFlush(jwt);

            return new ResponseAuth(token, refreshToken, null);
        } catch (Exception e){
            System.err.println("Error while loading user by username: " + e.getMessage());

            return new ResponseAuth(null, null, e.getMessage());
        }
    }

    public ResponseAuth authenticate(@NotNull HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
            log.info("authenticate");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String tokenValue = authorizationHeader.substring(7);
            String username = serviceToken.extractUsername(tokenValue);
            Optional<UserEntity> optionalUser = repositoryUser.findByEmail(username);

            if (optionalUser.isPresent() && serviceToken.isTokenValid(tokenValue)) {
                UserEntity user = optionalUser.get();
                String newAccessToken = serviceToken.generateToken(null, user);
                String refreshToken = serviceToken.refreshToken(user);

                return ResponseAuth.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }

        return ResponseAuth.builder().build();
    }

    public ResponseAuth refreshToken(@NotNull HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String token;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Bearer Authorization Token unavailable in the header.");
        }

        token = authHeader.substring(7);

        if (serviceToken.isTokenValid(token)) {
            userEmail = serviceToken.extractUsername(token);

            if (userEmail != null) {
                UserEntity user = this.repositoryUser.findByEmail(userEmail)
                        .orElseThrow();
                revokeAllUserTokens(user);

                var accessToken = serviceToken.generateToken(null, user);

                return ResponseAuth.builder()
                        .accessToken(accessToken)
                        .refreshToken(token)
                        .build();
            }
        }
        throw new IllegalArgumentException("Invalid refresh token or user not found.");
    }

    private void revokeAllUserTokens(@NotNull UserEntity user) {
        var validUserTokens = repositoryToken.findAllValidTokenByUserid(user.getId());

        if (validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        repositoryToken.saveAll(validUserTokens);
    }

    public void forgotPassword(@NotNull RequestForgotPassword request ){
        String email = request.getEmail();
        log.info(email);
//        emailAuth.sendForgotPasswordEmail(email);
    }
    public void changePassword(@NotNull RequestChangePassword request) {
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
    }

    public void logout(@NotNull HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new LogoutException("Invalid Authorization header");
        }

        jwt = authHeader.substring(7);
        var storedToken = repositoryToken.findByToken(jwt).orElse(null);

        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            repositoryToken.save(storedToken);
            SecurityContextHolder.clearContext();
        } else {
            throw new LogoutException("Token not found or invalid");
        }
    }
}