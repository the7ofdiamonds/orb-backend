package tech.orbfin.api.gateway.authentication.filters;

import org.springframework.security.core.userdetails.UserDetails;
import tech.orbfin.api.gateway.model.session.IRepositorySession;
import tech.orbfin.api.gateway.model.session.Session;
import tech.orbfin.api.gateway.services.ServiceSession;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;
import tech.orbfin.api.gateway.services.ServiceToken;
import tech.orbfin.api.gateway.services.ServiceTokenJW;
import tech.orbfin.api.gateway.services.ServiceUserDetails;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class FilterToken implements GlobalFilter {
    private final ServiceSession serviceSession;
    private final ServiceUserDetails serviceUserDetails;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String accessToken = ServiceToken.getToken(exchange);

        if (accessToken == null) {
            return chain.filter(exchange);
        }

        String refreshToken = ServiceToken.getRefreshToken(exchange);

        if (refreshToken == null) {
            return chain.filter(exchange);
        }

        String username = ServiceTokenJW.extractUsername(accessToken);

        if (username == null) {
            return chain.filter(exchange);
        }

        boolean tokenIsExpired = ServiceTokenJW.isTokenExpired(accessToken);

        if (!tokenIsExpired) {
            log.info("Access Token is expired");
            log.info("Searching for session to use Refresh Token ......");

            boolean sessionValid = serviceSession.validateSession(refreshToken);

            if (!sessionValid) {
                log.info("Session is not valid.");
                return chain.filter(exchange);
            }

            var longitude = "here";
            var latitude = "there";

            Map<String, String> location = new HashMap<>();
            location.put("longitude", longitude);
            location.put("latitude", latitude);

            Map<String, Object> extraClaims = new HashMap<>();

            extraClaims.put("location", location);

            UserDetails user = serviceUserDetails.loadUserByUsername(username);
            accessToken = ServiceTokenJW.generateToken(extraClaims, user);

            serviceSession.createSession(username, accessToken, refreshToken);

            log.info("New Access Token issued to username: {}", username);

            return chain.filter(exchange);
        }

        log.info("Valid token found for username: {}", username);

        return chain.filter(exchange);
    }
}
