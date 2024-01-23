package tech.orbfin.api.gateway.authorization;

import org.springframework.http.HttpStatus;
import tech.orbfin.api.gateway.entities.user.UserEntity;
import tech.orbfin.api.gateway.services.ServiceToken;
import tech.orbfin.api.gateway.services.ServiceUser;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class AuthEntryPoint implements ServerAuthenticationEntryPoint {
    private final ServiceToken serviceToken;
    private final ServiceUser serviceUser;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        try {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            log.info("Authentication entry point commence");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                boolean tokenIsValid = serviceToken.isTokenValid(jwt);

                if (tokenIsValid) {
                    log.info("Token is valid. Extracting username...");

                    String username = serviceToken.extractUsername(jwt);

                    if (username != null) {
                        log.info("Username extracted: {}", username);
                        UserEntity userEntity = serviceUser.loadUserByUsername(username);

                        log.info("Setting authentication in SecurityContextHolder...");
                        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                                userEntity,
                                null,
                                userEntity.getAuthorities()
                        );

                        SecurityContextHolder.getContext().setAuthentication(token);

                        log.info("Valid token found for user: {}", username);
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        log.warn("Failed to extract username from the token.");
                    }
                } else {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    log.warn("Invalid token found in the request.");
                }
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return Mono.empty();
            }

            return exchange.getResponse().setComplete();
        } catch (Exception e) {
            log.error("An error occurred in the authentication entry point: " + e.getMessage());
            return Mono.empty();
        }
    }
}
