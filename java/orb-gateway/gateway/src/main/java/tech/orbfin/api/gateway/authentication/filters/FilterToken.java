package tech.orbfin.api.gateway.authentication.filters;

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

@RequiredArgsConstructor
@Slf4j
@Component
public class FilterToken implements GlobalFilter {
    private final ServiceSession serviceSession;
    private final ServiceToken serviceToken;
    private final ServiceTokenJW serviceTokenJW;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String accessToken = serviceToken.getToken(exchange);
        String refreshToken = serviceToken.getRefreshToken(exchange);
        String username = serviceTokenJW.extractUsername(accessToken);

        serviceSession.createSession(username, accessToken, refreshToken);
        return chain.filter(exchange);
    }
}
