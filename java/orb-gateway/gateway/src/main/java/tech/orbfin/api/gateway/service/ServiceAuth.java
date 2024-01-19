package tech.orbfin.api.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tech.orbfin.api.gateway.request.RequestForgotPassword;
import tech.orbfin.api.gateway.user.User;
import tech.orbfin.api.gateway.repositories.RepositoryUser;
import tech.orbfin.api.gateway.repositories.RepositoryToken;
import tech.orbfin.api.gateway.response.ResponseAuth;
import tech.orbfin.api.gateway.request.RequestRegister;
import tech.orbfin.api.gateway.exceptions.LogoutException;
import tech.orbfin.api.gateway.request.RequestChangePassword;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;

import org.jetbrains.annotations.NotNull;

import org.springframework.stereotype.Service;

import org.springframework.http.HttpHeaders;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceAuth {
    private final RepositoryUser repositoryUser;
    private final RepositoryToken repositoryToken;
    private final ServiceToken serviceToken;
//    private final EmailAuth emailAuth;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public ResponseAuth register(@NotNull RequestRegister request) {
        log.info(request.getPassword());
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder().encode(request.getPassword()))
                .build();
        var savedUser = repositoryUser.save(user);
        String token = serviceToken.generateToken(null, savedUser);
        var refreshToken = serviceToken.refreshToken(user);

        repositoryToken.saveToken(token);

        return ResponseAuth.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }

    public ResponseAuth authenticate(@NotNull HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String tokenValue = authorizationHeader.substring(7);
            String username = serviceToken.extractUsername(tokenValue);
            Optional<User> optionalUser = repositoryUser.findByEmail(username);

            if (optionalUser.isPresent() && serviceToken.isTokenValid(tokenValue)) {
                User user = optionalUser.get();
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
                User user = this.repositoryUser.findByEmail(userEmail)
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

    private void revokeAllUserTokens(@NotNull User user) {
        var validUserTokens = repositoryToken.findAllValidTokenByUser(user.getId());

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
        Optional<User> optionalUser = repositoryUser.findByEmail(request.getEmail());
        User user = optionalUser.orElseThrow();

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