package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.entities.Session;
import tech.orbfin.api.gateway.repositories.RepositorySession;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class ServiceToken {
private final RepositorySession repositorySession;

    public String getToken(ServerWebExchange exchange){
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            return authHeader.substring(7);
        }

        return null;
    }

    public Mono<Object> getRefreshToken(Object jwt) {
        log.info("Get Refresh Token");
        return repositorySession.findByToken(jwt)
                .flatMap(session -> {
                    Object refreshToken = session.getRefreshToken();
                    return Mono.justOrEmpty(refreshToken); // Use Mono.justOrEmpty for null check
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Session not found for token: " + jwt);
                    return Mono.empty();
                }));
    }

}
