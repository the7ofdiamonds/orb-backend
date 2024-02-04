package tech.orbfin.api.gateway.services;

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

    public String getRefreshToken(ServerWebExchange exchange){
        String refreshToken = exchange.getRequest().getHeaders().getFirst("Refresh-Token");

        if(refreshToken != null){
            return refreshToken;
        }

        return null;
    }

    public Mono<Object> getRefreshTokenFromSession(Object jwt) {
        log.info("Get Refresh Token");

        return repositorySession.findByToken(jwt)
                .flatMap(session -> Mono.justOrEmpty(session.getRefreshToken()));
    }



}
