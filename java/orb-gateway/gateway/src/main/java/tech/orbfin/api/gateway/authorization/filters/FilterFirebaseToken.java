package tech.orbfin.api.gateway.authorization.filters;

import org.springframework.http.HttpStatus;
import tech.orbfin.api.gateway.repositories.RepositorySession;
import tech.orbfin.api.gateway.services.ServiceToken;
import tech.orbfin.api.gateway.services.ServiceTokenFirebase;
import tech.orbfin.api.gateway.services.ServiceUserFirebase;

import java.util.Map;

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
        log.info("Filter Firebase Token is being used");

        log.info("Searching for session......");

        Object jwt = serviceToken.getToken(exchange);

        if (jwt == null) {
            log.info("Firebase Token could not be found in the header.");
            return chain.filter(exchange);
        }

        log.info("Session has been located.");
        serviceToken.getRefreshToken(jwt).doOnError(e -> {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }).subscribe();

//        if(refreshToken == null){
//            log.info("Could not find a Refresh Token.");
//        }


//        log.info("Token has expired");
//        log.info("The refresh token is valid");
//        log.info("The refresh token has been used to issue a new access token.");
//        log.info("Access Granted");

        return Mono.empty();
    }
}

