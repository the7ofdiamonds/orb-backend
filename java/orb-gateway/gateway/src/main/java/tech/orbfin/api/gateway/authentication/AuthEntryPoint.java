package tech.orbfin.api.gateway.authentication;

import tech.orbfin.api.gateway.services.*;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import org.springframework.web.server.ServerWebExchange;

import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.server.ServerAuthenticationEntryPoint;

@Slf4j
@RequiredArgsConstructor
@Setter
@Getter
@Component
public class AuthEntryPoint implements ServerAuthenticationEntryPoint {
    private final ServiceSession serviceSession;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        try {
            log.info("Authentication entry point commence");

            boolean sessionValid = serviceSession.validateSession(exchange);

            if (!sessionValid) {
                log.info("Session is not valid.");
                return Mono.empty();
            }

            return exchange.getResponse().setComplete();
        } catch (Exception e) {
            log.error("An error occurred in the authentication entry point: " + e.getMessage());
            return Mono.empty();
        }
    }
}
