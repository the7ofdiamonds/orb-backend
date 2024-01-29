package tech.orbfin.api.gateway.authorization.filters;

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

        String jwt = serviceToken.getToken(exchange);

        if (jwt == null) {
            log.info("Firebase Token could not be found in the header.");
            return chain.filter(exchange);
        }

        log.info("Seaching for session......");

        return repositorySession.findByToken(jwt)
                .flatMap(session -> {
                    try {
                        var refreshToken = session.getRefreshToken();
                        log.info("Result after processing refreshToken: {}", refreshToken);
                        var newToken = serviceTokenFirebase.refreshToken((String) refreshToken);
                        session.setToken(newToken);
                       return repositorySession.updateSession(session);
                    } catch (FirebaseAuthException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(refreshToken -> {
                    log.info("Refresh Token: {}", refreshToken);
                    FirebaseToken validToken = null;

                    try {
                        validToken = serviceTokenFirebase.verifyToken(jwt);
                    } catch (FirebaseAuthException e) {
                        throw new RuntimeException(e);
                    }

                    if (validToken == null) {
                        log.info("Token is not valid");

                        log.info("Using Refresh Token");

//                        return Mono.empty();
                    }

                    String uid = validToken.getUid();
                    Map<String, Object> claims = validToken.getClaims();

                    log.info("UID: {}", uid);

                    try {
                        log.info("Email: {}", serviceUserFirebase.getUser(uid).getEmail());
                    } catch (FirebaseAuthException e) {
                        throw new RuntimeException(e);
                    }

                    log.info("Claims: {}", claims.toString());

                    // Your authentication logic here

                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    log.error("Error in filter", e);
                    return chain.filter(exchange);
                });
    }
}
