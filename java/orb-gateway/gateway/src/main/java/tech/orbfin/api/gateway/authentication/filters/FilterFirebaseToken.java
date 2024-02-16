package tech.orbfin.api.gateway.authentication.filters;

import tech.orbfin.api.gateway.repositories.RepositorySession;
import tech.orbfin.api.gateway.services.ServiceToken;
import tech.orbfin.api.gateway.services.ServiceTokenFirebase;
import tech.orbfin.api.gateway.services.ServiceUserFirebase;

import reactor.core.publisher.Mono;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Component
@AllArgsConstructor
public class FilterFirebaseToken implements GlobalFilter {
    private final ServiceToken serviceToken;
    private final ServiceTokenFirebase serviceTokenFirebase;
    private final ServiceUserFirebase serviceUserFirebase;
    private final RepositorySession repositorySession;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info(String.valueOf(exchange.getRequest().getHeaders()));
        String jwt = serviceToken.getToken(exchange);

        if (jwt == null) {
            log.info("Firebase Token could not be found in the header.");
            return chain.filter(exchange);
        }

        log.info("Validating token ...");

        FirebaseToken validatedToken = null;
        try {
            validatedToken = serviceTokenFirebase.verifyToken(jwt);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }

        if (validatedToken == null) {
            log.info("Token is not valid");

            log.info("Searching for session to use Refresh Token ......");

            String refreshToken = serviceToken.getRefreshToken(exchange);

            FirebaseToken validatedRefreshToken;
            try {
                validatedRefreshToken = serviceTokenFirebase.verifyToken(refreshToken);
            } catch (FirebaseAuthException e) {
                throw new RuntimeException(e);
            }

            if(validatedRefreshToken == null) {
                serviceToken.getRefreshTokenFromSession(jwt).doOnError(e -> {
                    throw new RuntimeException(e.getMessage(), e.getCause());
                }).subscribe();
            }

            log.info("The refresh token is valid");

            log.info("The refresh token has been used to issue a new access token.");

            log.info("Access Granted");

        } else {
            log.info("Searching for session using validated token ......");

            log.info("Session has been located.");

            log.info("Access Granted");

        }

        return Mono.empty();
    }
}

