package tech.orbfin.api.gateway.authentication.filters;

import tech.orbfin.api.gateway.services.ServiceToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;

import reactor.core.publisher.Mono;

@Slf4j
@Order(-1)
@RequiredArgsConstructor
@Component
public class FilterAuth implements GatewayFilterFactory<FilterAuth.Config> {
    private final FilterToken filterToken;

    @Override
    public GatewayFilter apply(Config config) {
        log.info("Gateway Filter Apply");
        return (exchange, chain) -> Mono.fromRunnable(() -> {
            System.out.println("FilterAuth is being applied.");

            filterToken.filter(exchange, chain);
        });
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    public static class Config {
        // Configuration properties can be added here if needed
    }
}