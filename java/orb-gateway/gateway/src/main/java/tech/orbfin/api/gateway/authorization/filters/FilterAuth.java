package tech.orbfin.api.gateway.authorization.filters;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Slf4j
@Order(-1)
@Component
@AllArgsConstructor
public class FilterAuth implements GatewayFilterFactory<FilterAuth.Config> {
    private final FilterJWT filterJWT;

    @Override
    public GatewayFilter apply(Config config) {
        log.info("GatewayFilter");
        return (exchange, chain) -> Mono.fromRunnable(() -> {
            System.out.println("FilterAuth is being applied.");
                filterJWT.filter(exchange, chain);
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