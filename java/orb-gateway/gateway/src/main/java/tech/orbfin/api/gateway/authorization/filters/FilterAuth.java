package tech.orbfin.api.gateway.authorization.filters;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import tech.orbfin.api.gateway.services.ServiceToken;

@Slf4j
@Order(-1)
@AllArgsConstructor
@Component
public class FilterAuth implements GatewayFilterFactory<FilterAuth.Config> {
    private final ServiceToken serviceToken;
    private final FilterJWT filterJWT;
    private final FilterFirebaseToken filterFirebaseToken;

    @Override
    public GatewayFilter apply(Config config) {
        log.info("Gateway Filter Apply");
        return (exchange, chain) -> Mono.fromRunnable(() -> {
            System.out.println("FilterAuth is being applied.");
            String token = serviceToken.getToken(exchange);

            if (token == null) {
                Mono.empty();
            }

            String header = ServiceToken.getTokenHeader(token);
            String algo = ServiceToken.getTokenAlgo(header);

            if (algo.equals("HS256")){
                filterJWT.filter(exchange, chain);
            }

            if (algo.equals("RS256")) {
                log.info("Filter Firebase Token is being used");
                filterFirebaseToken.filter(exchange, chain);
            }
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