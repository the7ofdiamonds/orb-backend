package tech.orbfin.api.gateway.services.authentication.filters;

import tech.orbfin.api.gateway.services.session.ServiceSession;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.web.server.ServerWebExchange;

@RequiredArgsConstructor
@Slf4j
@Component
public class FilterToken implements GlobalFilter {
    private final ServiceSession serviceSession;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        boolean sessionValid = serviceSession.validateSession(exchange);

        if (!sessionValid) {
            log.info("Session is not valid.");
            return chain.filter(exchange);
        }

        return chain.filter(exchange);
    }
}
